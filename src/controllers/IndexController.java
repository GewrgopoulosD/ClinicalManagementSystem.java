package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class IndexController {

    @FXML private TextField emailTxt;
    @FXML private TextField passwordTxt;
    @FXML private Button loginBtn;
    @FXML private Button signUpBtn;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            emailTxt.getParent().requestFocus();
        });

        loginBtn.setOnAction(e -> {
            System.out.println("Μετάβαση στη σελίδα Login");
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
}