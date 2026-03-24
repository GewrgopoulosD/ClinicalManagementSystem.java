package controllers;

import ui.WindowManaged;
import ui.WindowManager;
import alert.AlertView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import models.User;
import services.LoginService;
import session.CurrentUser;

public class IndexController implements WindowManaged {

    @FXML private TextField emailTxt;
    @FXML private TextField passwordTxt;
    @FXML private Button loginBtn;
    @FXML private Button signUpBtn;

    private final LoginService loginService = new LoginService();
    private WindowManager windowManager;

    public void setWindowManager(WindowManager wm) {
        this.windowManager = wm;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            emailTxt.getParent().requestFocus();
        });

        loginBtn.setOnAction(e -> {
            handleLogin();
        });

        signUpBtn.setOnAction(e -> openSignUp());

    }
    private void openSignUp() {

        if (windowManager != null) {
            windowManager.switchScene("/views/Signup.fxml", "Sign Up");
        }

    }

    private void handleLogin() {
        String email = emailTxt.getText();
        String password = passwordTxt.getText();

        if (email.isEmpty() || password.isEmpty()) {
            AlertView.showError("Login error","Empty fields", "Please fill all the fields.");
            return;
        }

        try {
            User user = loginService.login(email, password);
            if(user != null) {
                CurrentUser.setUser(user);
                if (windowManager != null) {
                    windowManager.showDashboard(user); // open dashboard in same primaryStage with other size
                }
            }
        } catch (Exception ex) {
            AlertView.showError("Login Error","Login failed", ex.getMessage());
        }
    }
}