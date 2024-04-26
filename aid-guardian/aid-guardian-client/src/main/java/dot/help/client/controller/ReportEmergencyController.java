package dot.help.client.controller;

import dot.help.model.*;
import dot.help.services.IObserver;
import dot.help.services.IServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class ReportEmergencyController implements IObserver {
    private static final Logger logger= LogManager.getLogger(ReportEmergencyController.class);


    @FXML
    public TextArea EmergencyDescriptionTextArea;
    @FXML
    public RadioButton useMyCurrentLlocationRadioButton;
    @FXML
    public RadioButton useAnotherLocationRadioButton;
    @FXML
    public TextField LocationTextField;


    private Profile currentProfile;

    private IServices server;
    private TabPane appTabPane;
    private Tab startTab;
    private Tab reportTab;


    public void setServer(IServices sev) {
        logger.traceEntry("Entering setService");
        this.server = sev;
        logger.traceExit();
    }

    public void setTab(TabPane mainTabPane, Tab startTab, Tab repTab) {
        logger.traceEntry("Entering setTab");
        this.appTabPane = mainTabPane;
        this.startTab = startTab;
        this.reportTab = repTab;
        logger.traceExit();
    }

    public void setUser(Profile profile) {
        this.currentProfile = profile;
    }

    public void handleReportButton(ActionEvent actionEvent) {
        User reporter = currentProfile.getUser();
        LocalDateTime dateTime = LocalDateTime.now();
        String description = EmergencyDescriptionTextArea.getText();
        Status status =  Status.Reported;
        FirstResponder firstResponder = null;
        String location = null;
        Boolean myLocation = useMyCurrentLlocationRadioButton.isSelected();
        Boolean anotherLocation = useAnotherLocationRadioButton.isSelected();
        String anotherLocationString = LocationTextField.getText();

        if ((myLocation && anotherLocation) || (!myLocation && !anotherLocation)) {
            MessageAlert.showErrorMessage(null, "Please select one option for location");
            return;
        }
        if (anotherLocation && anotherLocationString.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Please enter the location");
            return;
        }
        else {
            location = anotherLocationString;
        }
        Emergency  emergency = new Emergency(reporter, dateTime, description, status, firstResponder, location);
        try {
            server.reportEmergency(emergency, this);
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
            return;
        }
        logger.traceExit();
    }

    @Override
    public void emergencyReported(Emergency emergency) {

    }
}
