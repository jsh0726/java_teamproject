package application;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameInstructions {
    public Scene getScene(Stage primaryStage) {
        Pane root = new Pane(); // 절대 위치 설정을 위한 Pane 사용

        // 배경 이미지 설정
        Image backgroundImage = new Image(getClass().getResource("/application/img/gamerule.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(800, 500, false, false, false, false)
        );
        root.setBackground(new Background(bgImage));

        // "메인 메뉴로 돌아가기" 이미지 버튼 생성
        Image backButtonImage = new Image(getClass().getResource("/application/img/btn_back.png").toExternalForm());
        ImageView backButton = new ImageView(backButtonImage);
        backButton.setFitWidth(160); // 기본 버튼 크기 설정
        backButton.setFitHeight(60);
        backButton.setLayoutX(180); // X 좌표 설정
        backButton.setLayoutY(380); // Y 좌표 설정

        // "게임 시작" 이미지 버튼 생성
        Image startImage = new Image(getClass().getResource("/application/img/btn_start.png").toExternalForm());
        ImageView startButton = new ImageView(startImage);
        startButton.setFitWidth(160); // 기본 버튼 크기 설정
        startButton.setFitHeight(60);
        startButton.setLayoutX(430); // X 좌표 설정
        startButton.setLayoutY(380); // Y 좌표 설정

        // "돌아가기" 버튼 확대/축소 애니메이션
        backButton.setOnMouseEntered(e -> {
            backButton.setFitWidth(170); // 확대 크기
            backButton.setFitHeight(70);
        });
        backButton.setOnMouseExited(e -> {
            backButton.setFitWidth(150); // 원래 크기
            backButton.setFitHeight(60);
        });

        // "게임 시작" 버튼 확대/축소 애니메이션
        startButton.setOnMouseEntered(e -> {
            startButton.setFitWidth(170); // 확대 크기
            startButton.setFitHeight(70);
        });
        startButton.setOnMouseExited(e -> {
            startButton.setFitWidth(150); // 원래 크기
            startButton.setFitHeight(60);
        });

        // "돌아가기" 버튼 클릭 이벤트 설정
        backButton.setOnMouseClicked(e -> {
            MainMenu mainMenu = new MainMenu();
            primaryStage.setScene(mainMenu.getScene(primaryStage));
        });
        

        // "게임 시작" 클릭 이벤트 설정
        startButton.setOnMouseClicked(e -> {
            DialogueScene dialogueScene = new DialogueScene();
            GamePlay gamePlay = new GamePlay();
            primaryStage.setScene(dialogueScene.getScene(primaryStage, gamePlay)); // 대화 화면으로 전환
        });

        // 버튼 추가
        root.getChildren().addAll(backButton, startButton);

        // 씬 생성 및 반환
        return new Scene(root, 800, 500);
    }
}

