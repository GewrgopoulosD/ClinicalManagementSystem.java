package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import ui.WindowManaged;
import ui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import services.AdminService;

import java.util.Map;

public class AdminOverviewController implements WindowManaged {

    @FXML private Label totalDoctorsLbl;
    @FXML private Label totalPatientsLbl;
    @FXML private Label todayAppointmentsLbl;
    @FXML private Label pendingActionsLbl;

    @FXML private PieChart topDoctorsPieChart;
    @FXML private BarChart<String, Number> topPatientsBarChart;

    private WindowManager windowManager;
    private final AdminService  adminService = new AdminService();


    @Override
    public void setWindowManager(WindowManager wm) {
        this.windowManager = wm;
    }

    @FXML
    public void initialize() {
        loadDashboardStats();
        loadCharts();
    }

    private void loadDashboardStats() {
        pendingActionsLbl.setText("0");

        totalDoctorsLbl.setText(String.valueOf(adminService.getTotalDoctorsCount()));
        totalPatientsLbl.setText(String.valueOf(adminService.getTotalPatientsCount()));
        todayAppointmentsLbl.setText(String.valueOf(adminService.getTodayAppointmentsCount()));
        pendingActionsLbl.setText(String.valueOf(adminService.getPendingDoctors().size()));
    }

    private void loadCharts() {
        loadTopDoctorsPieChart();
        loadTopPatientsBarChart();
    }

    private void loadTopDoctorsPieChart() {
        //took the data
        Map<String, Long> stats = adminService.getTop5DoctorsStats();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        //map to piechart
        stats.forEach((name, count) -> {
            String label = name + " (" + count + ")";
            pieChartData.add(new PieChart.Data(label, count));
        });

        topDoctorsPieChart.setData(pieChartData);
    }

    private void loadTopPatientsBarChart() {
        //took data
        Map<String, Long> stats = adminService.getTop5PatientsStats();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Completed Visits");

        stats.forEach((name, count) -> {
            series.getData().add(new XYChart.Data<>(name, count));
        });

        //refresh
        topPatientsBarChart.getData().clear();
        topPatientsBarChart.getData().add(series);
    }
}
