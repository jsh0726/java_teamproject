package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class DialogueScene {
    private int dialogueIndex = 0; // 현재 대화 인덱스
    private String[] dialogues = {
        "\"구슬들이... 사라졌군! 누가 훔쳐간 거지?\"",
        "\"우하하! 이 구슬이 그렇게 유명하다던 천지의 구슬인가! 이 귀하신 몸이 가져가도록 하지.\"",
        "\"내가 이 마을의 홍보대사인데, 그럴 순 없어! 거기 서라!\"",
        "\"좋아! 그 패기를 어디 한 번 보자고. 네가 얼마나 잘 따라올 수 있을지 궁금하군!ㅋ\""
    };

    private String[] characterImages = {
        "/application/img/girl.png",  
        "/application/img/bossA.png",  
        "/application/img/girl.png",  
        "/application/img/bossA.png"  
    };

    public Scene getScene(Stage primaryStage, GamePlay gamePlay) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 500);

        // 배경 이미지 설정
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/application/img/stage1.png")));
        background.setFitWidth(800);
        background.setFitHeight(500);
        root.getChildren().add(background);

        // 캐릭터 이미지
        ImageView characterImage = new ImageView(new Image(getClass().getResourceAsStream(characterImages[dialogueIndex])));
        characterImage.setFitWidth(196);
        characterImage.setFitHeight(196);
        updateCharacterPosition(characterImage); // 캐릭터 위치 설정
        root.getChildren().add(characterImage);

        // 대사창 StackPane 생성
        StackPane dialoguePane = new StackPane();

        // 대사창 배경 이미지
        ImageView backgroundImageView = new ImageView(new Image(getClass().getResourceAsStream("/application/img/daesa.png")));
        backgroundImageView.setFitWidth(601); // 대사창 가로 크기
        backgroundImageView.setFitHeight(196); // 대사창 세로 크기

        // 대사 텍스트 라벨
        Label dialogueLabel = new Label(dialogues[dialogueIndex]);
        dialogueLabel.setFont(new Font("Arial", 18));
        dialogueLabel.setTextAlignment(TextAlignment.CENTER);
        dialogueLabel.setWrapText(true);
        dialogueLabel.setAlignment(Pos.CENTER);
        dialogueLabel.setPrefWidth(601); // 라벨 크기를 대사창 크기와 동일하게 설정
        dialogueLabel.setPrefHeight(196);
        dialogueLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;"); // 텍스트 스타일

        // StackPane에 배경 이미지와 텍스트 추가
        dialoguePane.getChildren().addAll(backgroundImageView, dialogueLabel);

        // StackPane 초기 위치 설정
        updateDialoguePosition(dialoguePane);

        root.getChildren().add(dialoguePane);

        // 대사 진행 로직
        Runnable advanceDialogue = () -> {
            dialogueIndex++;
            if (dialogueIndex < dialogues.length) {
                // 다음 대사로 갱신
                dialogueLabel.setText(dialogues[dialogueIndex]);
                characterImage.setImage(new Image(getClass().getResourceAsStream(characterImages[dialogueIndex])));
                updateCharacterPosition(characterImage); // 캐릭터 위치 업데이트
                updateDialoguePosition(dialoguePane); // 대사창 위치 업데이트
            } else {
                // 대화 종료 후 게임플레이 화면으로 전환
                primaryStage.setScene(gamePlay.getScene(primaryStage));
            }
        };

        // 화면 클릭 이벤트 추가
        scene.setOnMouseClicked(event -> advanceDialogue.run());

        // 스페이스바 입력 이벤트 추가
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE:
                    advanceDialogue.run();
                    break;
                default:
                    break;
            }
        });

        return scene;
    }

    private void updateCharacterPosition(ImageView characterImage) {
        // 대화 인덱스에 따라 캐릭터 위치 변경
        if (dialogueIndex % 2 == 0) {
            // 주인공: 왼쪽 아래
            characterImage.setLayoutX(0); // X 좌표
            characterImage.setLayoutY(304); // Y 좌표
        } else {
            // 보스: 오른쪽 아래
            characterImage.setLayoutX(604); // X 좌표
            characterImage.setLayoutY(304); // Y 좌표
        }
    }

    private void updateDialoguePosition(StackPane dialoguePane) {
        // 대화 인덱스에 따라 대사창 위치 변경
        if (dialogueIndex % 2 == 0) {
            // 첫 번째, 세 번째 대사: 기존 위치 유지
            dialoguePane.setLayoutX(199); // X 좌표
        } else {
            // 두 번째, 네 번째 대사: X 좌표를 0으로 변경
            dialoguePane.setLayoutX(0); // X 좌표
        }
        dialoguePane.setLayoutY(304); // Y 좌표는 동일
    }
}
