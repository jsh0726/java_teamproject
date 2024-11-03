package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameInstructions {
    public Scene getScene(Stage primaryStage) {
        VBox layout = new VBox();
        
        // 게임 방법 내용 추가
        Button backButton = new Button("메인 메뉴로 돌아가기");
        
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu();
            primaryStage.setScene(mainMenu.getScene(primaryStage));  // 메인 메뉴로 전환
        });
        
        layout.getChildren().add(backButton);
        return new Scene(layout, 800, 500);
    }
}
