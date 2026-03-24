package controllers;

import ui.WindowManaged;
import ui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import services.AdminService;

public class AdminOverviewController implements WindowManaged {

    @FXML private Label totalDoctorsLbl;
    @FXML private Label totalPatientsLbl;
    @FXML private Label todayAppointmentsLbl;
    @FXML private Label pendingActionsLbl;

    private WindowManager windowManager;
    private final AdminService  adminService = new AdminService();


    @Override
    public void setWindowManager(WindowManager wm) {
        this.windowManager = wm;
    }

    @FXML
    public void initialize() {
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        pendingActionsLbl.setText("0");

        totalDoctorsLbl.setText(String.valueOf(adminService.getTotalDoctorsCount()));
        totalPatientsLbl.setText(String.valueOf(adminService.getTotalPatientsCount()));
        todayAppointmentsLbl.setText(String.valueOf(adminService.getTodayAppointmentsCount()));
        pendingActionsLbl.setText(String.valueOf(adminService.getPendingDoctors().size()));
    }
}
