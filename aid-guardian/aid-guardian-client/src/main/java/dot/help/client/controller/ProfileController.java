package dot.help.client.controller;

import dot.help.client.StartProtoBufferClient;
import dot.help.model.CommunityDispatcher;
import dot.help.model.Emergency;
import dot.help.model.Profile;
import dot.help.services.IObserver;
import dot.help.services.IServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable, IObserver {
    private static final Logger logger= LogManager.getLogger(ProfileController.class);
    @FXML
    public Label UsernameLabel;
    @FXML
    public Label TypeOfUserLabel;


    private Profile currentProfile;

    private IServices server;
    private TabPane appTabPane;
    private Tab startTab;
    private Tab profileTab;


    public void setServer(IServices sev) {
        logger.traceEntry("Entering setService");
        this.server = sev;
        logger.traceExit();
    }

    public void setTab(TabPane mainTabPane, Tab startTab, Tab profileTab) {
        logger.traceEntry("Entering setTab");
        this.appTabPane = mainTabPane;
        this.startTab = startTab;
        this.profileTab = profileTab;
        logger.traceExit();
    }

    public void setUser(Profile profile) {
        this.currentProfile = profile;
        UsernameLabel.setText(profile.getUser().getUsername());
        if (profile.getUser() instanceof CommunityDispatcher) {
            TypeOfUserLabel.setText("CommunityDispatcher");
        }
        else {
            TypeOfUserLabel.setText("FirstResponder");
        }
    }


    @Override
    public void emergencyReported(Emergency emergency) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleReportEmergencyButton(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleReportEmergencyButton");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtoBufferClient.class.getResource("ReportEmergency-view.fxml"));
            AnchorPane root = loader.load();

            Tab reportEmergencyTab = new Tab("Report Emergency");
            reportEmergencyTab.setContent(root);
            appTabPane.getTabs().add(reportEmergencyTab);
            appTabPane.getSelectionModel().select(reportEmergencyTab);

            ProfileController profileController= loader.getController();
            profileController.setServer(server);
            profileController.setTab(appTabPane, profileTab, reportEmergencyTab);
            profileController.setUser(currentProfile);

            appTabPane.getTabs().remove(profileTab);
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.traceExit();
    }

    public void handleRespondToEmergencyButton(ActionEvent actionEvent) {
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "Emergency response confirmed. Your assistance is greatly appreciated.");
    }

    public void handleLogoutButton(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleLogoutButton");
        try {
            server.logOut(currentProfile.getUser(), this);
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        appTabPane.getTabs().add(startTab);
        appTabPane.getSelectionModel().select(startTab);
        logger.traceExit();
    }

    public void handleSettingsButton(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleSettingsButton");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtoBufferClient.class.getResource("Settings-view.fxml"));
            AnchorPane root = loader.load();

            Tab reportEmergencyTab = new Tab("Settings");
            reportEmergencyTab.setContent(root);
            appTabPane.getTabs().add(reportEmergencyTab);
            appTabPane.getSelectionModel().select(reportEmergencyTab);

            SettingsController settingsController= loader.getController();
            settingsController.setServer(server);
            settingsController.setTab(appTabPane, profileTab, reportEmergencyTab);
            settingsController.setUser(currentProfile);

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.traceExit();
    }

    public void handleYourScoreButton(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleYourScoreButton");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtoBufferClient.class.getResource("Score-view.fxml"));
            AnchorPane root = loader.load();

            Tab newTab = new Tab("Report Emergency");
            reportEmergencyTab.setContent(root);
            appTabPane.getTabs().add(newTab);
            appTabPane.getSelectionModel().select(newTab);

            ScoreController scoreController= loader.getController();
            scoreController.setServer(server);
            scoreController.setTab(appTabPane, profileTab, newTab);
            scoreController.setUser(currentProfile);

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.traceExit();
    }

    public void handleCall112Button(ActionEvent actionEvent) {
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Emergency", "Emergency response confirmed. Your assistance is greatly appreciated.");
    }

    public void handleGuidesButton(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleGuidesButton");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtoBufferClient.class.getResource("Guides-view.fxml"));
            AnchorPane root = loader.load();

            Tab newTab = new Tab("Report Emergency");
            newTab.setContent(root);
            appTabPane.getTabs().add(newTab);
            appTabPane.getSelectionModel().select(newTab);

            GuidesController guidesController= loader.getController();
            guidesController.setServer(server);
            guidesController.setTab(appTabPane, profileTab, newTab);
            guidesController.setUser(currentProfile);

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.traceExit();
    }
}
