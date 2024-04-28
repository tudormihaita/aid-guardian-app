package dot.help.client.controller;

import dot.help.client.StartProtobufClient;
import dot.help.client.events.EmergencyAlert;
import dot.help.client.events.MessageAlert;
import dot.help.model.*;
import dot.help.services.IObserver;
import dot.help.services.IServices;
import javafx.application.Platform;
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
        if(currentProfile.getUser() instanceof FirstResponder responder) {


            Platform.runLater(() -> {
                EmergencyAlert.showMessage(null, Alert.AlertType.WARNING, "Emergency reported",
                        "An emergency has been reported in your area at " + emergency.getLocation() + ". Do you want to help?",
                        (acceptEvent) -> {
                            server.respondToEmergency(responder, emergency, this);
                            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Emergency responded", "You have successfully responded to the emergency." +
                                    " Please proceed to the provided location to offer assistance!");
                        },
                        (cancelEvent) -> {
                            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Emergency ignored", "You have ignored the emergency. You may continue with your current activities.");
                        });
            });
        }
    }

    @Override
    public void emergencyResponded(Emergency emergency) {
        if(currentProfile.getUser().equals(emergency.getReporter())) {

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleReportEmergencyButton(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleReportEmergencyButton");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtobufClient.class.getResource("ReportEmergency-view.fxml"));
            AnchorPane root = loader.load();

            Tab reportEmergencyTab = new Tab("Report Emergency");
            reportEmergencyTab.setContent(root);
            appTabPane.getTabs().add(reportEmergencyTab);
            appTabPane.getSelectionModel().select(reportEmergencyTab);

            ReportEmergencyController emergencyController= loader.getController();
            emergencyController.setServer(server);
            emergencyController.setTab(appTabPane, profileTab, reportEmergencyTab);
            emergencyController.setUser(currentProfile);
            emergencyController.setProfileController(this);

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

    public void handleLogOut(ActionEvent actionEvent) {
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

    public void handleSettings(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleSettingsButton");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtobufClient.class.getResource("Settings-view.fxml"));
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

    public void handleYourScore(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleYourScoreButton");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtobufClient.class.getResource("Score-view.fxml"));
            AnchorPane root = loader.load();

            Tab scoreTab = new Tab("Report Emergency");
            scoreTab.setContent(root);
            appTabPane.getTabs().add(scoreTab);
            appTabPane.getSelectionModel().select(scoreTab);

            ScoreController scoreController= loader.getController();
            scoreController.setServer(server);
            scoreController.setTab(appTabPane, profileTab, scoreTab);
            scoreController.setUser(currentProfile);

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.traceExit();
    }

    public void handleCallEmergencyNumber(ActionEvent actionEvent) {
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Emergency", "Emergency response confirmed. Your assistance is greatly appreciated.");
    }

    public void handleGuides(ActionEvent actionEvent) {
        logger.traceEntry("Entering handleGuidesButton");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartProtobufClient.class.getResource("Guides-view.fxml"));
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
