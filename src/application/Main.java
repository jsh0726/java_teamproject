package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private ImageView character;
    private final double CHARACTER_SIZE = 100; // 캐릭터 크기
    private final double GROUND_Y = 300; // 한옥 지붕 높이에 맞춘 땅의 위치
    private double y = GROUND_Y; // 캐릭터의 초기 y 좌표 (한옥 지붕 위)
    private double velocityY = 0; // 캐릭터의 Y축 속도 (점프용)
    private boolean onGround = true; // 캐릭터가 땅에 있는지 여부
    private List<Rectangle> obstacles = new ArrayList<>();  // 장애물 리스트
    private int lives = 3;  // 초기 생명 개수
    private Text livesText;  // 생명 수를 표시하는 텍스트
    private boolean gameOver = false;  // 게임 종료 여부 확인
    private boolean hasCollided = false;  // 현재 충돌 상태를 저장

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        // 고정된 배경 이미지 설정
        Image backgroundImage = new Image(getClass().getResource("background.png").toExternalForm());
        ImageView background = new ImageView(backgroundImage);
        background.setFitWidth(800);
        background.setFitHeight(500);

        // 캐릭터 이미지 설정
        Image characterImage = new Image(getClass().getResource("character2.png").toExternalForm());
        character = new ImageView(characterImage);
        character.setFitWidth(CHARACTER_SIZE);
        character.setFitHeight(CHARACTER_SIZE);
        character.setX(100); // 캐릭터의 x 좌표
        character.setY(y); // 캐릭터의 y 좌표 설정

        // 생명 텍스트 초기화
        livesText = new Text("Lives: " + lives);
        livesText.setX(10);
        livesText.setY(20);

        root.getChildren().addAll(background, character, livesText);
        Scene scene = new Scene(root, 800, 500);

        initObstacles(root); // 장애물 초기화

        // 키보드 이벤트 처리
        scene.setOnKeyPressed(event -> handleKeyPress(event));
        scene.setOnKeyReleased(event -> handleKeyRelease(event));

        // 게임 루프
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) {
                    updateCharacterPosition();
                    moveObstacles();
                    checkCollision();
                } else {
                    stop();
                }
            }
        }.start();

        primaryStage.setScene(scene);
        primaryStage.setTitle("수호신이 되어보자");
        primaryStage.show();
    }

    private void initObstacles(Pane root) {     // 장애물 초기화 메서드
        for (int i = 0; i < 5; i++) {  // 장애물 5개 생성
            double randomHeight = 60 + Math.random() * 50;
            Rectangle obstacle = new Rectangle(20, randomHeight, Color.RED);
            obstacle.setX(600 + i * 300); // 각 장애물의 초기 x 위치 설정 (간격을 300으로 변경)
            obstacle.setY(GROUND_Y + randomHeight);  // 장애물의 y 위치 설정 (한옥 지붕 높이에 맞춤)
            obstacles.add(obstacle);
            root.getChildren().add(obstacle);
        }
    }

    private void moveObstacles() {
        for (Rectangle obstacle : obstacles) {
            obstacle.setX(obstacle.getX() - 3);  // 장애물이 왼쪽으로 이동
            if (obstacle.getX() < 0) {
                obstacle.setX(800);  // 화면을 벗어나면 오른쪽에서 다시 등장
                double randomHeight = 30 + Math.random() * 70;
                obstacle.setHeight(randomHeight);
            }
        }
    }

    private void checkCollision() {
        boolean collided = false;  // 충돌 여부 확인 변수

        for (Rectangle obstacle : obstacles) {
            // 캐릭터의 충돌 영역을 줄이기 위해 getBoundsInParent()에서 조정
            if (character.getBoundsInParent().intersects(
                    obstacle.getBoundsInParent().getMinX() + 7, // 장애물의 왼쪽 가장자리보다 약간 오른쪽
                    obstacle.getBoundsInParent().getMinY() + 7, // 장애물의 위쪽 가장자리보다 약간 아래
                    obstacle.getBoundsInParent().getWidth() - 10, // 장애물의 너비를 약간 좁힘
                    obstacle.getBoundsInParent().getHeight() - 10)) { // 장애물의 높이를 약간 줄임
                collided = true;
                if (!hasCollided) {  // 이전에 충돌하지 않은 상태일 때만 생명 감소
                    lives--;  // 생명 감소
                    livesText.setText("Lives: " + lives);
                    hasCollided = true;

                    System.out.println("충돌 발생! 남은 생명: " + lives);

                    if (lives <= 0) {
                        gameOver = true;
                        System.out.println("게임 종료! 생명이 모두 소진되었습니다.");
                    }
                }
                break;
            }
        }

        if (!collided) {
            hasCollided = false;  // 장애물과 겹치지 않으면 충돌 상태 초기화
        }
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                if (onGround) {
                    velocityY = -15; // 점프
                    onGround = false;
                }
                break;
            case DOWN:
                character.setFitHeight(70); // 숙이기
                break;
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.DOWN) {
            character.setFitHeight(CHARACTER_SIZE); // 숙임 해제
        }
    }

    private void updateCharacterPosition() {
        // 중력 효과 적용
        velocityY += 0.7;
        y += velocityY;

        // 캐릭터가 한옥 지붕에 닿았을 때
        if (y >= GROUND_Y) {
            y = GROUND_Y;
            velocityY = 0;
            onGround = true;
        }

        // 캐릭터 위치 업데이트
        character.setY(y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}