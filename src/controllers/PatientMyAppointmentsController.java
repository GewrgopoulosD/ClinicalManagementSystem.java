package controllers;

import alert.AlertView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Appointment;
import services.AppointmentService;
import session.CurrentUser;
import java.util.List;

public class PatientMyAppointmentsController {

    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> doctorNameColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private TableColumn<Appointment, String> descColumn;

    @FXML private Button refreshBtn;
    @FXML private Button cancelBtn;

    private final AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDatetime"));
        doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("doctorFullName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));

        refreshBtn.setOnAction(event -> loadData());

        cancelBtn.setOnAction(event -> handleCancel());

        loadData();
    }

    private void loadData() {
        if (CurrentUser.getUser() != null) {
            int patientId = CurrentUser.getUser().getId();
            List<Appointment> myAppointments = appointmentService.getAppointmentsForPatient(patientId);
            appointmentsTable.setItems(FXCollections.observableArrayList(myAppointments));
        }
    }

    private void handleCancel() {
        //took the selected appointment
        Appointment selectedApp = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selectedApp == null) {
            AlertView.showWarning("No Selection", "Please select an appointment",
                    "You need to click on a row to cancel it.");
            return;
        }

        if (selectedApp.getAppointmentType().equals(Appointment.STATUS_CANCELLED)) {
            AlertView.showInfo("Already Cancelled", "Notice", "This appointment is already cancelled.");
            return;
        }

        boolean confirm = AlertView.showConfirmation("Cancel Appointment", "Are you sure?",
                "Do you really want to cancel this appointment?");

        if (confirm) {
            try {
                selectedApp.setAppointmentType(Appointment.STATUS_CANCELLED);

                appointmentService.updateAppointment(selectedApp);

                AlertView.showInfo("Success", "Appointment Cancelled", "Your record has been updated.");

                loadData();
            } catch (Exception e) {
                AlertView.showError("Error", "Update Failed", e.getMessage());
            }
        }
    }
}