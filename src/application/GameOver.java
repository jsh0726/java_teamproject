package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameOver {
    public Scene getScene(Stage primaryStage, int score) {
        VBox layout = new VBox();
        
        // 점수 표시와 게임 종료 화면 구성
        Button mainMenuButton = new Button("메인 메뉴로 돌아가기");
        
        mainMenuButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu();
            primaryStage.setScene(mainMenu.getScene(primaryStage));  // 메인 메뉴로 전환
        });
        
        layout.getChildren().add(mainMenuButton);
        return new Scene(layout, 800, 500);
    }
}
