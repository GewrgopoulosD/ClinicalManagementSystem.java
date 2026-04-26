package controllers;

import alert.AlertView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Appointment;
import models.MedicalRecord;
import models.Patient;
import models.User;
import services.AdminService;
import services.MedicalRecordService;
import ui.WindowManaged;
import ui.WindowManager;
import java.util.List;

public class AdminPatientsController implements WindowManaged {

    @FXML private TableView<User> patientTable;
    @FXML private TableColumn<User, String> amkaCol;
    @FXML private TableColumn<User, String> lastnameCol;
    @FXML private TableColumn<User, String> nameCol;

    @FXML private TextField searchField;
    @FXML private Label totalPatientsLbl;

    @FXML private TextField editNameField;
    @FXML private TextField editLastnameField;
    @FXML private TextField editAmkaField;
    @FXML private TextField editEmailField;
    @FXML private TextField editPhoneField;

    @FXML private TableView<Appointment> historyTable;
    @FXML private TableColumn<Appointment, String> dateCol;
    @FXML private TableColumn<Appointment, String> doctorCol;
    @FXML private TableColumn<Appointment, String> statusCol;

    @FXML private Tab consultationDetailsTab;
    @FXML private TabPane detailsTabPane;
    @FXML private Label doctorNameLbl;
    @FXML private Label detDateLbl;
    @FXML private TextArea detSymptomsArea;
    @FXML private TextArea detDiagnosisArea;
    @FXML private TextArea detTreatmentArea;
    @FXML private TextArea detInternalNotesArea;

    private final AdminService adminService = new AdminService();
    private ObservableList<User> allPatientsList = FXCollections.observableArrayList();
    private User selectedUser;
    private String oldEmail;
    private final MedicalRecordService recordService = new MedicalRecordService();

    @Override
    public void setWindowManager(WindowManager wm) { }

    @FXML
    public void initialize() {
        //column
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastnameCol.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        amkaCol.setCellValueFactory(new PropertyValueFactory<>("amka"));

        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDatetime"));
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorFullName"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));

        loadPatients();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(newValue);
        });

        //row table click
        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clearMedicalRecordTab();
                fillFormWithData(newSelection);
            }
        });

        setupHistorySelectionListener();
    }

    @FXML
    public void loadPatients() {
        allPatientsList.clear();
        List<Patient> patientsFromDB = adminService.getAllPatients();
        allPatientsList.addAll(patientsFromDB);

        patientTable.setItems(allPatientsList);
        totalPatientsLbl.setText("" + allPatientsList.size());
    }

    private void filterTable(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            patientTable.setItems(allPatientsList);
            return;
        }

        ObservableList<User> filteredResults = FXCollections.observableArrayList();
        String filter = searchText.toLowerCase();

        for (User u : allPatientsList) {
            boolean matchesName = u.getName().toLowerCase().contains(filter);
            boolean matchesLastname = u.getLastname().toLowerCase().contains(filter);

            boolean matchesAmka = false;
            if (u instanceof Patient) {
                matchesAmka = ((Patient) u).getAmka().contains(filter);
            }

            if (matchesName || matchesLastname || matchesAmka) {
                filteredResults.add(u);
            }
        }
        patientTable.setItems(filteredResults);
    }

    private void fillFormWithData(User user) {
        selectedUser = user;
        oldEmail = user.getEmail();

        editNameField.setText(user.getName());
        editLastnameField.setText(user.getLastname());
        editEmailField.setText(user.getEmail());
        editPhoneField.setText(user.getTelephone());

        if (user instanceof Patient) {
            Patient p = (Patient) user;
            editAmkaField.setText(p.getAmka());
        }

        //history data
        List<Appointment> history = adminService.getPatientHistory(user.getId());
        historyTable.setItems(FXCollections.observableArrayList(history));
    }

    private void setupHistorySelectionListener() {
        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {

                doctorNameLbl.setText(newVal.getDoctorFullName());
                detDateLbl.setText(newVal.getAppointmentDatetime());
                detSymptomsArea.setText(newVal.getAppointmentDescription());

                //medical records
                MedicalRecord record = recordService.getDetailsForAppointment(newVal.getIdAppointment());

                if (record != null) {
                    detDiagnosisArea.setText(record.getDiagnosis());
                    detTreatmentArea.setText(record.getTreatment());
                    detInternalNotesArea.setText(record.getInternalNotes());
                } else {
                    //if there isnt record
                    detDiagnosisArea.setText("No diagnosis recorded yet.");
                    detTreatmentArea.clear();
                    detInternalNotesArea.clear();
                }

                //go to tab
                detailsTabPane.getSelectionModel().select(consultationDetailsTab);
            }
        });
    }

    @FXML
    private void handleSave() {
        if (selectedUser == null){
            AlertView.showError("Error", "Please Select a User", "Please Select a User");
            return;
        }

        selectedUser.setName(editNameField.getText());
        selectedUser.setLastname(editLastnameField.getText());
        selectedUser.setEmail(editEmailField.getText());
        selectedUser.setTelephone(editPhoneField.getText());

        if (selectedUser instanceof Patient) {
            ((Patient) selectedUser).setAmka(editAmkaField.getText());
        }

        boolean success = adminService.updatePatient((Patient) selectedUser, oldEmail);

        if (success) {
            oldEmail = selectedUser.getEmail();
            patientTable.refresh();
            alert.AlertView.showInfo("Success", "Patient Updated", "The patient information has been saved successfully.");
        } else {
            alert.AlertView.showError("Error", "Update Failed", "Could not saveSchedule patient data. Please try again.");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedUser == null){
            AlertView.showError("Error", "Please Select a User", "Please Select a User");
            return;
        }

        boolean confirm = alert.AlertView.showConfirmation("Delete Patient",
                "Are you sure?",
                "You are about to delete " + selectedUser.getLastname() + ". This action cannot be undone.");

        if (confirm) {
            if (adminService.deletePatient(selectedUser.getEmail())) {
                allPatientsList.remove(selectedUser);
                filterTable(searchField.getText());
                clearForm();
                alert.AlertView.showInfo("Deleted", "Success", "The patient has been removed from the system.");
            } else {
                alert.AlertView.showError("Error", "Deletion Failed", "The patient could not be deleted.");
            }
        }
    }

    private void clearForm() {
        editNameField.clear();
        editLastnameField.clear();
        editAmkaField.clear();
        editEmailField.clear();
        editPhoneField.clear();
        selectedUser = null;
    }

    private void clearMedicalRecordTab() {
        doctorNameLbl.setText("-");
        detDateLbl.setText("-");
        detSymptomsArea.clear();
        detDiagnosisArea.clear();
        detTreatmentArea.clear();
        detInternalNotesArea.clear();
    }
}