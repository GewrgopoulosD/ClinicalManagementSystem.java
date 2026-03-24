package ui;

import alert.AlertView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import models.User;
import java.io.IOException;

public class WindowManager {// a class to control my stage,scenes

    private final Stage primaryStage;

    public WindowManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    private void prepareScene(String fxmlPath, String title, int width, int height, boolean resizable) {//switch scenes
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (loader.getController() instanceof WindowManaged controller) {// controller implements thw windowManaged we give control
                controller.setWindowManager(this);
            }

            primaryStage.setTitle(title);
            primaryStage.setScene(new Scene(root, width, height));
            primaryStage.setResizable(resizable);
            primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - width) / 2);//kentratisma
            primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - height) / 2);//kentrarisma
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertView.showError("Error", "Loading Failed", "Could not load: " + fxmlPath);
        }
    }

    public void showLogin() {
        primaryStage.getIcons().clear();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/photos/logo.jpg")));

        prepareScene("/views/Index.fxml", "Clinic Management System", 800, 600, false);
    }

    public void showDashboard(User user) {
        if (user == null) return;

        String fxml = switch (user.getRole()) {
            case DOCTOR -> "/views/DoctorDashboard.fxml";
            case PATIENT -> "/views/PatientDashboard.fxml";
            case ADMIN -> "/views/AdminDashboard.fxml";
            default -> null;
        };

        if (fxml != null) {
            prepareScene(fxml, "Clinic - Dashboard", 1200, 900, false);
        } else {
            AlertView.showError("Error", "Unknown Role", "Cannot determine dashboard");
        }
    }

    public void loadInnerView(BorderPane container, String fxmlPath) {//method to change the central node in a borderpane
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            if (loader.getController() instanceof WindowManaged controller) {
                controller.setWindowManager(this);
            }

            container.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            AlertView.showError("Error", "Content Load Failed", "Could not load: " + fxmlPath);
        }
    }

    public void switchScene(String fxmlPath, String title) {
        prepareScene(fxmlPath, title, 1200, 900, true);
    }

    public void close() {
        primaryStage.close();
    }
}