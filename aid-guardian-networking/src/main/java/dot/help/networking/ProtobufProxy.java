package dot.help.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ProtobufProxy {
    private String host;
    private int port;
    private InputStream input;
    private OutputStream output;
    private Socket connection;

    private BlockingQueue<Protobufs.Response> qresponses;
    private volatile boolean finished;

    public ProtobufProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<>();
    }
    public void Login(User user) throws Exception {
        initializeConnection();
        System.out.println("Login request ...");
        sendRequest(ProtoUtils.createLoginRequest(user));
        Protobufs.Response response=readResponse();
        if (response.getType()==Protobufs.Response.Type.Ok){
//            this.client=client;
            return;
        }

        if (response.getType()==Protobufs.Response.Type.Error){
            String errorText=ProtoUtils.getError(response);
            closeConnection();
            throw new Exception(errorText);
        }

    }
    private Protobufs.Response readResponse() {
        Protobufs.Response response = null;
        try{
            response = qresponses.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }
    private void closeConnection() {
        finished = true;
        try{
            input.close();
            output.close();
            connection.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void sendRequest(Protobufs.Request req) throws Exception {
        try{
            req.writeDelimitedTo(output);
            output.flush();

        } catch (IOException e) {
            throw new Exception("Error sendind request " + e);
        }
    }
    private void initializeConnection() {
        try{
            connection = new Socket(host, port);
            output = connection.getOutputStream();
            output.flush();
            input = connection.getInputStream();
            finished = false;
            startReader();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Protobufs.Response response=Protobufs.Response.parseDelimitedFrom(input);
                    System.out.println("response received "+response);

//                    if (isUpdate(response)){
//                        handleUpdate(response);
//                    }else{
                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
