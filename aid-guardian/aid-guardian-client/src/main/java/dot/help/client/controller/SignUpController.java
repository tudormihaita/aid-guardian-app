package dot.help.client.controller;

import dot.help.client.StartProtoBufferClient;
import dot.help.model.*;
import dot.help.services.IServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class SignUpController {
    private static final Logger logger= LogManager.getLogger(SignUpController.class);

    private IServices server;


    private TabPane appTabPane;
    private Tab startTab;
    private Tab signUpTab;

    @FXML
    public TextField firstNameTextField;
    @FXML
    public TextField emailTextField;
    @FXML
    public TextField usernameTextField;
    @FXML
    public TextField lastNameTextField;
    @FXML
    public DatePicker dateOfBirthDatePicker;
    @FXML
    public TextField heightTextField;
    @FXML
    public TextField weeightTextField;
    @FXML
    public TextField medicalHstoryTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public PasswordField confirmPasswordTextField;
    @FXML
    public ChoiceBox<String> genderChoiceBox;
    @FXML
    public ChoiceBox<BloodGroupType> bloodGroupChoiceBox;
    @FXML
    public CheckBox certiifiedCheckBox;



    public void setServer(IServices sev) {
        logger.traceEntry("Entering setService");
        this.server = sev;
        logger.traceExit();
    }

    public void setTab(TabPane mainTabPane, Tab startTab, Tab signUpTab) {
        logger.traceEntry("Entering setTab");
        this.appTabPane = mainTabPane;
        this.startTab = startTab;
        this.signUpTab = signUpTab;
        logger.traceExit();
    }

    @FXML
    private void initialize() {
        logger.traceEntry("Entering initialize");
        genderChoiceBox.getItems().addAll("Male", "Female", "Other");
        bloodGroupChoiceBox.getItems().addAll(BloodGroupType.O_NEGATIVE, BloodGroupType.O_POSITIVE, BloodGroupType.A_NEGATIVE,
        BloodGroupType.A_POSITIVE, BloodGroupType.B_NEGATIVE, BloodGroupType.B_POSITIVE, BloodGroupType.AB_NEGATIVE, BloodGroupType.AB_POSITIVE);
        logger.traceExit();
    }


    public void handleSignUp() {
        logger.traceEntry("Entering handleSignUp");
        //Save the user
        String email = emailTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String confirmPassword = confirmPasswordTextField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        boolean certified = certiifiedCheckBox.isSelected();
        GenderType gender = GenderType.valueOf(genderChoiceBox.getValue());
        BloodGroupType bloodGroup = bloodGroupChoiceBox.getValue();
        LocalDate dateOfBirth = dateOfBirthDatePicker.getValue();
        Float height = Float.parseFloat(heightTextField.getText());
        Float weight = Float.parseFloat(weeightTextField.getText());
        String medicalHistory = medicalHstoryTextField.getText();

        if (!Objects.equals(password, confirmPassword)) {
            MessageAlert.showErrorMessage(null, "The passwords are not identical!");
            return;
        }

        try {
            User user;
            Profile profile;
            if (certified) {
                user = new FirstResponder(email, username, password);
            }
            else {
                // o zis Tudor ca se mai gandeste daca in comunity helper pune sau nu profile
                user = new CommunityHelper(username, password);
            }
            profile = new Profile(user, firstName, lastName, gender, dateOfBirth, bloodGroup, height, weight, medicalHistory, 0);
            // Nu era in server create account cand am facut eu controller, am prezis ca se va numi asa
            server.createAcount(profile);

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

                appTabPane.getTabs().remove(signUpTab);
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception se) {
            MessageAlert.showErrorMessage(null, se.getMessage());
        }
        logger.traceExit();
    }


    public void handleBackButton() {
        logger.traceEntry("Entering handleBackButton");
        appTabPane.getTabs().add(startTab);
        appTabPane.getSelectionModel().select(startTab);
        logger.traceExit();
    }
}
