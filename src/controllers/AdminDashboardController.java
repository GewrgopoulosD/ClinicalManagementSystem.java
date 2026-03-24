package controllers;

import ui.WindowManaged;
import ui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import session.CurrentUser;

public class AdminDashboardController implements WindowManaged {

    private WindowManager windowManager;

    @FXML private BorderPane mainBorderPane;

    //top nodes
    @FXML private Label welcomeLbl;
    @FXML private Button logoutBtn;

    //left buttons
    @FXML private Button generalBtn;
    @FXML private Button doctorsBtn;
    @FXML private Button patientsBtn;



    @Override
    public void setWindowManager(WindowManager wm) {
        this.windowManager = wm;
        loadDefaultView();
    }

    @FXML
    public void initialize() {
        welcomeLbl.setText("Welcome, MR. " + CurrentUser.getDisplayName());


        generalBtn.setOnAction(e -> {
            windowManager.loadInnerView(mainBorderPane, "/views/AdminOverview.fxml");
        });


        doctorsBtn.setOnAction(e -> {
            windowManager.loadInnerView(mainBorderPane, "/views/AdminDoctorsList.fxml");
        });


//        patientsBtn.setOnAction(e -> {
//            windowManager.loadInnerView(mainBorderPane, "/views/AdminPatients.fxml");
//        });

        logoutBtn.setOnAction(e -> {
            CurrentUser.logout();
            windowManager.showLogin();
        });
    }

    private void loadDefaultView() {
        windowManager.loadInnerView(mainBorderPane, "/views/AdminOverview.fxml");
    }

}