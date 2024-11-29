package application;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePlay {
    private static final double RUNNING_WIDTH = 100;
    private static final double RUNNING_HEIGHT = 100;
    private static final double JUMPING_WIDTH = 120;
    private static final double JUMPING_HEIGHT = 120;
    
    private boolean isSliding = false; // 슬라이드 상태를 나타내는 변수
    private static final double SLIDE_WIDTH = 50; // 슬라이드 상태 캐릭터 너비
    private static final double SLIDE_HEIGHT = 50; // 슬라이드 상태 캐릭터 높이
    
    // 1분(60초)을 나노초 단위로 설정
    private static final long BOSS_APPEAR_TIME = 60L * 1_000_000_000L; 
    private double characterY = 300;
    private double characterVelocityY = 0;
    private boolean isJumping = false;
    private static final double GRAVITY = 0.7;
    private static final double JUMP_STRENGTH = -15;
    private static final double SCROLL_SPEED = 5;
    private static final int ITEM_SPACING = 50;
    private ImageView character;
    private ImageView enemy;
    private Rectangle[] obstacles;
    private List<ImageView> items;
    private Label gameOverLabel, scoreLabel, livesLabel, gameClearLabel, battleLabel;
    private boolean gameOver = false;
    private boolean gameClear = false;
    private boolean inBattle = false;
    private int score = 0;
    private int lives = 3;
    private int enemyHealth = 30;
    
    private List<Circle> enemyProjectiles = new ArrayList<>();
    private long startTime; // 게임 시작 시간
    private long lastProjectileTime = 0;

    private Label enemyHealthLabel;private Pane root;
    
    public Scene getScene(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 896, 512);

        // 배경 이미지
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/application/img/stage1.png")));
        background.setFitWidth(896);
        background.setFitHeight(512);
        root.getChildren().add(background);

        // 캐릭터 이미지
        try {
            Image runningImage = new Image(getClass().getResourceAsStream("/application/img/runningBy256.gif"));
            if (runningImage.isError()) {
                System.out.println("러닝 이미지 로드 실패");
            }
            character = new ImageView(runningImage);
            character.setFitWidth(RUNNING_WIDTH);
            character.setFitHeight(RUNNING_HEIGHT);
            character.setX(50);
            character.setY(characterY);
            root.getChildren().add(character);
        } catch (Exception e) {
            System.out.println("러닝 이미지 로드 중 예외 발생: " + e.getMessage());
        }

     // 장애물 생성
        obstacles = new Rectangle[3];
        for (int i = 0; i < obstacles.length; i++) {
            double randomY = (Math.random() < 0.5) ? 370 : 320; // Y 좌표를 370 또는 320 중 랜덤으로 선택
            obstacles[i] = new Rectangle(800 + i * 300, randomY, 30, 30);
            obstacles[i].setFill(Color.BLACK);
            root.getChildren().add(obstacles[i]);
        }



        // 아이템
        items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ImageView item = new ImageView(new Image(getClass().getResourceAsStream("/application/img/yakgwa.png")));
            item.setFitWidth(30);
            item.setFitHeight(30);
            item.setX(800 + i * ITEM_SPACING);
            item.setY(350);
            items.add(item);
            root.getChildren().add(item);
        }

        // 라벨 생성
        scoreLabel = new Label("Score: 0");
        livesLabel = new Label("Lives: 3");
        gameOverLabel = new Label("Game Over");
        gameClearLabel = new Label("Game Clear!");
        battleLabel = new Label("Fight the Enemy!");
        enemyHealthLabel = new Label(String.valueOf(enemyHealth));

        // 라벨 설정 및 추가
        setupLabels(root);
        
        // 적 초기화
        enemy = new ImageView(new Image(getClass().getResourceAsStream("/application/img/boss.gif")));
        enemy.setFitWidth(150);
        enemy.setFitHeight(150);
        enemy.setX(650);
        enemy.setY(250);
        enemy.setVisible(false);
        root.getChildren().add(enemy);

        // 키 입력 처리
        setupKeyHandlers(scene);
        startTime = System.nanoTime(); // 게임 시작 시간 기록
        // 게임 루프
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver && !gameClear) {
                    update(now);  // 캐릭터 및 기타 요소 업데이트
                    if (inBattle && now - lastProjectileTime > 1_000_000_000) {
                        spawnEnemyProjectile(root);
                        lastProjectileTime = now;
                    }
                }
            }
        };
        timer.start();

        return scene;
    }
    
    private void setupLabels(Pane root) {
        // 점수 및 생명 라벨
        scoreLabel.setLayoutX(600);
        scoreLabel.setLayoutY(20);
        scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        root.getChildren().add(scoreLabel);

        livesLabel.setLayoutX(700);
        livesLabel.setLayoutY(20);
        livesLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        root.getChildren().add(livesLabel);

        // 기타 라벨
        setupMessageLabels(root);
    }

    private void setupMessageLabels(Pane root) {

        // 게임 클리어 라벨
        gameClearLabel.setTextFill(Color.BLUE);
        gameClearLabel.setStyle("-fx-font-size: 48px;");
        gameClearLabel.setLayoutX(250);
        gameClearLabel.setLayoutY(200);
        gameClearLabel.setVisible(false);
        root.getChildren().add(gameClearLabel);

        // 전투 라벨
        battleLabel.setTextFill(Color.PURPLE);
        battleLabel.setStyle("-fx-font-size: 32px;");
        battleLabel.setLayoutX(300);
        battleLabel.setLayoutY(50);
        battleLabel.setVisible(false);
        root.getChildren().add(battleLabel);

        // 적 체력 라벨
        enemyHealthLabel.setLayoutX(750);
        enemyHealthLabel.setLayoutY(280);
        enemyHealthLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: red; -fx-font-weight: bold;");
        enemyHealthLabel.setVisible(false);
        root.getChildren().add(enemyHealthLabel);
    }

    private void setupKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            // 점프 로직
            if (event.getCode() == KeyCode.SPACE && !isJumping && !gameOver && !gameClear && !isSliding) {
                characterVelocityY = JUMP_STRENGTH;
                isJumping = true;
                character.setImage(new Image(getClass().getResourceAsStream("/application/img/jumpBy256.gif")));
                character.setFitWidth(JUMPING_WIDTH);
                character.setFitHeight(JUMPING_HEIGHT);
            }
            // 슬라이드 로직
            if (event.getCode() == KeyCode.S && !isJumping && !isSliding && !gameOver && !gameClear) {
                isSliding = true;
                character.setFitWidth(SLIDE_WIDTH);
                character.setFitHeight(SLIDE_HEIGHT);
                character.setY(350); // 슬라이드 시 Y 좌표를 350으로 고정
            }
        });

        scene.setOnKeyReleased(event -> {
            // 슬라이드에서 복구
            if (event.getCode() == KeyCode.S && isSliding) {
                isSliding = false;
                character.setFitWidth(RUNNING_WIDTH);
                character.setFitHeight(RUNNING_HEIGHT);
                character.setY(300); // 원래 Y 좌표로 복구
            }
        });
    }

    private void update(long now) {
        // 보스 등장 조건
        if (!inBattle && now - startTime >= BOSS_APPEAR_TIME) {
            startBattle();
        }

        // 캐릭터 위치 업데이트
        updateCharacterPosition();

        // 게임 상태에 따라 업데이트 처리
        if (!inBattle) {
            handleObstacles();
            handleItems();
        } else {
            updateProjectiles();
        }
    }

    private void updateCharacterPosition() {
        if (!isSliding) { // 슬라이드 상태에서는 Y 좌표를 업데이트하지 않음
            characterY += characterVelocityY;
            characterVelocityY += GRAVITY;

            if (characterY >= 300) {
                characterY = 300;
                characterVelocityY = 0;

                if (isJumping) { // 점프 상태일 때만 복원
                    isJumping = false;
                    try {
                        Image runningImage = new Image(getClass().getResourceAsStream("/application/img/runningBy256.gif"));
                        character.setImage(runningImage);
                        character.setFitWidth(RUNNING_WIDTH);
                        character.setFitHeight(RUNNING_HEIGHT);
                    } catch (Exception e) {
                        System.out.println("러닝 이미지 복원 중 오류: " + e.getMessage());
                    }
                }
            }

            character.setY(characterY); // 캐릭터의 위치를 업데이트
        }
    }


    private void handleObstacles() {
        // 장애물 가능한 Y 좌표
        double[] possibleYPositions = {300, 370};

        for (int i = 0; i < obstacles.length; i++) {
            Rectangle obstacle = obstacles[i];

            // 장애물 이동
            obstacle.setX(obstacle.getX() - SCROLL_SPEED);

            // 장애물이 화면 왼쪽을 벗어나면 재배치
            if (obstacle.getX() < -30) {
                double newX;
                double newY;
                boolean validPosition;

                int retryCount = 0; // 무한 루프 방지용 카운터
                int maxRetries = 10; // 최대 시도 횟수

                do {
                    validPosition = true;

                    // 새 X와 Y 좌표 생성
                    newX = 800 + Math.random() * 300; // X 좌표를 랜덤 생성
                    newY = possibleYPositions[(int) (Math.random() * possibleYPositions.length)]; // Y 좌표 랜덤 선택

                    // 다른 장애물들과 간격 확인
                    for (int j = 0; j < obstacles.length; j++) {
                        if (i != j) { // 자기 자신 제외
                            double xDistance = Math.abs(newX - obstacles[j].getX());
                            if (xDistance < 200) { // 최소 간격 200픽셀 조건
                                validPosition = false;
                                break;
                            }
                        }
                    }

                    retryCount++;
                    if (retryCount > maxRetries) {
                        System.out.println("Warning: Unable to find valid position for obstacle " + i);
                        validPosition = true; // 강제 탈출
                        break;
                    }
                } while (!validPosition);

                // 장애물 위치 설정
                obstacle.setX(newX);
                obstacle.setY(newY);

                // 디버깅용 로그
                System.out.println("Obstacle " + i + " positioned at X: " + newX + ", Y: " + newY);
            }

            // 캐릭터와 장애물 충돌 체크
            if (checkCollision(obstacle)) {
                reduceLife();

                // 충돌 후 재배치
                obstacle.setX(800 + Math.random() * 300);
                obstacle.setY(possibleYPositions[(int) (Math.random() * possibleYPositions.length)]);
            }
        }
    }
    
    private boolean checkCollision(Rectangle obstacle) {
        Rectangle characterHitBox = new Rectangle(
            character.getX() + 10, // 캐릭터의 히트 박스를 약간 안쪽으로 조정
            character.getY() + 10,
            character.getFitWidth() - 20,
            character.getFitHeight() - 20
        );
        return characterHitBox.getBoundsInParent().intersects(obstacle.getBoundsInParent());
    }


    private void handleItems() {
        for (ImageView item : items) {
            item.setX(item.getX() - SCROLL_SPEED);
            if (item.getX() < -30) {
                item.setX(800 + Math.random() * ITEM_SPACING);
            }
            if (character.getBoundsInParent().intersects(item.getBoundsInParent())) {
                score += 100;
                scoreLabel.setText("Score: " + score);
                item.setX(800 + Math.random() * ITEM_SPACING);
                
                }
            }
        }
    

    private void startBattle() {
        inBattle = true;
        battleLabel.setVisible(true);
        enemy.setVisible(true);
        enemyHealthLabel.setVisible(true);
    }

    private void spawnEnemyProjectile(Pane root) {
        Circle projectile = new Circle(enemy.getX() - 10, enemy.getY() + enemy.getFitHeight() / 2, 5);
        projectile.setFill(Color.DARKRED);
        enemyProjectiles.add(projectile);
        root.getChildren().add(projectile);
    }
    
    private void updateProjectiles() {
        Iterator<Circle> iterator = enemyProjectiles.iterator();
        while (iterator.hasNext()) {
            Circle projectile = iterator.next();
            projectile.setCenterX(projectile.getCenterX() - 5);

            if (character.getBoundsInParent().intersects(projectile.getBoundsInParent())) {
                reduceLife();
                projectile.setVisible(false);
                iterator.remove();
            }

            if (projectile.getCenterX() < -10) {
                projectile.setVisible(false);
                iterator.remove();
            }
        }
    }
    
    private void reduceLife() {
        lives--;
        livesLabel.setText("Lives: " + lives);
        if (lives <= 0) {
            gameOver();
        }
    }

    private void gameOver() {
    	gameOver = true;

        // 캐릭터를 게임 오버 상태로 변경
        character.setImage(new Image(getClass().getResourceAsStream("/application/img/loseBy256.gif")));
        character.setFitWidth(RUNNING_WIDTH); // lose GIF 크기 조정
        character.setFitHeight(RUNNING_HEIGHT);

        // 게임 오버 화면으로 전환
        GameOver gameOverScene = new GameOver();
        Stage primaryStage = (Stage) character.getScene().getWindow(); // 현재 Stage 가져오기
        primaryStage.setScene(gameOverScene.createGameOverScene(primaryStage, score)); // 변경된 메서드 사용

    }
}