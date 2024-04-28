package dot.help.client.controller;

import dot.help.client.StartProtobufClient;
import dot.help.client.events.MessageAlert;
import dot.help.model.Profile;
import dot.help.model.User;
import dot.help.services.IServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private Stage stage;


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

    public void setStage(Stage stage) {
        this.stage = stage;
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
            FXMLLoader profileLoader = new FXMLLoader();
            profileLoader.setLocation(StartProtobufClient.class.getResource("Profile-view.fxml"));
            AnchorPane root = profileLoader.load();
            ProfileController profileController= profileLoader.getController();

            currentUser = server.logIn(candidateCredential, candidatePassword, profileController);

            Tab profileTab = new Tab(currentUser.getUsername() + " account");
            profileController.setServer(server);
            profileController.setTab(appTabPane, startTab, profileTab);
            profileTab.setContent(root);

            Profile profile = server.findUserProfile(currentUser, profileController);
            profileController.setUser(profile);

            appTabPane.getTabs().remove(loginTab);
            stage.setOnCloseRequest(windowEvent -> {
                profileController.handleLogOut(null);
                System.exit(0);
            });

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
