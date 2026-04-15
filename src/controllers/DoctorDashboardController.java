package controllers;

import javafx.scene.layout.BorderPane;
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

    @FXML private BorderPane mainBorderPane;

    //first line
    @FXML private Label welcomeLbl;
    @FXML private Button logoutBtn;

    //second line
    @FXML private Label totalAppointmentsLbl;
    @FXML private Label nextAppointmentLbl;

    //left
    @FXML private Button shiftsBtn;
    @FXML private Button AppointmentsBtn;
    @FXML private Button medicalRecordsBtn;

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

        logoutBtn.setOnAction(e -> {
            CurrentUser.logout();
            windowManager.showLogin();
        });

        shiftsBtn.setOnAction(e -> {
            windowManager.loadInnerView(mainBorderPane, "/views/DoctorShifts.fxml");
        });

        AppointmentsBtn.setOnAction(e -> windowManager.loadInnerView(mainBorderPane, "/views/DoctorAppointments.fxml"));

        medicalRecordsBtn.setOnAction(e -> windowManager.loadInnerView(mainBorderPane, "/views/DoctorMedicalRecord.fxml"));
    }

    private void loadStats(Doctor doctor) {

        int totalAppointments = appointmentService.getTotalAppointmentsCount(doctor.getId());

        Appointment nextAppointment = appointmentService.getNextAppointment(doctor.getId());

        totalAppointmentsLbl.setText(String.valueOf(totalAppointments));

        if (nextAppointment != null) {
            nextAppointmentLbl.setText(nextAppointment.getAppointmentDatetime().toString());
        } else {
            nextAppointmentLbl.setText("No upcoming appointment");
        }
    }

}
