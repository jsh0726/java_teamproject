package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

/**
 * 게임 방법 화면을 담당하는 클래스.
 */
public class GameInstructions {
    public Scene getScene(Stage primaryStage) {
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(20);

        Label instructionsLabel = new Label(
            "게임 방법:\n1. 스테이지를 진행하며 장애물을 피하고 아이템을 수집하세요.\n" +
            "2. 스테이지를 클리어하면 다음 맵으로 이동합니다.\n" +
            "3. 마지막 스테이지에서는 보스와 전투를 벌이세요!"
        );
        instructionsLabel.setStyle("-fx-font-size: 16px;");

        Button backButton = new Button("메인 메뉴로 돌아가기");
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu();
            primaryStage.setScene(mainMenu.getScene(primaryStage));
        });

        layout.getChildren().addAll(instructionsLabel, backButton);
        return new Scene(layout, 800, 500);
    }
}
