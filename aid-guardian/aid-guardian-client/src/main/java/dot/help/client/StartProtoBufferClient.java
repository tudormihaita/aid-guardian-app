package dot.help.client;

import dot.help.networking.ProtobufProxy;

import dot.help.client.controller.StartAppController;
import dot.help.services.IServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class StartProtoBufferClient extends Application {
    private static int defaultChatPort=55555;
    private static String defaultServer="localhost";
    private static final Logger logger= LogManager.getLogger(StartProtoBufferClient.class);


    public void start(Stage primaryStage) throws Exception {

        Properties clientProps=new Properties();
        try {
            clientProps.load(StartProtoBufferClient.class.getResourceAsStream("/dot/help/client/client.properties"));
            logger.info("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatclient.properties "+e);
            return;
        }
        String serverIP=clientProps.getProperty("chat.server.host",defaultServer);
        int serverPort=defaultChatPort;
        try{
            serverPort=Integer.parseInt(clientProps.getProperty("chat.server.port"));
        }catch(NumberFormatException ex){
            System.err.println("Wrong port number "+ex.getMessage());
            logger.info("Using default port: "+defaultChatPort);
        }
        logger.info("Using server IP "+serverIP);
        logger.info("Using server port "+serverPort);

        IServices server = new ProtobufProxy(serverIP, serverPort);

        FXMLLoader applicationLoader = new FXMLLoader();
        applicationLoader.setLocation(StartProtoBufferClient.class.getResource("StartApp-view.fxml"));
        TabPane ApplicationLayout = applicationLoader.load();
        primaryStage.setScene(new Scene(ApplicationLayout));

        StartAppController controller = applicationLoader.getController();
        controller.setServer(server);

        primaryStage.setWidth(875);
        primaryStage.setHeight(620);
        primaryStage.setTitle("Flight Agency");
        primaryStage.show();

    }
}