package controllers;

import ui.WindowManaged;
import ui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import session.CurrentUser;

public class PatientDashboardController implements WindowManaged {

    private WindowManager windowManager;

    @FXML private BorderPane mainBorderPane;
    @FXML private Label welcomeLbl;
    @FXML private Button logoutBtn;
    @FXML private Button homeBtn;
    @FXML private Button bookBtn;
    @FXML private Button historyBtn;
    @FXML private Button profileBtn;

    @Override
    public void setWindowManager(WindowManager wm) {
        this.windowManager = wm;
        loadDefaultView();
    }

    @FXML
    public void initialize() {
        welcomeLbl.setText("Welcome, " + CurrentUser.getDisplayName());

        homeBtn.setOnAction(e -> windowManager.loadInnerView(mainBorderPane, "/views/PatientHome.fxml"));
        bookBtn.setOnAction(e -> windowManager.loadInnerView(mainBorderPane, "/views/PatientBooking.fxml"));
        historyBtn.setOnAction(e -> windowManager.loadInnerView(mainBorderPane, "/views/PatientMyAppointments.fxml"));
        profileBtn.setOnAction(e -> windowManager.loadInnerView(mainBorderPane, "/views/PatientProfile.fxml"));

        logoutBtn.setOnAction(e -> {
            CurrentUser.logout();
            windowManager.showLogin();
        });
    }

    private void loadDefaultView() {
        windowManager.loadInnerView(mainBorderPane, "/views/PatientHome.fxml");
    }
}