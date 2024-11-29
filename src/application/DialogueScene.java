package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
        "/application/img/girl.png",   // 주인공 이미지
        "/application/img/bossA.png",  // 보스 이미지
        "/application/img/girl.png",  // 주인공 이미지
        "/application/img/bossA.png"   // 보스 이미지
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
        characterImage.setFitWidth(140);
        characterImage.setFitHeight(140);
        updateCharacterPosition(characterImage); // 캐릭터 위치 설정
        root.getChildren().add(characterImage);

        // 대사 라벨 설정
        Label dialogueLabel = new Label(dialogues[dialogueIndex]);
        dialogueLabel.setFont(new Font("Arial", 20));
        dialogueLabel.setTextAlignment(TextAlignment.CENTER);
        dialogueLabel.setWrapText(true);
        dialogueLabel.setAlignment(Pos.CENTER);
        dialogueLabel.setStyle("-fx-background-color: #eec39a; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-padding: 15px;");
        dialogueLabel.setPrefWidth(660); // 라벨 가로 크기 고정
        dialogueLabel.setPrefHeight(120); // 라벨 세로 크기 고정
        updateLabelPosition(dialogueLabel); // 대사 라벨 초기 위치 설정
        root.getChildren().add(dialogueLabel);

        // 대사 진행 로직
        Runnable advanceDialogue = () -> {
            dialogueIndex++;
            if (dialogueIndex < dialogues.length) {
                // 다음 대사로 갱신
                dialogueLabel.setText(dialogues[dialogueIndex]);
                characterImage.setImage(new Image(getClass().getResourceAsStream(characterImages[dialogueIndex])));
                updateCharacterPosition(characterImage); // 캐릭터 위치 업데이트
                updateLabelPosition(dialogueLabel); // 대사 라벨 위치 업데이트
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
            // 주인공: 오른쪽 아래
            characterImage.setLayoutX(0); // X 좌표
            characterImage.setLayoutY(378); // Y 좌표
        } else {
            // 보스: 왼쪽 아래
            characterImage.setLayoutX(660); // X 좌표
            characterImage.setLayoutY(378); // Y 좌표
        }
    }

    
    private void updateLabelPosition(Label dialogueLabel) {
        // 대화 인덱스에 따라 라벨 위치 변경
        if (dialogueIndex % 2 == 0) {
            // 주인공 대사: 라벨 왼쪽으로 배치
            dialogueLabel.setLayoutX(140); // X 좌표
            dialogueLabel.setLayoutY(380); // Y 좌표
        } else {
            // 보스 대사: 라벨 오른쪽으로 배치
            dialogueLabel.setLayoutX(0); // X 좌표
            dialogueLabel.setLayoutY(380); // Y 좌표
        }
    }
}
