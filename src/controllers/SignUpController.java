package controllers;

import dao.VerificationCodeDAO;
import interfaces.FormValidator;
import alert.AlertView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.*;
import services.SignUpService;

import java.io.IOException;
import java.util.List;

public class SignUpController implements FormValidator {

    @FXML
    private TextField nameTxt;
    @FXML
    private TextField lastnameTxt;
    @FXML
    private TextField telephoneTxt;
    @FXML
    private TextField emailTxt;
    @FXML
    private PasswordField passwordTxt;
    @FXML
    private PasswordField passwordCheckTxt;

    @FXML
    private RadioButton patientRadio;
    @FXML
    private RadioButton doctorRadio;
    private ToggleGroup roleGroup;


    private Label extraLabel;
    @FXML
    private TextField extraTxt;

    @FXML
    private Button signUpBtn;
    @FXML
    public Button goBackBtn;
    private SignUpService service;

    public SignUpController() {
        extraLabel = new Label("Special Field");
        service = new SignUpService();
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> { //take of the focus from the nametxt and give it to parent
            nameTxt.getParent().requestFocus();
        });

        roleGroup = new ToggleGroup();//give toggle to radio button
        patientRadio.setToggleGroup(roleGroup);
        doctorRadio.setToggleGroup(roleGroup);

        updateExtraField();// hide the extra field

        roleGroup.selectedToggleProperty().addListener(
                (obs, oldToggle, newToggle) -> updateExtraField());

        signUpBtn.setOnAction(e -> handleSignUp());//handler for Sign Up page
        goBackBtn.setOnAction(e -> goBack());//handler for goback to index
    }

    private void goBack() {
        try {
            Parent signUpRoot = FXMLLoader.load(getClass().getResource("/views/Index.fxml"));

            //take the stage
            Stage stage = (Stage) goBackBtn.getScene().getWindow();

            //make stage the Index.fxml
            stage.getScene().setRoot(signUpRoot);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateExtraField() {
        if (patientRadio.isSelected()) {
            extraLabel.setText("AMKA");
            extraTxt.setPromptText("Enter AMKA");
            extraTxt.setVisible(true);
        } else if (doctorRadio.isSelected()) {
            extraLabel.setText("Verification Code");
            extraTxt.setPromptText("Enter verification code");
            extraTxt.setVisible(true);
        } else {
            extraLabel.setVisible(false);
            extraTxt.setVisible(false);
        }
    }

    private void handleSignUp() {

        List<FieldDescriptor> fields = List.of( //make a list of the form to validate for the acceptable data
                new FieldDescriptor("Name", nameTxt, 2, 50, "string", true),
                new FieldDescriptor("Lastname", lastnameTxt, 2, 50, "string", true),
                new FieldDescriptor("Telephone", telephoneTxt, 10, 15, "phone", true),
                new FieldDescriptor("Email", emailTxt, 5, 100, "email", true),
                new FieldDescriptor("Password", passwordTxt, 6, 30, "password", true),
                new FieldDescriptor("Password Check", passwordCheckTxt, 6, 30, "password", true),
                new FieldDescriptor(extraLabel.getText(), extraTxt, 11, 11, "amka", true)
        );

        if (!FormValidator.validateFields(fields)) {//validate fields
            return;
        }

        if (!passwordTxt.getText().equals(passwordCheckTxt.getText())) { //check for the same password
            AlertView.showWarning("Error", "Passwords don't match!", "Please try again");
            return;
        }

        User user;
        String verificationCode = "";

        if (patientRadio.isSelected()) {
            user = new Patient(
                    nameTxt.getText(),
                    lastnameTxt.getText(),
                    telephoneTxt.getText(),
                    emailTxt.getText(),
                    passwordTxt.getText(),
                    extraTxt.getText() // AMKA
            );

        } else if (doctorRadio.isSelected()) {
            verificationCode = extraTxt.getText();
            user = new Doctor(
                    nameTxt.getText(),
                    lastnameTxt.getText(),
                    telephoneTxt.getText(),
                    emailTxt.getText(),
                    passwordTxt.getText()
            );
        } else {
            AlertView.showWarning("Error", "Role not selected", "Please select Patient or Doctor.");
            return;
        }

        try {
            service.registerUser(user, verificationCode);
            AlertView.showInfo("Success", "Sign Up Successful",
                    "Your account has been created successfully!" + "\n" +
                            "You can now log in with your email: " + user.getEmail());
            goBack();
        } catch (RuntimeException e) {
            AlertView.showWarning("Registration Error", "Cannot register user", e.getMessage());
        }
    }
}
