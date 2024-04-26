package dot.help.networking;

import dot.help.model.User;
import dot.help.services.IObserver;
import dot.help.services.IServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ProtobufProxy implements IServices {
    private String host;
    private int port;
    private InputStream input;
    private OutputStream output;
    private Socket connection;


    private IObserver client;
    private BlockingQueue<Protobufs.Response> qresponses;
    private volatile boolean finished;
    private final Logger log = LogManager.getLogger(ProtobufProxy.class);

    public ProtobufProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<>();
    }

    @Override
    public User logIn(String credential, String password, IObserver client) {
        initializeConnection();
        System.out.println("sending login request ...");

        try {
            User user = new User(credential, password);
            sendRequest(ProtoUtils.createLoginRequest(user));

            Protobufs.Response response = readResponse();
            if(response.getType() == Protobufs.Response.Type.Login) {
                User loggedUser = ProtoUtils.getUser(response);
                log.traceExit("Client successfully logged in: {}", client);
                this.client = client;
                return loggedUser;
            }
        } catch (Exception exception) {
            log.error(exception);
        }

        log.error("Failed to process Login request, aborting...");
        throw new IllegalArgumentException("Failed to login!");
    }

    @Override
    public void logOut(User user, IObserver client) {
        log.info("Sending logout request...");

        try {
            sendRequest(ProtoUtils.createLogoutRequest(user));
            Protobufs.Response response = readResponse();

            if (response.getType() == Protobufs.Response.Type.Error) {
                String err = response.getError();
                throw new Exception(err);
            }
        } catch (Exception exception) {
            log.error(exception);
        }

        closeConnection();
        log.traceExit("Client successfully logged out: {}", user);
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
