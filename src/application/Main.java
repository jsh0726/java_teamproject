package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainMenu mainMenu = new MainMenu();
        Scene mainMenuScene = mainMenu.getScene(primaryStage);

        primaryStage.setTitle("Running Game");
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
 