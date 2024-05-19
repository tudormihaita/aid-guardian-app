package dot.help.server;

import dot.help.networking.utils.AbstractServer;
import dot.help.networking.utils.ProtobufConcurrentServer;
import dot.help.persistence.repository.EmergencyRepository;
import dot.help.persistence.repository.ProfileRepository;
import dot.help.persistence.repository.UserRepository;
import dot.help.persistence.repository.database.EmergencyDBRepository;
import dot.help.persistence.repository.database.ProfileDBRepository;
import dot.help.persistence.repository.database.UserDBRepository;
import dot.help.services.IServices;

import java.io.IOException;
import java.util.Properties;

public class StartProtobufServer {
    private static final int DEFAULT_PORT = 55555;

    public static void main(String[] args) {
        Properties serverProperties = new Properties();
        try {
            serverProperties.load(StartProtobufServer.class.getResourceAsStream("/server.properties"));
            serverProperties.list(System.out);
        } catch (IOException exception) {
            System.err.println("Cannot find server.properties: " + exception);
            return;
        }

        UserRepository userRepository = new UserDBRepository(serverProperties);
        ProfileRepository profileRepository = new ProfileDBRepository(serverProperties);
        EmergencyRepository emergencyRepository = new EmergencyDBRepository(serverProperties);

        IServices services = new ServicesImpl(userRepository, profileRepository, emergencyRepository);

        int serverPort = DEFAULT_PORT;
        try {
            serverPort = Integer.parseInt(serverProperties.getProperty("project.server.port"));
        } catch (NumberFormatException exception) {
            System.err.println("Wrong  Port Number" + exception.getMessage());
            System.err.println("Using default port " + DEFAULT_PORT);
        }

        System.out.println("Starting server on port: " + serverPort);
        AbstractServer server = new ProtobufConcurrentServer(serverPort, services);
        try {
            server.start();
        } catch (Exception exception) {
            System.err.println("Error starting the server: " + exception);
        }
    }
}
