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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

            Scene scene = new Scene(root, width, height); // Δημιουργούμε τη scene //vgale afto


            primaryStage.setTitle(title);
//            primaryStage.setScene(new Scene(root, width, height)); vale afto
            primaryStage.setScene(scene); //βγαλε αυτο
            primaryStage.setResizable(resizable);
            primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - width) / 2);//kentratisma
            primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - height) / 2);//kentrarisma
            primaryStage.show();

            try { //βγαλε αυτο
                enableLiveCSS(scene, fxmlPath);
            } catch (Exception e) {
                System.out.println("Live CSS could not be initialized: " + e.getMessage());
            }

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
            prepareScene(fxml, "Clinic - Dashboard", 1200, 900, true);
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
            BorderPane.setMargin(view, new javafx.geometry.Insets(5));
            container.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            AlertView.showError("Error", "Content Load Failed", "Could not load: " + fxmlPath);
        }
    }

    public void switchScene(String fxmlPath, String title,int width, int height) {
        prepareScene(fxmlPath, title, width, height, false);
    }

    public void close() {
        primaryStage.close();
    }

    private void enableLiveCSS(Scene scene, String fxmlPath) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.F5) {
                try {
                    String cssName = fxmlPath.substring(fxmlPath.lastIndexOf("/") + 1).replace(".fxml", "Css.css");
                    File cssFile = new File("resources/css/" + cssName).getAbsoluteFile();

                    if (cssFile.exists()) {
                        String content = Files.readString(cssFile.toPath());
                        String base64Content = java.util.Base64.getEncoder().encodeToString(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));

                        // ΠΡΟΣΟΧΗ ΕΔΩ: Καθαρίζουμε ΟΛΑ τα stylesheets και από τη scene και από το root
                        scene.getStylesheets().clear();
                        scene.getRoot().getStylesheets().clear();

                        String dataUri = "data:text/css;base64," + base64Content;
                        scene.getStylesheets().add(dataUri);

                        System.out.println("🚀 CSS Force Injected for: " + cssName);
                    } else {
                        System.out.println("❌ Not found: " + cssFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}