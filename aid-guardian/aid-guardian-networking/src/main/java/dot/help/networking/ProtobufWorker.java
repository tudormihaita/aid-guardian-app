package dot.help.networking;

import dot.help.model.Emergency;
import dot.help.model.FirstResponder;
import dot.help.model.Profile;
import dot.help.model.User;
import dot.help.services.IObserver;
import dot.help.services.IServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ProtobufWorker implements Runnable, IObserver {

    private IServices service;
    private Socket connection;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private volatile boolean connected;

    private final Logger log = LogManager.getLogger(ProtobufWorker.class);


    public ProtobufWorker(IServices service, Socket connection)
    {
        log.info("Initializing ProtobufWorker with service and connection {} {}", service, connection);
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
    private void sendResponse(Protobufs.Response response) {
        System.out.println("Sending response" + response.getType());
        synchronized ( output ){
            try {
                response.writeDelimitedTo(output);
                output.flush();
            } catch (IOException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }
    }
    private Protobufs.Response handleRequest(Protobufs.Request request) {

        if(request.getType() == Protobufs.Request.Type.LOGIN)
        {
            log.info("Login request" + request.getType());
            User user = ProtoUtils.getUser(request);
            try{
                User loggedUser = service.logIn(user.getUsername(), user.getPassword(), this);
                return ProtoUtils.createLoginResponse(loggedUser);
            } catch (Exception e) {
                connected = false;
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if(request.getType() == Protobufs.Request.Type.LOGOUT)
        {
            log.info("Logout request" +  request.getType());
            User user = ProtoUtils.getUser(request);
            try{
                service.logOut(user, this);
                connected = false;
                return ProtoUtils.createLogoutResponse();
            } catch (Exception e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if(request.getType() == Protobufs.Request.Type.GET_PROFILE)
        {
            log.info("Get profile request" + request.getType());
            User user = ProtoUtils.getUser(request);
            try {
                Profile profile = service.findUserProfile(user, this);
                return ProtoUtils.createProfileResponse(profile);
            } catch (Exception e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if(request.getType() == Protobufs.Request.Type.REPORT_EMERGENCY)
        {
            log.info("Report emergency request" + request.getType());
            Emergency emergency = ProtoUtils.getEmergency(request);
            try {
                service.reportEmergency(emergency, this);
                return ProtoUtils.createReportEmergencyResponse(emergency);
            } catch (Exception e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if(request.getType() == Protobufs.Request.Type.RESPOND_EMERGENCY)
        {
            log.info("Respond emergency request" + request.getType());
            Emergency emergency = ProtoUtils.getEmergency(request);
            FirstResponder responder = (FirstResponder) ProtoUtils.getUser(request);
            try {
                service.respondToEmergency(responder, emergency, this);
                return ProtoUtils.createRespondToEmergencyResponse(emergency);
            } catch (Exception e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }

        log.error("Invalid request" + request.getType());
        return ProtoUtils.createErrorResponse("Invalid request received");
    }

    @Override
    public void emergencyReported(Emergency emergency) {
        log.info("Announcing the reported emergency...");
        sendResponse(ProtoUtils.createEmergencyReportedResponse(emergency));
    }

    @Override
    public void emergencyResponded(Emergency emergency) {
        log.info("Announcing the responded emergency...");
        sendResponse(ProtoUtils.createEmergencyRespondedResponse(emergency));
    }

}
