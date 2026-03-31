package controllers;

import alert.AlertView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Patient;
import org.mindrot.jbcrypt.BCrypt;
import services.PatientService;
import session.CurrentUser;
import ui.WindowManaged;
import ui.WindowManager;

public class PatientProfileController implements WindowManaged {

    @FXML private TextField nameField;
    @FXML private TextField lastnameField;
    @FXML private TextField amkaField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Button saveBtn;

    private final PatientService patientService = new PatientService();
    private String oldEmail;
    private Patient currentPatient;
    private WindowManager windowManager;

    @Override
    public void setWindowManager(WindowManager wm) {
        this.windowManager = wm;
    }

    @FXML
    public void initialize() {
        if (CurrentUser.isLoggedIn() && CurrentUser.getUser() instanceof Patient patient) {
            this.currentPatient = patient;
            fillFormWithData(patient);
        }

        saveBtn.setOnAction(e -> handleSave());
    }

    private void fillFormWithData(Patient patient) {
        this.oldEmail = patient.getEmail();

        nameField.setText(patient.getName());
        lastnameField.setText(patient.getLastname());
        amkaField.setText(patient.getAmka());
        emailField.setText(patient.getEmail());
        phoneField.setText(patient.getTelephone());
        passwordField.setText(patient.getPassword());
    }

    @FXML
    private void handleSave() {
        if (currentPatient == null) return;

        String plainPassword = passwordField.getText();

        if (!plainPassword.isEmpty() && !plainPassword.equals(currentPatient.getPassword())) {
            String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(plainPassword, org.mindrot.jbcrypt.BCrypt.gensalt());
            currentPatient.setPassword(hashedPassword);
        }
        currentPatient.setName(nameField.getText());
        currentPatient.setLastname(lastnameField.getText());
        currentPatient.setEmail(emailField.getText());
        currentPatient.setTelephone(phoneField.getText());

        boolean success = patientService.updateOwnProfile(currentPatient, oldEmail);

        if (success) {
            oldEmail = currentPatient.getEmail();
            AlertView.showInfo("Success", "Updated", "Your profile is now safe and updated.");
        }
    }

}