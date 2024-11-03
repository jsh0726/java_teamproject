package application;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class GameStart {
    public Scene getScene(Stage primaryStage) {
        StackPane layout = new StackPane();
        
        // "START!" 텍스트 표시
        Label startLabel = new Label("START!");
        startLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: blue;");
        layout.getChildren().add(startLabel);
        
        Scene scene = new Scene(layout, 800, 500);

        // 일정 시간 후 자동으로 게임 화면으로 전환
        PauseTransition pause = new PauseTransition(Duration.seconds(2));  // 2초 대기
        pause.setOnFinished(e -> {
            GamePlay gamePlay = new GamePlay();
            primaryStage.setScene(gamePlay.getScene(primaryStage));  // 게임 화면으로 전환
        });
        pause.play();

        // 또는 클릭하면 바로 게임 화면으로 전환
        scene.setOnMouseClicked(e -> {
            GamePlay gamePlay = new GamePlay();
            primaryStage.setScene(gamePlay.getScene(primaryStage));  // 게임 화면으로 전환
        });

        return scene;
    }
}
