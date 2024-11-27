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
    private static final double GRAVITY = 0.7;
    private static final double JUMP_STRENGTH = -15;
    private static final double SCROLL_SPEED = 5;
    private static final int ITEM_SPACING = 50;
    private static final int MAX_SCORE = 5000;

    private double characterY = 300;
    private double characterVelocityY = 0;
    private boolean isJumping = false;
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
    private long lastProjectileTime = 0;
    private Label enemyHealthLabel;

    public Scene getScene(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 896, 512);

        // 배경 이미지
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/application/img/daenamu.png")));
        background.setFitWidth(896);
        background.setFitHeight(512);
        root.getChildren().add(background);

        // 캐릭터 이미지
        character = new ImageView(new Image(getClass().getResourceAsStream("/application/img/runningBy256.gif")));
        character.setFitWidth(RUNNING_WIDTH);
        character.setFitHeight(RUNNING_HEIGHT);
        character.setX(50);
        character.setY(characterY);
        root.getChildren().add(character);

        // 장애물
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

        // 라벨
        scoreLabel = new Label("Score: 0");
        livesLabel = new Label("Lives: 3");
        gameOverLabel = new Label("Game Over");
        gameClearLabel = new Label("Game Clear!");
        battleLabel = new Label("Fight the Enemy!");
        enemyHealthLabel = new Label(String.valueOf(enemyHealth));

        // 라벨 설정 및 추가
        setupLabels(root);

        // 키 입력 처리
        setupKeyHandlers(scene);

        // 게임 루프
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver && !gameClear) {
                    update();
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
        // 게임 오버 라벨
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setStyle("-fx-font-size: 48px;");
        gameOverLabel.setLayoutX(250);
        gameOverLabel.setLayoutY(200);
        gameOverLabel.setVisible(false);
        root.getChildren().add(gameOverLabel);

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
        // 캐릭터 위치 업데이트
        characterY += characterVelocityY;
        characterVelocityY += GRAVITY;
        if (characterY >= 300) {
            characterY = 300;
            characterVelocityY = 0;
            isJumping = false;
            character.setImage(new Image(getClass().getResourceAsStream("/application/img/runningBy256.gif")));
            character.setFitWidth(RUNNING_WIDTH);
            character.setFitHeight(RUNNING_HEIGHT);
        }
        character.setY(characterY);

        // 장애물 및 아이템 충돌 처리
        handleObstacles();
        handleItems();
    }

    private void handleObstacles() {
        for (Rectangle obstacle : obstacles) {
            obstacle.setX(obstacle.getX() - SCROLL_SPEED);
            if (obstacle.getX() < -30) {
                obstacle.setX(800 + Math.random() * 300);
            }
            if (character.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                reduceLife();
                obstacle.setX(800 + Math.random() * 300);
            }
        }
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

    private void reduceLife() {
        lives--;
        livesLabel.setText("Lives: " + lives);
        if (lives <= 0) {
            gameOver();
        }
    }

    private void gameOver() {
        gameOver = true;
        gameOverLabel.setVisible(true);
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
}
