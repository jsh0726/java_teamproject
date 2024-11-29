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
    
    private double characterY = 300;
    private double characterVelocityY = 0;
    private boolean isJumping = false;
    private static final double GRAVITY = 0.7;
    private static final double JUMP_STRENGTH = -15;
    private static final double SCROLL_SPEED = 5;
    private static final int ITEM_SPACING = 50;
    private static final int MAX_SCORE = 5000;
    private ImageView character;
    private ImageView enemy;
    private Rectangle[] obstacles;
    private List<ImageView> items;
    private Label gameOverLabel, scoreLabel, livesLabel, gameClearLabel, battleLabel;
    private boolean gameOver = false;
    private boolean gameClear = false;
    private boolean inBattle = false;
    private int score = 0;
    private int lives = 5;
    private int enemyHealth = 30;
    private List<Circle> enemyProjectiles = new ArrayList<>();
    private long lastProjectileTime = 0;
    private Label enemyHealthLabel;
    private List<ImageView> platforms = new ArrayList<>();


    public Scene getScene(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 500);

        // 배경 이미지
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/application/img/stage1.png")));
        background.setFitWidth(800);
        background.setFitHeight(500);
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
            obstacles[i] = new Rectangle(800 + i * 300, 370, 30, 30);
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
        livesLabel = new Label("Lives: 5");
        gameOverLabel = new Label("Game Over");
        gameClearLabel = new Label("Game Clear!");
        battleLabel = new Label("Fight the Enemy!");
        enemyHealthLabel = new Label(String.valueOf(enemyHealth));

        // 라벨 설정 및 추가
        setupLabels(root);
        createPlatform(root);

        
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

        // 게임 루프
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver && !gameClear) {
                    update();
                    updatePlatforms();  // 발판 업데이트 호출
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
    
    public void createPlatform(Pane root) {
        int[][] map = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 발판 배열 (1: 발판 있음, 0: 발판 없음)
        };

        int tileSize = 100; // 발판의 너비
        int platformHeight = 20; // 발판의 높이
        int startY = 400; // 발판의 Y 위치

        try {
            Image platformImage = new Image(getClass().getResourceAsStream("/application/img/scaffolding1.png"));

            for (int row = 0; row < map.length; row++) {
                for (int col = 0; col < map[row].length; col++) {
                    if (map[row][col] == 1) {
                        ImageView platform = new ImageView(platformImage);
                        platform.setFitWidth(tileSize);
                        platform.setFitHeight(platformHeight); // 세로 길이 증가
                        platform.setX(col * tileSize); // X 위치 설정
                        platform.setY(startY + row * platformHeight); // Y 위치 설정
                        platforms.add(platform); // 발판을 리스트에 추가
                        root.getChildren().add(platform); // Pane에 발판 추가
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("발판 이미지 로드 실패: " + e.getMessage());
        }
    }

    public void updatePlatforms() {
        for (ImageView platform : platforms) {
            platform.setX(platform.getX() - SCROLL_SPEED); // 발판을 왼쪽으로 이동

            // 발판이 화면 왼쪽으로 벗어나면 오른쪽 끝으로 이동
            if (platform.getX() + platform.getFitWidth() < 0) {
                platform.setX(800); // 화면의 오른쪽 끝으로 이동
            }
        }
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
        // 게임 오버 라벨
//        gameOverLabel.setTextFill(Color.RED);
//        gameOverLabel.setStyle("-fx-font-size: 48px;");
//        gameOverLabel.setLayoutX(250);
//        gameOverLabel.setLayoutY(200);
//        gameOverLabel.setVisible(false);
//        root.getChildren().add(gameOverLabel);

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
            if (event.getCode() == KeyCode.SPACE && !isJumping && !gameOver && !gameClear) {
                characterVelocityY = JUMP_STRENGTH;
                isJumping = true;
                character.setImage(new Image(getClass().getResourceAsStream("/application/img/jumpBy256.gif")));
                character.setFitWidth(JUMPING_WIDTH);
                character.setFitHeight(JUMPING_HEIGHT);
            }
        });
    }

    private void update() {
        boolean onPlatform = false;

        // 캐릭터 위치 업데이트
        characterY += characterVelocityY;
        characterVelocityY += GRAVITY;

        // 발판과의 충돌 확인
        for (ImageView platform : platforms) {
            if (character.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                if (characterVelocityY >= 0) { // 캐릭터가 내려가는 중일 때만 멈추게
                    characterY = platform.getY() - character.getFitHeight(); // 발판 위에 캐릭터 배치
                    characterVelocityY = 0; // 낙하 중지
                    onPlatform = true;
                    isJumping = false; // 점프 상태 해제
                }
            }
        }

        // 바닥 충돌 확인
        if (characterY >= 300 && !onPlatform) {
            characterY = 300;
            characterVelocityY = 0;
            isJumping = false; // 바닥에 닿았으므로 점프 상태 해제
        }

        // 캐릭터가 공중에 있는 상태
        if (!onPlatform && characterY < 300) {
            isJumping = true;
        }

        character.setY(characterY);

        if (!inBattle) {
            handleObstacles();
            handleItems();
        } else {
            updateProjectiles();
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



    private void handleObstacles() {
        for (Rectangle obstacle : obstacles) {
            obstacle.setX(obstacle.getX() - SCROLL_SPEED);
            if (obstacle.getX() < -30) {
                obstacle.setX(800 + Math.random() * 300);
            }
            if (checkCollision(obstacle)) {
                reduceLife();
                obstacle.setX(800 + Math.random() * 300);
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
                if (score >= MAX_SCORE) {
                    startBattle();
                }
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


}