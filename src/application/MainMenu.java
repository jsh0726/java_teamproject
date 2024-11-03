package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu {
    public Scene getScene(Stage primaryStage) {
        VBox layout = new VBox();
        
        Button startButton = new Button("게임 시작");
        Button instructionsButton = new Button("게임 방법");
        
        startButton.setOnAction(e -> {
            GamePlay gamePlay = new GamePlay();
            primaryStage.setScene(gamePlay.getScene(primaryStage));  // 게임 화면으로 전환
        });
        
        instructionsButton.setOnAction(e -> {
            GameInstructions instructions = new GameInstructions();
            primaryStage.setScene(instructions.getScene(primaryStage));  // 게임 방법 화면으로 전환
        });
        
        layout.getChildren().addAll(startButton, instructionsButton);
        return new Scene(layout, 800, 500);
    }
}
