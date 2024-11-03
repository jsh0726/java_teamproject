package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
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

public class GamePlay extends Application {
    private double characterY = 300;
    private double characterVelocityY = 0;
    private boolean isJumping = false;
    private Rectangle character;
    private Rectangle[] obstacles;
    private List<ImageView> items;
    private Label gameOverLabel, scoreLabel, livesLabel, gameClearLabel, battleLabel;
    private boolean gameOver = false;
    private boolean gameClear = false;
    private boolean inBattle = false;
    private int score = 0;
    private int lives = 3;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -12;
    private static final double SCROLL_SPEED = 3;
    private static final int ITEM_SPACING = 150;
    private static final int MAX_SCORE = 5000;
    private Rectangle enemy;
    private int enemyHealth = 30;
    private double initialEnemyWidth = 50;
    private List<Circle> enemyProjectiles = new ArrayList<>();
    private long lastProjectileTime = 0;

    public Scene getScene(Stage primaryStage) {
        Pane root = new Pane();

        // 캐릭터 생성 및 추가
        character = new Rectangle(50, 300, 30, 30);
        character.setFill(Color.RED);
        root.getChildren().add(character);

        // 땅 생성 및 추가
        Rectangle ground = new Rectangle(0, 350, 800, 150);
        ground.setFill(Color.GREEN);
        root.getChildren().add(ground);

        // 장애물 생성 및 추가
        obstacles = new Rectangle[3];
        for (int i = 0; i < obstacles.length; i++) {
            obstacles[i] = new Rectangle(800 + i * 300, 320, 30, 30);
            obstacles[i].setFill(Color.BLACK);
            root.getChildren().add(obstacles[i]);
        }

        // 아이템 이미지 로드
        Image coinImage, arrowImage, orbImage;
        try {
            coinImage = new Image(getClass().getResource("/application/yakgwa.png").toExternalForm());
            arrowImage = new Image(getClass().getResource("/application/arrow.png").toExternalForm());
            orbImage = new Image(getClass().getResource("/application/ball.png").toExternalForm());
        } catch (Exception e) {
            System.out.println("이미지 파일을 찾을 수 없습니다.");
            return null;
        }

        // 아이템 리스트 생성 및 초기화
        items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ImageView coin = new ImageView(coinImage);
            coin.setFitWidth(30);
            coin.setFitHeight(30);
            items.add(coin);

            ImageView arrow = new ImageView(arrowImage);
            arrow.setFitWidth(30);
            arrow.setFitHeight(30);
            items.add(arrow);

            ImageView orb = new ImageView(orbImage);
            orb.setFitWidth(30);
            orb.setFitHeight(30);
            items.add(orb);
        }

        // 아이템을 일정 간격으로 배치
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setX(800 + i * ITEM_SPACING);
            items.get(i).setY(300);
            root.getChildren().add(items.get(i));
        }

        // 점수와 생명 표시 라벨 생성 및 추가
        scoreLabel = new Label("Score: 0");
        scoreLabel.setLayoutX(600);
        scoreLabel.setLayoutY(20);
        scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        root.getChildren().add(scoreLabel);

        livesLabel = new Label("Lives: 3");
        livesLabel.setLayoutX(700);
        livesLabel.setLayoutY(20);
        livesLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        root.getChildren().add(livesLabel);

        // 게임 오버 메시지 생성 및 추가
        gameOverLabel = new Label("Game Over");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setStyle("-fx-font-size: 48px;");
        gameOverLabel.setLayoutX(250);
        gameOverLabel.setLayoutY(200);
        gameOverLabel.setVisible(false);
        root.getChildren().add(gameOverLabel);

        // 게임 클리어 메시지 생성 및 추가
        gameClearLabel = new Label("Game Clear!");
        gameClearLabel.setTextFill(Color.BLUE);
        gameClearLabel.setStyle("-fx-font-size: 48px;");
        gameClearLabel.setLayoutX(250);
        gameClearLabel.setLayoutY(200);
        gameClearLabel.setVisible(false);
        root.getChildren().add(gameClearLabel);

        // 전투 시작 알림 메시지 생성 및 추가
        battleLabel = new Label("Fight the Enemy!");
        battleLabel.setTextFill(Color.PURPLE);
        battleLabel.setStyle("-fx-font-size: 32px;");
        battleLabel.setLayoutX(300);
        battleLabel.setLayoutY(50);
        battleLabel.setVisible(false);
        root.getChildren().add(battleLabel);

        // 적 생성
        enemy = new Rectangle(750, 300, initialEnemyWidth, 50);
        enemy.setFill(Color.DARKRED);
        enemy.setVisible(false);
        root.getChildren().add(enemy);

        // 키 입력 처리
        Scene scene = new Scene(root, 800, 500);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE && !isJumping && !gameOver && !gameClear) {
                characterVelocityY = JUMP_STRENGTH;
                isJumping = true;
            } else if (event.getCode() == KeyCode.A && inBattle && !gameOver) {
                attackEnemy();
            }
        });

        // 게임 루프 - 매 프레임마다 게임 상태 업데이트
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

    private void update() {
        characterY += characterVelocityY;
        characterVelocityY += GRAVITY;

        if (characterY >= 300) {
            characterY = 300;
            characterVelocityY = 0;
            isJumping = false;
        }

        character.setY(characterY);

        if (!inBattle) {
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

            for (int i = 0; i < items.size(); i++) {
                ImageView item = items.get(i);
                item.setX(item.getX() - SCROLL_SPEED);

                if (item.getX() < -30) {
                    item.setX(800 + i * ITEM_SPACING);
                }

                if (character.getBoundsInParent().intersects(item.getBoundsInParent())) {
                    score += 100;
                    scoreLabel.setText("Score: " + score);
                    item.setX(800 + i * ITEM_SPACING);

                    if (score >= MAX_SCORE) {
                        startBattle();
                    }
                }
            }
        } else {
            if (enemyHealth <= 0) {
                winBattle();
            }
            updateProjectiles();
        }
    }

    private void startBattle() {
        inBattle = true;
        battleLabel.setVisible(true);
        enemy.setVisible(true);

        for (Rectangle obstacle : obstacles) {
            obstacle.setVisible(false);
        }
        for (ImageView item : items) {
            item.setVisible(false);
        }
    }

    private void attackEnemy() {
        enemyHealth--;
        if (enemyHealth > 0) {
            enemy.setWidth(initialEnemyWidth * enemyHealth / 30.0);
        } else {
            winBattle();
        }
    }

    private void winBattle() {
        inBattle = false;
        enemy.setVisible(false);
        battleLabel.setVisible(false);
        gameClearLabel.setVisible(true);
        gameClear = true;
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

    private void spawnEnemyProjectile(Pane root) {
        Circle projectile = new Circle(enemy.getX() - 10, enemy.getY() + enemy.getHeight() / 2, 5);
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
                continue;
            }

            if (projectile.getCenterX() < -10) {
                projectile.setVisible(false);
                iterator.remove();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}