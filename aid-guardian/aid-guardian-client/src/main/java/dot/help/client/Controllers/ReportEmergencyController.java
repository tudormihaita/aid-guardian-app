package dot.help.client.Controllers;

import dot.help.client.StartProtoBufferClient;
import dot.help.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;

public class ReportEmergencyController {
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

    private IService server;
    private TabPane appTabPane;
    private Tab startTab;
    private Tab reportTab;


    public void setServer(IService sev) {
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
            MessageAlert.showErrorMessage(null, "Please eneter the location");
            return;
        }
        else {
            location = anotherLocationString;
        }
        Emergency  emergency = new Emergency(reporter, dateTime, description, status, firstResponder, location);
        try {
            server.ReportEmergency(emergency);
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
            return;
        }
        logger.traceExit();
    }
}
