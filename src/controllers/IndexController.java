package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class IndexController {

    @FXML
    private Button loginBtn;

    @FXML
    private Button signUpBtn;

    @FXML
    public void initialize() {
        loginBtn.setOnAction(e -> {
            System.out.println("Μετάβαση στη σελίδα Login");
        });

        signUpBtn.setOnAction(e -> {
            System.out.println("Μετάβαση στη σελίδα Sign Up");
        });
    }
}