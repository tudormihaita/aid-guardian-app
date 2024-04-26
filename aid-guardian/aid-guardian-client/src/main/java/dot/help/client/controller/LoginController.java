package dot.help.client.controller;

import dot.help.client.StartProtoBufferClient;
import dot.help.model.Profile;
import dot.help.model.User;
import dot.help.services.IServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LoginController {
    private User currentUser;
    private static final Logger log = LogManager.getLogger(LoginController.class);

    @FXML
    public TextField usernameEmailTextField;
    @FXML
    public PasswordField passwordTextField;

    private IServices server;
    private TabPane appTabPane;
    private Tab startTab;
    private Tab loginTab;


    public void setServer(IServices sev) {
        log.traceEntry("Entering setService");
        this.server = sev;
        log.traceExit();
    }

    public void setTab(TabPane mainTabPane, Tab startTab, Tab loginTab) {
        log.traceEntry("Entering setTab");
        this.appTabPane = mainTabPane;
        this.startTab = startTab;
        this.loginTab = loginTab;
        log.traceExit();
    }

    @FXML
    private void initialize() {
        log.traceEntry("Entering initialize");
        log.traceExit();
    }

    public void handleLogin() {
        log.traceEntry("Entering handleLogin");

        String candidateCredential = usernameEmailTextField.getText();
        String candidatePassword = passwordTextField.getText();

        try {
            currentUser = server.logIn(candidateCredential, candidatePassword, profileController);
            Profile profile = server.findProfileByUser(currentUser);
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

        log.traceExit();
    }

    public void handleBackButton(ActionEvent actionEvent) {
        log.traceEntry("Entering handleBackButton");
        appTabPane.getTabs().add(startTab);
        appTabPane.getSelectionModel().select(startTab);
        appTabPane.getTabs().remove(loginTab);
        log.traceExit();
    }
}
