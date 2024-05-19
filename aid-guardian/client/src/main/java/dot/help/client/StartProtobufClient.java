package dot.help.client;

import dot.help.client.controller.StartAppController;
import dot.help.networking.ProtobufProxy;
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

public class StartProtobufClient extends Application {
    private static int DEFAULT_PORT = 55555;
    private static String DEFAULT_SERVER = "localhost";
    private static final Logger logger = LogManager.getLogger(StartProtobufClient.class);


    public void start(Stage primaryStage) throws Exception {

        Properties clientProps=new Properties();
        try {
            clientProps.load(StartProtobufClient.class.getResourceAsStream("/client.properties"));
            logger.info("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find client.properties "+e);
            return;
        }
        String serverIP=clientProps.getProperty("project.server.host", DEFAULT_SERVER);
        int serverPort= DEFAULT_PORT;
        try{
            serverPort=Integer.parseInt(clientProps.getProperty("project.server.port"));
        }catch(NumberFormatException ex){
            System.err.println("Wrong port number "+ex.getMessage());
            logger.info("Using default port: "+ DEFAULT_PORT);
        }
        logger.info("Using server IP " + serverIP);
        logger.info("Using server port " + serverPort);

        IServices server = new ProtobufProxy(serverIP, serverPort);

        FXMLLoader applicationLoader = new FXMLLoader();
        applicationLoader.setLocation(StartProtobufClient.class.getResource("StartApp-view.fxml"));
        TabPane ApplicationLayout = applicationLoader.load();
        primaryStage.setScene(new Scene(ApplicationLayout));

        StartAppController controller = applicationLoader.getController();
        controller.setServer(server);
        controller.setStage(primaryStage);

        primaryStage.setWidth(875);
        primaryStage.setHeight(620);
        primaryStage.setTitle("Aid Guardian - Welcome");
        primaryStage.show();

    }
}