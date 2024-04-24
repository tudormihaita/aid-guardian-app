package dot.help.client.Controllers;

import dot.help.client.StartProtoBufferClient;
import dot.help.model.Profile;
import dot.help.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LoginController {
    private static final Logger logger= LogManager.getLogger(LoginController.class);

    @FXML
    public TextField usernameEmailTextField;
    @FXML
    public PasswordField passwordTextField;

    private IService server;
    private TabPane appTabPane;
    private Tab startTab;
    private Tab loginTab;


    public void setServer(IService sev) {
        logger.traceEntry("Entering setService");
        this.server = sev;
        logger.traceExit();
    }

    public void setTab(TabPane mainTabPane, Tab startTab, Tab loginTab) {
        logger.traceEntry("Entering setTab");
        this.appTabPane = mainTabPane;
        this.startTab = startTab;
        this.loginTab = loginTab;
        logger.traceExit();
    }

    @FXML
    private void initialize() {
        logger.traceEntry("Entering initialize");
        logger.traceExit();
    }

    public void handleLogin() {
        logger.traceEntry("Entering handleLogin");

        String username = usernameEmailTextField.getText();
        String candidatePassword = passwordTextField.getText();
        User user = new User(username, candidatePassword);

        try {
            server.Login(user);
            Profile profile = server.findProfileByUser(User);
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(StartProtoBufferClient.class.getResource("Profile-view.fxml"));
                AnchorPane root = loader.load();

                Tab profileTab = new Tab(username + " account");
                profileTab.setContent(root);
                appTabPane.getTabs().add(profileTab);
                appTabPane.getSelectionModel().select(profileTab);

                ProfileController profileController= loader.getController();
                profileController.setServer(server);
                profileController.setTab(appTabPane, startTab, profileTab);
                profileController.setUser(profile);

                appTabPane.getTabs().remove(loginTab);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
            return;
        }

        logger.traceExit();
    }

    public void handleBackButton(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleBackButton");
        appTabPane.getTabs().add(startTab);
        appTabPane.getSelectionModel().select(startTab);
        appTabPane.getTabs().remove(loginTab);
        logger.traceExit();
    }
}
