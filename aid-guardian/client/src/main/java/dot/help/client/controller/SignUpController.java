package dot.help.client.controller;

import dot.help.client.StartProtobufClient;
import dot.help.client.validation.ValidateFields;
import dot.help.client.events.MessageAlert;
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
        ValidateFields validator = new ValidateFields();
        //Save the user
        String email = emailTextField.getText();
        validator.validate(email,"Error! Email can't be null!");
        String username = usernameTextField.getText();
        validator.validate(username,"Error! Username can't be null!");
        String password = passwordTextField.getText();
        validator.validate(password,"Error! Password can't be null!");
        String confirmPassword = confirmPasswordTextField.getText();
        validator.validate(confirmPassword,"Error! Confirm password can't be null!");
        String firstName = firstNameTextField.getText();
        validator.validate(firstName, "Error! First Name can't be null!");
        String lastName = lastNameTextField.getText();
        validator.validate(lastName, "Error! Last Name can't be null!");
        boolean certified = certiifiedCheckBox.isSelected();
        GenderType gender = GenderType.valueOf(genderChoiceBox.getValue());
        BloodGroupType bloodGroup = bloodGroupChoiceBox.getValue();
        LocalDate dateOfBirth = dateOfBirthDatePicker.getValue();
        validator.validateDate(dateOfBirth, "Error! Date of Birth can't be null!");
        Double height = Double.parseDouble(heightTextField.getText());
        Double weight = Double.parseDouble(weeightTextField.getText());
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
                user = new User(username, password);
            }
            profile = new Profile(user, firstName, lastName, gender, dateOfBirth, bloodGroup, height, weight, medicalHistory, 1.0);
            //TODO: check how to init client
            server.registerUser(profile,  null);

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(StartProtobufClient.class.getResource("Profile-view.fxml"));
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
