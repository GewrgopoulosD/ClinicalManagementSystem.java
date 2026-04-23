package controllers;

import alert.AlertView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Appointment;
import models.MedicalRecord;
import services.AppointmentService;
import services.MedicalRecordService;
import session.CurrentUser;
import java.util.List;

public class PatientMyAppointmentsController {

    @FXML private ListView<Appointment> appointmentsListView;

    @FXML private Label lblDoctorName;
    @FXML private Label lblAppointmentDate;
    @FXML private TextArea txtSymptoms;
    @FXML private TextArea txtDiagnosis;
    @FXML private TextArea txtTreatment;

    @FXML private Button refreshBtn;
    @FXML private Button cancelBtn;

    private final AppointmentService appointmentService = new AppointmentService();
    private final MedicalRecordService medicalRecordService = new MedicalRecordService();

    @FXML
    public void initialize() {
        //listener for visit
        appointmentsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayAppointmentDetails(newVal);
            }
        });

        //listiview
        appointmentsListView.setCellFactory(lv -> new ListCell<Appointment>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    //date - doctor
                    setText(item.getAppointmentDatetime() + " - " + item.getDoctorFullName() + " (" + item.getAppointmentType() + ")");
                }
            }
        });

        refreshBtn.setOnAction(event -> loadData());
        cancelBtn.setOnAction(event -> handleCancel());

        loadData();
    }

    private void loadData() {
        if (CurrentUser.getUser() != null) {
            int patientId = CurrentUser.getUser().getId();
            //all the appointments
            List<Appointment> myAppointments = appointmentService.getAppointmentsForPatient(patientId);
            appointmentsListView.setItems(FXCollections.observableArrayList(myAppointments));

            //clear medical report
            clearDetails();
        }
    }

    private void displayAppointmentDetails(Appointment app) {
        lblDoctorName.setText(app.getDoctorFullName());
        lblAppointmentDate.setText(app.getAppointmentDatetime());
        txtSymptoms.setText(app.getAppointmentDescription());

        //medical records
        MedicalRecord record = medicalRecordService.getDetailsForAppointment(app.getIdAppointment());

        if (record != null) {
            txtDiagnosis.setText(record.getDiagnosis());
            txtTreatment.setText(record.getTreatment());
        } else {
            //if there isn't appointment
            if (app.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_COMPLETED)) {
                txtDiagnosis.setText("The doctor has not uploaded the diagnosis yet.");
                txtTreatment.setText("-");
            } else if (app.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_CANCELLED)) {
                txtDiagnosis.setText("Appointment was cancelled. No record available.");
                txtTreatment.setText("-");
            } else {
                txtDiagnosis.setText("Pending...");
                txtTreatment.setText("Pending...");
            }
        }

        //cancel btn only if the app is pending
        cancelBtn.setDisable(!app.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_PENDING));
    }

    private void clearDetails() {
        lblDoctorName.setText("-");
        lblAppointmentDate.setText("-");
        txtSymptoms.clear();
        txtDiagnosis.clear();
        txtTreatment.clear();
        cancelBtn.setDisable(true);
    }

    private void handleCancel() {
        Appointment selectedApp = appointmentsListView.getSelectionModel().getSelectedItem();

        if (selectedApp == null) {
            AlertView.showWarning("No Selection", "Please select an appointment", "Click on an appointment from the list first.");
            return;
        }

        boolean confirm = AlertView.showConfirmation("Cancel Appointment", "Are you sure?",
                "Do you really want to cancel this appointment? This action cannot be undone.");

        if (confirm) {
            try {
                //refresh status
                appointmentService.updateAppointmentStatus(selectedApp.getIdAppointment(), Appointment.STATUS_CANCELLED);
                AlertView.showInfo("Success", "Appointment Cancelled", "Your appointment has been successfully cancelled.");
                loadData(); //refresh
            } catch (Exception e) {
                AlertView.showError("Error", "Update Failed", "A technical error occurred: " + e.getMessage());
            }
        }
    }
}