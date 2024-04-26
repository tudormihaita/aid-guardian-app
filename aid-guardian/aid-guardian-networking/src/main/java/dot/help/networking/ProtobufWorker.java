package dot.help.networking;

import dot.help.model.Emergency;
import dot.help.model.User;
import dot.help.services.IObserver;
import dot.help.services.IServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ProtobufWorker implements Runnable, IObserver {

    private IServices service;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ProtobufWorker(IServices service, Socket connection)
    {
        this.service = service;
        this.connection = connection;
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void run() {
        while(connected) {
            try {
                Protobufs.Request request = Protobufs.Request.parseDelimitedFrom(input);
                System.out.println("reading" + request.getType());
                Protobufs.Response response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try{
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error" + e);
            throw new RuntimeException(e);
        }

    }
    private void sendResponse(Protobufs.Response response) throws IOException {
        System.out.println("Sending response" + response.getType());
        synchronized (output){
            response.writeDelimitedTo(output);
            output.flush();
        }
    }
    private Protobufs.Response handleRequest(Protobufs.Request request) {
        Protobufs.Response response = null;
        if(request.getType() == Protobufs.Request.Type.Login)
        {
            System.out.println("Login request" + request.getType());
            User user = ProtoUtils.getUser(request);
            try{
                service.logIn(user.getUsername(), user.getPassword(), this);
                return ProtoUtils.createOkResponse();
            } catch (Exception e) {
                connected = false;
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if(request.getType() == Protobufs.Request.Type.Logout)
        {
            System.out.println("Logout request" +  request.getType());
            User user = ProtoUtils.getUser(request);
            try{
                service.logOut(user, this);
                connected = false;
                return ProtoUtils.createOkResponse();
            } catch (Exception e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }

        return response;
    }

    @Override
    public void emergencyReported(Emergency emergency) {

    }
}
