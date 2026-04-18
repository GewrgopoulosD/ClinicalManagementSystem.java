import javafx.scene.Scene;
import ui.WindowManager;
import javafx.application.*;
import javafx.stage.*;

import java.io.File;

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
