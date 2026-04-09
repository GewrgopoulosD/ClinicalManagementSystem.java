package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Appointment;
import services.AppointmentService;
import alert.AlertView;
import session.CurrentUser;
import ui.WindowManaged;
import ui.WindowManager;
import java.time.LocalDate;
import java.util.List;

public class DoctorAppointmentsController implements WindowManaged {

    //lbls
    @FXML private Label totalAppsLabel;
    @FXML private Label completedAppsLabel;
    @FXML private Label nextAppLabel;
    @FXML private Label currentDateLabel;

    //tableview
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> timeColumn;
    @FXML private TableColumn<Appointment, String> patientNameColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private TableColumn<Appointment, String> descColumn;

    //btns
    @FXML private Button refreshBtn;
    @FXML private Button cancelBtn;
    @FXML private Button completeBtn;

    private WindowManager windowManager;
    private final AppointmentService appointmentService = new AppointmentService();
    private int doctorId;

    @Override
    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    @FXML
    public void initialize() {
        if (CurrentUser.isLoggedIn()) {
            this.doctorId = CurrentUser.getUser().getId();
        } else {
            AlertView.showError("Session Error", "No active session found.", "Please log in again.");
            if (windowManager != null) {
                windowManager.showLogin();
            }
            return;
        }

        currentDateLabel.setText(LocalDate.now().toString());
        setupTable();
        loadDashboardData();

        refreshBtn.setOnAction(e -> loadDashboardData());
        completeBtn.setOnAction(e -> handleStatusChange(Appointment.STATUS_COMPLETED));
        cancelBtn.setOnAction(e -> handleStatusChange(Appointment.STATUS_CANCELLED));
    }

    //tableview set up
    private void setupTable() {
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDatetime"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerFullName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));

        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equalsIgnoreCase(Appointment.STATUS_COMPLETED)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase(Appointment.STATUS_CANCELLED)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    //call service and fill the lbls - table view
    private void loadDashboardData() {
        try {
            //refresh table(pending, completed, cancelled)
            List<Appointment> dailyApps = appointmentService.getTodayFullSchedule(doctorId);
            appointmentsTable.setItems(FXCollections.observableArrayList(dailyApps));

            //labels
            totalAppsLabel.setText(String.valueOf(appointmentService.getTodayTotalCount(doctorId)));
            completedAppsLabel.setText(String.valueOf(appointmentService.getTodayCompletedCount(doctorId)));

            //next app
            Appointment next = appointmentService.getNextAppointmentToday(doctorId);
            if (next != null) {
                //time only
                String[] parts = next.getAppointmentDatetime().split(" ");
                String timeOnly = (parts.length > 1) ? parts[1] : parts[0];
                nextAppLabel.setText(timeOnly + " - " + next.getCustomerFullName());
            } else {
                nextAppLabel.setText("No more today");
            }

        } catch (Exception e) {
            AlertView.showError("Data Error", "Refresh failed", "Could not load today's schedule: " + e.getMessage());
        }
    }

    //changing status
    private void handleStatusChange(String newStatus) {
        Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertView.showWarning("No Selection", "Please select an appointment from the table first.", "");
            return;
        }

        // Προστασία: Μην αλλάζεις status σε ήδη ακυρωμένα/ολοκληρωμένα αν δεν χρειάζεται
        if (selected.getAppointmentType().equalsIgnoreCase(newStatus)) {
            return;
        }

        try {
            appointmentService.updateAppointmentStatus(selected.getIdAppointment(), newStatus);

            loadDashboardData();

        } catch (RuntimeException e) {
            AlertView.showError("Update Error", "Database update failed", e.getMessage());
        }
    }
}