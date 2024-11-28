//package application;
//
//import javafx.animation.AnimationTimer;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.KeyCode;
//import javafx.scene.layout.Pane;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.Rectangle;
//import javafx.stage.Stage;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class Testda extends Application {
//    private static final double RUNNING_WIDTH = 100; // 러닝 이미지 너비
//    private static final double RUNNING_HEIGHT = 100; // 러닝 이미지 높이
//    private static final double JUMPING_WIDTH = 120; // 점프 이미지 너비
//    private static final double JUMPING_HEIGHT = 120; // 점프 이미지 높이
//
//    private double characterY = 300; // 캐릭터 Y 좌표
//    private double characterVelocityY = 0; // 캐릭터 수직 속도
//    private boolean isJumping = false; // 점프 중인지 여부
//    private ImageView character; // 캐릭터 이미지
//    private ImageView enemy; // 적 이미지로 변경
//    private Rectangle[] obstacles; // 장애물 배열
//    private List<ImageView> items; // 아이템 리스트
//    private Label gameOverLabel, scoreLabel, livesLabel, gameClearLabel, battleLabel; // 화면에 표시할 라벨들
//    private boolean gameOver = false; // 게임 오버 상태
//    private boolean gameClear = false; // 게임 클리어 상태
//    private boolean inBattle = false; // 전투 모드 여부
//    private int score = 0; // 현재 점수
//    private int lives = 3; // 남은 생명 수
//    private static final double GRAVITY = 0.7; // 중력 상수
//    private static final double JUMP_STRENGTH = -15; // 점프 강도
//    private static final double SCROLL_SPEED = 5; // 화면 스크롤 속도
//    private static final int ITEM_SPACING = 50; // 아이템 간 간격
//    private static final int MAX_SCORE = 5000; // 전투 모드로 들어가기 위한 최대 점수
//    private static final int MIN_OBSTACLE_SPACING = 100; // 장애물 간 최소 간격
//    private int enemyHealth = 30; // 적 체력
//    private double initialEnemyWidth = 50; // 적의 초기 너비
//    private List<Circle> enemyProjectiles = new ArrayList<>(); // 적의 발사체 리스트
//    private long lastProjectileTime = 0; // 마지막 발사체 발사 시간
//    private Label enemyHealthLabel; // 적 체력을 표시할 라벨
//
//    @Override
//    public void start(Stage primaryStage) {
//        Pane root = new Pane();
//        Scene scene = new Scene(root, 896, 512);
//
//        // 배경 이미지 추가
//        ImageView background;
//        try {
//            Image backgroundImage = new Image(getClass().getResourceAsStream("/application/img/daenamu.png"));
//            background = new ImageView(backgroundImage);
//            background.setFitWidth(896);
//            background.setFitHeight(512);
//        } catch (Exception e) {
//            System.out.println("배경 이미지 파일을 찾을 수 없습니다.");
//            e.printStackTrace(); // 디버깅을 위해 에러 메시지 출력
//            return;
//        }
//
//        root.getChildren().add(background); // 배경을 가장 먼저 추가 (맨 뒤 레이어)
//
//        // 캐릭터 GIF 이미지 생성 및 추가
//        try {
//            Image characterImage = new Image(getClass().getResourceAsStream("/application/img/runningBy256.gif"));
//            character = new ImageView(characterImage);
//            character.setFitWidth(100); // 캐릭터의 크기를 조정
//            character.setFitHeight(100);
//            character.setX(50); // 캐릭터 초기 위치 설정
//            character.setY(characterY);
//        } catch (Exception e) {
//            System.out.println("캐릭터 이미지 파일을 찾을 수 없습니다.");
//            e.printStackTrace();
//            return;
//        }
//        root.getChildren().add(character);
//
//
//        // 땅 생성 및 추가
//        Rectangle ground = new Rectangle(0, 400, 896, 150);
//        ground.setFill(Color.GREEN);
//        root.getChildren().add(ground);
//
//        // 장애물 생성 및 추가
//        obstacles = new Rectangle[3];
//        for (int i = 0; i < obstacles.length; i++) {
//            obstacles[i] = new Rectangle(800 + i * 300, 370, 30, 30);
//            obstacles[i].setFill(Color.BLACK);
//            root.getChildren().add(obstacles[i]);
//        }
//
//        // 아이템 이미지 로드
//        Image coinImage, arrowImage, orbImage;
//        try {
//            coinImage = new Image(getClass().getResourceAsStream("/application/img/yakgwa.png"));
//            arrowImage = new Image(getClass().getResourceAsStream("/application/img/arrow.png"));
//            orbImage = new Image(getClass().getResourceAsStream("/application/img/ball.png"));
//        } catch (Exception e) {
//            System.out.println("아이템 이미지 파일을 찾을 수 없습니다.");
//            e.printStackTrace();
//            return;
//        }
//
//        // 아이템 리스트 생성 및 초기화
//        items = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ImageView coin = new ImageView(coinImage);
//            coin.setFitWidth(30);
//            coin.setFitHeight(30);
//            items.add(coin);
//
//            ImageView arrow = new ImageView(arrowImage);
//            arrow.setFitWidth(30);
//            arrow.setFitHeight(30);
//            items.add(arrow);
//
//            ImageView orb = new ImageView(orbImage);
//            orb.setFitWidth(30);
//            orb.setFitHeight(30);
//            items.add(orb);
//        }
//
//
//        // 아이템을 일정 간격으로 배치
//        for (int i = 0; i < items.size(); i++) {
//            items.get(i).setX(i * ITEM_SPACING);
//            items.get(i).setY(350); // 아이템의 Y 위치를 땅 위로 조정
//            root.getChildren().add(items.get(i));
//        }
//
//        // 점수와 생명 표시 라벨 생성 및 추가
//        scoreLabel = new Label("Score: 0");
//        scoreLabel.setLayoutX(600);
//        scoreLabel.setLayoutY(20);
//        scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
//        root.getChildren().add(scoreLabel);
//
//        livesLabel = new Label("Lives: 3");
//        livesLabel.setLayoutX(700);
//        livesLabel.setLayoutY(20);
//        livesLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
//        root.getChildren().add(livesLabel);
//
//        // 게임 오버 메시지 생성 및 추가
//        gameOverLabel = new Label("Game Over");
//        gameOverLabel.setTextFill(Color.RED);
//        gameOverLabel.setStyle("-fx-font-size: 48px;");
//        gameOverLabel.setLayoutX(250);
//        gameOverLabel.setLayoutY(200);
//        gameOverLabel.setVisible(false);
//        root.getChildren().add(gameOverLabel);
//
//        // 게임 클리어 메시지 생성 및 추가
//        gameClearLabel = new Label("Game Clear!");
//        gameClearLabel.setTextFill(Color.BLUE);
//        gameClearLabel.setStyle("-fx-font-size: 48px;");
//        gameClearLabel.setLayoutX(250);
//        gameClearLabel.setLayoutY(200);
//        gameClearLabel.setVisible(false);
//        root.getChildren().add(gameClearLabel);
//
//        // 전투 시작 알림 메시지 생성 및 추가
//        battleLabel = new Label("Fight the Enemy!");
//        battleLabel.setTextFill(Color.PURPLE);
//        battleLabel.setStyle("-fx-font-size: 32px;");
//        battleLabel.setLayoutX(300);
//        battleLabel.setLayoutY(50);
//        battleLabel.setVisible(false);
//        root.getChildren().add(battleLabel);
//
//        // 적 초기화
//        try {
//            Image enemyImage = new Image(getClass().getResourceAsStream("/application/img/boss.gif"));
//            enemy = new ImageView(enemyImage);
//            enemy.setFitWidth(150);
//            enemy.setFitHeight(150);
//            enemy.setX(650); // 적 초기 위치
//            enemy.setY(250);
//            enemy.setVisible(false); // 전투 시작 전까지 숨김
//            root.getChildren().add(enemy);
//        } catch (Exception e) {
//            System.out.println("적 이미지 파일을 찾을 수 없습니다.");
//            e.printStackTrace();
//        }
//        // 적 체력 라벨 초기화
//        enemyHealthLabel = new Label(String.valueOf(enemyHealth));
//        enemyHealthLabel.setLayoutX(750); // 적 위치와 동일한 X 좌표
//        enemyHealthLabel.setLayoutY(280); // 적 위에 표시되도록 Y 좌표 조정
//        enemyHealthLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: red; -fx-font-weight: bold;");
//        enemyHealthLabel.setVisible(false); // 전투 시작 전에는 보이지 않도록 설정
//        root.getChildren().add(enemyHealthLabel);
//
//        // 키 입력 처리
//        scene.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.SPACE && !isJumping && !gameOver && !gameClear) {
//                try {
//                    // 점프 이미지 로드
//                    Image jumpImage = new Image(getClass().getResourceAsStream("/application/img/jumpBy256.gif"));
//                    character.setImage(jumpImage);
//                    character.setFitWidth(JUMPING_WIDTH);
//                    character.setFitHeight(JUMPING_HEIGHT);
//                } catch (Exception e) {
//                    System.out.println("점프 이미지 파일을 찾을 수 없습니다.");
//                    e.printStackTrace();
//                }
//                characterVelocityY = JUMP_STRENGTH;
//                isJumping = true;
//            }
//        });
//
//        // 게임 루프 - 매 프레임마다 게임 상태 업데이트
//        AnimationTimer timer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                if (!gameOver && !gameClear) {
//                    update();
//                    if (inBattle && now - lastProjectileTime > 1_000_000_000) {
//                        spawnEnemyProjectile(root);
//                        lastProjectileTime = now;
//                    }
//                }
//            }
//        };
//        timer.start();
//
//        primaryStage.setTitle("Running Game with GIF Character");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void update() {
//        // 캐릭터 위치 업데이트 (중력 적용)
//        characterY += characterVelocityY;
//        characterVelocityY += GRAVITY;
//
//        if (characterY >= 300) {
//            characterY = 300;
//            characterVelocityY = 0;
//
//            if (isJumping) {
//                isJumping = false;
//
//                try {
//                    // 러닝 이미지 복원
//                    Image runningImage = new Image(getClass().getResourceAsStream("/application/img/runningBy256.gif"));
//                    character.setImage(runningImage);
//                    character.setFitWidth(RUNNING_WIDTH);
//                    character.setFitHeight(RUNNING_HEIGHT);
//                } catch (Exception e) {
//                    System.out.println("캐릭터 러닝 이미지 파일을 찾을 수 없습니다.");
//                    e.printStackTrace();
//                }
//            }
//        }
//    
//
//        
//
//
//        character.setY(characterY);
//
//        if (!inBattle) {
//            // 장애물 이동 및 충돌 처리
//            for (Rectangle obstacle : obstacles) {
//                obstacle.setX(obstacle.getX() - SCROLL_SPEED);
//                if (obstacle.getX() < -30) {
//                    obstacle.setX(800 + Math.random() * 500);
//                }
//
//                // 캐릭터와 장애물이 충돌하면 생명 감소
//                if (character.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
//                    reduceLife();
//                    obstacle.setX(800 + Math.random() * 500);
//                }
//            }
//
//            // 아이템 이동 및 충돌 처리
//            for (int i = 0; i < items.size(); i++) {
//                ImageView item = items.get(i);
//                item.setX(item.getX() - SCROLL_SPEED);
//
//                // 화면을 벗어난 아이템을 오른쪽으로 재배치
//                if (item.getX() < -30) {
//                    item.setX(800 + i * ITEM_SPACING);
//                }
//
//                // 캐릭터와 아이템이 충돌하면 점수 증가 및 위치 재설정
//                if (character.getBoundsInParent().intersects(item.getBoundsInParent())) {
//                    score += 100;
//                    scoreLabel.setText("Score: " + score);
//                    item.setX(800 + i * ITEM_SPACING);
//
//                    // 최대 점수에 도달 시 전투 모드로 전환
//                    if (score >= MAX_SCORE) {
//                        startBattle();
//                    }
//                }
//            }
//        } else {
//            // 전투 모드에서 적 공격 업데이트
//            if (enemyHealth <= 0) {
//                winBattle();
//            }
//            updateProjectiles();
//        }
//    }
//
//    private void startBattle() {
//        inBattle = true;
//        battleLabel.setVisible(true);
//        if (enemy != null) {
//            enemy.setVisible(true); // 적 표시
//        }
//        enemyHealthLabel.setVisible(true); // 체력 라벨 표시
//    }
//    private void attackEnemy() {
//        enemyHealth--; // 체력 감소
//        if (enemyHealth > 0) {
//            enemyHealthLabel.setText(String.valueOf(enemyHealth)); // 체력 라벨 업데이트
//        } else {
//            enemyHealthLabel.setText("0"); // 체력이 0이면 0으로 표시
//            winBattle(); // 승리 처리
//        }
//    }
//
//
//    private void winBattle() {
//        inBattle = false;
//        enemy.setVisible(false);
//        enemyHealthLabel.setVisible(false); // 체력 라벨 숨기기
//        battleLabel.setVisible(false);
//        gameClearLabel.setVisible(true);
//        gameClear = true;
//    }
//
//    private void reduceLife() {
//        lives--;
//        livesLabel.setText("Lives: " + lives);
//        if (lives <= 0) {
//            gameOver();
//        }
//    }
//
//    private void gameOver() {
//        gameOver = true;
//        gameOverLabel.setVisible(true);
//    }
//
//    private void spawnEnemyProjectile(Pane root) {
//        if (enemy == null || !enemy.isVisible()) {
//            // enemy가 null이거나 보이지 않을 경우, 발사체 생성 방지
//            return;
//        }
//
//        // 적 발사체 생성
//        Circle projectile = new Circle(
//            enemy.getX() - 10,
//            enemy.getY() + enemy.getFitHeight() / 2,
//            5
//        );
//        projectile.setFill(Color.DARKRED);
//        enemyProjectiles.add(projectile);
//        root.getChildren().add(projectile);
//    }
//
//    private void updateProjectiles() {
//        Iterator<Circle> iterator = enemyProjectiles.iterator();
//        while (iterator.hasNext()) {
//            Circle projectile = iterator.next();
//            projectile.setCenterX(projectile.getCenterX() - 5);
//
//            // 캐릭터와 발사체가 충돌하면 생명 감소
//            if (character.getBoundsInParent().intersects(projectile.getBoundsInParent())) {
//                reduceLife();
//                projectile.setVisible(false);
//                iterator.remove();
//                continue;
//            }
//
//            // 화면을 벗어난 발사체 제거
//            if (projectile.getCenterX() < -10) {
//                projectile.setVisible(false);
//                iterator.remove();
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}