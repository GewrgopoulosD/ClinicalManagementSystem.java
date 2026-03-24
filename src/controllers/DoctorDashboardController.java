package controllers;

import ui.WindowManaged;
import ui.WindowManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Appointment;
import models.Doctor;
import models.Patient;
import services.AppointmentService;
import session.CurrentUser;

public class DoctorDashboardController implements WindowManaged {

    //first line
    @FXML private Label welcomeLbl;
    @FXML private Button logoutBtn;

    //second line
    @FXML private Label totalAppointmentsLbl;
    @FXML private Label nextAppointmentLbl;

    //third line
    @FXML private TableView<Patient> patientsTable;
    @FXML private TableColumn<Patient, String> nameCol;
    @FXML private TableColumn<Patient, String> lastnameCol;
    @FXML private TableColumn<Patient, String> telephoneCol;
    @FXML private TableColumn<Patient, String> emailCol;
    @FXML private TableColumn<Patient, String> amkaCol;

    private final AppointmentService appointmentService = new AppointmentService();
    WindowManager windowManager;

    @Override
    public void setWindowManager(WindowManager vw) {
        this.windowManager = vw;
    }

    @FXML
    public void initialize() {

        Doctor doctor = (Doctor) CurrentUser.getUser();

        welcomeLbl.setText("Welcome Dr. " + CurrentUser.getDisplayName());

        loadStats(doctor);

        loadPatients();

        logoutBtn.setOnAction(e -> {
            CurrentUser.logout();
            windowManager.showLogin();
        });
    }

    private void loadStats(Doctor doctor) {

        int totalAppointments = appointmentService.getTotalAppointments(doctor.getId());

        Appointment nextAppointment = appointmentService.getNextAppointment(doctor.getId());

        totalAppointmentsLbl.setText(String.valueOf(totalAppointments));

        if (nextAppointment != null) {
            nextAppointmentLbl.setText(nextAppointment.getAppointmentDatetime().toString());
        } else {
            nextAppointmentLbl.setText("No upcoming appointment");
        }
    }

    private void loadPatients() {

        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        lastnameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLastname()));

        telephoneCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTelephone()));

        emailCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail()));

        amkaCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAmka()));

        patientsTable.getItems().setAll(
                appointmentService.getAllPatients()
        );
    }
}
