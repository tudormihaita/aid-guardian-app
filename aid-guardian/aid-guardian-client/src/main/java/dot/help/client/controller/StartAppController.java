package dot.help.client.controller;

import dot.help.client.StartProtobufClient;
import dot.help.services.IServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class StartAppController {
    private static final Logger logger= LogManager.getLogger(StartAppController.class);

    @FXML
    public TabPane appTabPane;
    @FXML
    public Tab startTab;

    private IServices server;
    private Stage stage;

    public void setServer(IServices sev) {
        logger.traceEntry("Entering setService");
        this.server = sev;
        logger.traceExit();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        logger.traceEntry("Entering initialize");
    }

    public void handleLogin() {
        try {
            logger.traceEntry("Entering handleLogin");
            // create a new tab for Login
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtobufClient.class.getResource("Login-view.fxml"));
            AnchorPane root = loader.load();

            // Add a tab for entering login data.
            Tab loginTab = new Tab("Login");
            loginTab.setContent(root);
            appTabPane.getTabs().add(loginTab);
            appTabPane.getSelectionModel().select(loginTab);

            LoginController controller = loader.getController();
            controller.setServer(server);
            controller.setStage(stage);
            controller.setTab(appTabPane, startTab, loginTab);

            appTabPane.getTabs().remove(startTab);

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.traceExit();
    }

    public void handleSignUp(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleSignUp");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtobufClient.class.getResource("dot/help/client/SignUp-view.fxml"));
            AnchorPane root = loader.load();

            Tab signInTab = new Tab("Sign Up");
            signInTab.setContent(root);
            appTabPane.getTabs().add(signInTab);
            appTabPane.getSelectionModel().select(signInTab);

            SignUpController controller = loader.getController();
            controller.setServer(server);
            controller.setTab(appTabPane, startTab, signInTab);
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.traceExit();
    }
}
