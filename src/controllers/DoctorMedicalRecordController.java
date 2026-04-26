package controllers;

import alert.AlertView;
import dao.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Appointment;
import models.MedicalRecord;
import models.Patient;
import models.User;
import services.MedicalRecordService;
import services.PatientService;

import java.util.ArrayList;
import java.util.List;

public class DoctorMedicalRecordController {

    @FXML private TextField patientSearchField;

    @FXML private TableView<User> patientTable;
    @FXML private TableColumn<User, String> amkaCol;
    @FXML private TableColumn<User, String> lastnameCol;

    @FXML private ListView<Appointment> historyListView;

    @FXML private Label lblAppointmentDate, lblPatientName, totalVisitsLbl;

    @FXML private TextArea txtSymptoms, txtDiagnosis, txtTreatment, txtInternalNotes;

    private MedicalRecordService medicalService;
    private PatientService patientService;
    private ObservableList<User> allPatientsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            medicalService = new MedicalRecordService();
            patientService = new PatientService();

            setupTableColumns();//set up column table view
            loadPatients();

            //search listener
            patientSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filterTable(newVal);
            });

            //patient choice listener
            patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedUser) -> {
                if (selectedUser instanceof Patient) {
                    handlePatientSelection((Patient) selectedUser);
                }
            });

            //app choice listener
            historyListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedApp) -> {
                if (selectedApp != null) {
                    displayAppointmentDetails(selectedApp);
                }
            });

            setupHistoryListFactory();//history of patient

        } catch (Exception e) {
            AlertView.showError("Error", "Initialization Failed", e.getMessage());
        }
    }

    private void loadPatients() {
        try {
            List<Patient> patientsOnly = patientService.getAllPatients();

            allPatientsList.setAll(patientsOnly);
            patientTable.setItems(allPatientsList);
        } catch (Exception e) {
            AlertView.showError("DAO Error", "Could not load users", e.getMessage());
        }
    }

    private void filterTable(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            patientTable.setItems(allPatientsList);
            return;
        }

        String filter = searchText.toLowerCase();
        ObservableList<User> filteredList = FXCollections.observableArrayList();

        for (User u : allPatientsList) {
            String lastname = u.getLastname().toLowerCase();

            if (lastname.contains(filter)) {
                filteredList.add(u);
            }
            else if (u instanceof Patient p && p.getAmka().contains(filter)) {
                filteredList.add(p);
            }
        }
        patientTable.setItems(filteredList);
    }

    private void handlePatientSelection(Patient patient) {
        // set text label
        lblPatientName.setText(patient.getName() + " " + patient.getLastname());

        //history of a patient
        List<Appointment> history = medicalService.getPatientAppointmentHistory(patient.getId());

        if (history != null) {
            historyListView.setItems(FXCollections.observableArrayList(history));
            totalVisitsLbl.setText(String.valueOf(history.size()));
        } else {
            historyListView.setItems(FXCollections.observableArrayList());
            totalVisitsLbl.setText("0");
        }

        clearMedicalFields();//clear all
    }

    private void displayAppointmentDetails(Appointment app) {
        lblAppointmentDate.setText(app.getAppointmentDatetime());
        txtSymptoms.setText(app.getAppointmentDescription());

        //record from id
        MedicalRecord record = medicalService.getDetailsForAppointment(app.getIdAppointment());

        if (record != null) {
            txtDiagnosis.setText(record.getDiagnosis());
            txtTreatment.setText(record.getTreatment());
            txtInternalNotes.setText(record.getInternalNotes());
        } else {
            txtDiagnosis.clear();
            txtTreatment.clear();
            txtInternalNotes.clear();
        }
    }

    @FXML
    private void handleSaveRecord() {
        //took the selected appointment
        Appointment selectedApp = historyListView.getSelectionModel().getSelectedItem();

        if (selectedApp == null) {
            AlertView.showWarning("Attention", "Selection Required", "Please select an appointment first.");
            return;
        }

        //canfirmation
        boolean confirm = AlertView.showConfirmation("Save", "Confirm Save", "Save this medical record?");

        if (confirm) {//if confirm create a new record
            try {
                MedicalRecord newRecord = new MedicalRecord();
                newRecord.setAppointmentId(selectedApp.getIdAppointment());
                newRecord.setIdCustomer(selectedApp.getIdCustomer());
                newRecord.setDiagnosis(txtDiagnosis.getText());
                newRecord.setTreatment(txtTreatment.getText());
                newRecord.setInternalNotes(txtInternalNotes.getText());

                medicalService.saveConsultation(newRecord);
                AlertView.showInfo("Success", "Saved", "Record saved successfully!");
            } catch (Exception e) {
                AlertView.showError("Error", "Save failed", e.getMessage());
            }
        }
    }

    private void setupTableColumns() {//set up tableview
        amkaCol.setCellValueFactory(data -> {//amka column
            if (data.getValue() instanceof Patient p) {
                return new javafx.beans.property.SimpleStringProperty(p.getAmka());
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        lastnameCol.setCellValueFactory(data ->//lastname column
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLastname()));
    }

    private void setupHistoryListFactory() {
        historyListView.setCellFactory(lv -> new ListCell<Appointment>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getAppointmentDatetime() + " | " + item.getDoctorFullName() + " (" + item.getAppointmentType() + ")");
                }
            }
        });
    }

    private void clearMedicalFields() {
        lblAppointmentDate.setText("-");
        txtSymptoms.clear();
        txtDiagnosis.clear();
        txtTreatment.clear();
        txtInternalNotes.clear();
    }
}