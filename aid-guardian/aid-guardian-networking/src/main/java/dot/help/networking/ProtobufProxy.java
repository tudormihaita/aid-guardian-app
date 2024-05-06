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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProtobufProxy implements IServices {
    private final String host;
    private final int port;
    private InputStream input;
    private OutputStream output;
    private Socket connection;

    private IObserver client;
    private BlockingQueue<Protobufs.Response> qresponses;
    private volatile boolean finished;
    private final Logger log = LogManager.getLogger(ProtobufProxy.class);

    public ProtobufProxy(String host, int port) {
        log.info("Initializing ProtobufProxy with host and port {} {}", host, port);
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    public void setClient(IObserver client) {
        this.client = client;
    }

    private boolean isUpdate(Protobufs.Response.Type type) {
        return type == Protobufs.Response.Type.EMERGENCY_RESPONDED || type == Protobufs.Response.Type.EMERGENCY_REPORTED;
    }

    private void handleUpdate(Protobufs.Response response) {
        if (response.getType() == Protobufs.Response.Type.EMERGENCY_REPORTED) {
            try {
                Emergency emergency = ProtoUtils.getEmergency(response);
                client.emergencyReported(emergency);
            } catch (Exception exception) {
                log.error(exception);
                throw new RuntimeException(exception.getMessage());
            }
        } else if (response.getType() == Protobufs.Response.Type.EMERGENCY_RESPONDED) {
            try {
//                FirstResponder responder = (FirstResponder) ProtoUtils.getUser(response);
                Emergency respondedEmergency = ProtoUtils.getEmergency(response);
                client.emergencyResponded(respondedEmergency);
            } catch (Exception exception) {
                log.error(exception);
                throw new RuntimeException(exception.getMessage());
            }
        } else {
            log.error("Unknown update type received: {}", response.getType());
            throw new RuntimeException("Unknown update type received!");
        }

    }

    @Override
    public void registerUser(Profile profile, IObserver client) {
        log.info("Sending register user request...");

        try {
            sendRequest(ProtoUtils.createSaveProfileRequest(profile));
            Protobufs.Response response = readResponse();

            if (response.getType() == Protobufs.Response.Type.ERROR) {
                String err = response.getError();
                throw new Exception(err);
            }
        } catch (Exception exception) {
            log.error(exception);
        }
    }

    @Override
    public User logIn(String credential, String password, IObserver client) {
        initializeConnection();
        log.traceEntry("Sending login request ...");

        try {
            User user = new User(credential, password);
            sendRequest(ProtoUtils.createLoginRequest(user));

            Protobufs.Response response = readResponse();
            if(response.getType() == Protobufs.Response.Type.OK) {
                User loggedUser = ProtoUtils.getUser(response);
                log.traceExit("Client successfully logged in: {}", client);
                setClient(client);
                return loggedUser;
            }
        } catch (Exception exception) {
            closeConnection();
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

            if (response.getType() == Protobufs.Response.Type.ERROR) {
                String err = response.getError();
                throw new Exception(err);
            }
        } catch (Exception exception) {
            log.error(exception);
        }

        closeConnection();
        log.traceExit("Client successfully logged out: {}", user);
    }

    @Override
    public Profile findUserProfile(User user, IObserver client) {
        log.traceEntry("Sending get profile request...");

        try {
            sendRequest(ProtoUtils.createGetProfileRequest(user));
            Protobufs.Response response = readResponse();

            if (response.getType() == Protobufs.Response.Type.OK) {
                Profile userProfile = ProtoUtils.getProfile(response);
                userProfile.setUser(user);
                log.traceExit("Profile found: {}", userProfile);
                return userProfile;
            }
        } catch (Exception exception) {
            log.error(exception);
        }

        log.error("Failed to process get profile request, aborting...");
        throw new IllegalArgumentException("Failed to get profile!");
    }

    @Override
    public void reportEmergency(Emergency emergency, IObserver client) {
        log.info("Sending emergency report request...");

        try {
            sendRequest(ProtoUtils.createReportEmergencyRequest(emergency));
            Protobufs.Response response = readResponse();

            if (response.getType() == Protobufs.Response.Type.ERROR) {
                String err = response.getError();
                throw new Exception(err);
            }
        } catch (Exception exception) {
            log.error(exception);
        }
    }

    @Override
    public void respondToEmergency(FirstResponder responder, Emergency emergency, IObserver client) {
        log.info("Sending emergency response request...");

        try {
            sendRequest(ProtoUtils.createRespondToEmergencyRequest(responder, emergency));
            Protobufs.Response response = readResponse();

            if (response.getType() == Protobufs.Response.Type.ERROR) {
                String err = response.getError();
                throw new Exception(err);
            }
        } catch (Exception exception) {
            log.error(exception);
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
            setClient(null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRequest(Protobufs.Request req) throws Exception {
        try{
            req.writeDelimitedTo(output);
            output.flush();

        } catch (IOException e) {
            throw new Exception("Error sending request " + e);
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
                    System.out.println("response received " + response);

                    if (isUpdate(response.getType())){
                        handleUpdate(response);
                    }
                    else {
                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                           log.error(e);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
