package controllers;

import alert.AlertView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import services.LoginService;

import java.io.IOException;

public class IndexController {

    @FXML private TextField emailTxt;
    @FXML private TextField passwordTxt;
    @FXML private Button loginBtn;
    @FXML private Button signUpBtn;

    private final LoginService loginService = new LoginService();

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
        try {
            Parent signUpRoot = FXMLLoader.load(getClass().getResource("/views/signUp.fxml"));

            //take the stage
            Stage stage = (Stage) signUpBtn.getScene().getWindow();

            // Αντικατέστησε το root του τρέχοντος scene
            stage.getScene().setRoot(signUpRoot);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private User handleLogin() {
        String email = emailTxt.getText();
        String password = passwordTxt.getText();

        if (email.isEmpty() || password.isEmpty()) {
            AlertView.showError("Login error","Empty fields", "Please fill all the fields.");
            return null;
        }

        try {
            User user = loginService.login(email, password);
            System.out.println(user.toString());
            return user;
        } catch (Exception ex) {
            AlertView.showError("Login Error","Login failed", ex.getMessage());
        }
        return null;
    }

}