package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import models.Appointment;
import models.Patient;
import services.AppointmentService;
import services.PatientService;
import session.CurrentUser;
import ui.WindowManaged;
import ui.WindowManager;
import java.util.List;
import java.util.Map;

public class PatientHomeController implements WindowManaged {

    @FXML private Label welcomeLabel;
    @FXML private Label nextDateLbl;
    @FXML private Label nextDoctorLbl;
    @FXML private Label totalVisitsLbl;

    @FXML private PieChart topDoctorsPieChart;

    private final PatientService patientService = new PatientService();
    private final AppointmentService appointmentService = new AppointmentService();
    private WindowManager windowManager;

    @Override
    public void setWindowManager(WindowManager wm) {
        this.windowManager = wm;
    }

    @FXML
    public void initialize() {
        if (CurrentUser.isLoggedIn() && CurrentUser.getUser() instanceof Patient patient) {
            welcomeLabel.setText("Welcome back, " + patient.getName() + "!");
            loadHomeStats(patient.getId());
            loadTopDoctorsChart(patient.getId());
        }
    }

    private void loadHomeStats(int patientId) {
        List<Appointment> apps = patientService.getMyAppointments(patientId);

        long activeCount = apps.stream()
                .filter(a -> !a.getAppointmentType().equals(Appointment.STATUS_CANCELLED))
                .count();

        totalVisitsLbl.setText(String.valueOf(activeCount));


        Appointment next = appointmentService.getNextAppointment(patientId);

        if (next != null) {
            nextDateLbl.setText(next.getAppointmentDatetime());
            nextDoctorLbl.setText(patientService.getDoctorFullNameById(next.getIdEmployee()));
        }
    }

    private void loadTopDoctorsChart(int loggedInPatientId) {
        Map<String, Long> stats = appointmentService.getTop3DoctorsStats(loggedInPatientId);

        if (stats == null || stats.isEmpty()) {
            topDoctorsPieChart.setVisible(false);
            topDoctorsPieChart.setManaged(false);
            return;
        }

        topDoctorsPieChart.setVisible(true);
        topDoctorsPieChart.setManaged(true);

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        stats.forEach((doctorName, count) -> {
            pieData.add(new PieChart.Data(doctorName + " (" + count + ")", count));
        });

        topDoctorsPieChart.setData(pieData);
    }
}