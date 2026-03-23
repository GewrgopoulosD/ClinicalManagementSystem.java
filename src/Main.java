import UI.WindowManager;
import controllers.IndexController;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.scene.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        WindowManager windowManager = new WindowManager(primaryStage);
        windowManager.showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
