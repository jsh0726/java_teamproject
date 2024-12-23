package application;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

public class GameClear {

    public Scene createGameClearScene(Stage primaryStage, int score) {
    	StackPane layout = new StackPane();
        ImageView background;
        try {
            // GIF 이미지 로드
            Image backgroundImage = new Image(getClass().getResource("/application/img/gamesuccess.gif").toExternalForm());
            background = new ImageView(backgroundImage);
            background.setFitWidth(800);
            background.setFitHeight(500);
        } catch (Exception e) {
            System.err.println("Error: Could not load gamelose.gif. Check the file path.");
            background = new ImageView();
        }

        // 메인 화면으로 돌아가기 버튼
        ImageView backButtonImage = new ImageView(new Image(getClass().getResource("/application/img/btn_back.png").toExternalForm()));
        backButtonImage.setFitWidth(150); // 기본 크기 설정
        backButtonImage.setFitHeight(60);
        Button backButton = new Button("", backButtonImage);
        backButton.setFocusTraversable(false);
        backButton.setStyle("-fx-background-color: transparent;");
        backButton.setOnAction(e -> {
            System.out.println("메인 메뉴로 돌아갑니다."); // 디버그 메시지
            MainMenu mainMenu = new MainMenu();
            primaryStage.setScene(mainMenu.getScene(primaryStage)); // 메인 메뉴 화면으로 전환
        });


        // 게임종료 버튼 생성
        ImageView endButtonImage = new ImageView(new Image(getClass().getResource("/application/img/btn_end.png").toExternalForm()));
        endButtonImage.setFitWidth(150); // 기본 크기 설정
        endButtonImage.setFitHeight(60);
        Button endButton = new Button("", endButtonImage);
        endButton.setFocusTraversable(false);
        endButton.setStyle("-fx-background-color: transparent;");
        endButton.setOnAction(e -> {
            System.out.println("게임 종료"); // 디버그 메시지
            primaryStage.close(); // 프로그램 종료
        });

     // "게임 재시작" 버튼 확대/축소 애니메이션
        backButton.setOnMouseEntered(e -> {
            backButtonImage.setFitWidth(170); // 확대 크기
            backButtonImage.setFitHeight(70);
        });
        backButton.setOnMouseExited(e -> {
            backButtonImage.setFitWidth(150); // 원래 크기
            backButtonImage.setFitHeight(60);
        });

        // "메인 메뉴" 버튼 확대/축소 애니메이션
        endButton.setOnMouseEntered(e -> {
            endButtonImage.setFitWidth(170); // 확대 크기
            endButtonImage.setFitHeight(70);
        });
        endButton.setOnMouseExited(e -> {
            endButtonImage.setFitWidth(150); // 원래 크기
            endButtonImage.setFitHeight(60);
        });

     // 레이아웃에 배경과 버튼 추가
        layout.getChildren().addAll(background, backButton, endButton);

        // 버튼 위치 조정
        StackPane.setMargin(backButton, new Insets(42, 0, 50, 0)); // back 버튼 위치
        StackPane.setMargin(endButton, new Insets(190, 0, 10, 0)); // end 버튼 위치

        return new Scene(layout, 800, 500);

    }
}
