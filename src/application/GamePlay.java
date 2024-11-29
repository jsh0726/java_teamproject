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
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GamePlay {
    private static final double RUNNING_WIDTH = 100;
    private static final double RUNNING_HEIGHT = 100;
    private static final double JUMPING_WIDTH = 120;
    private static final double JUMPING_HEIGHT = 120;
    
    private boolean isSliding = false; // 슬라이드 상태를 나타내는 변수
    
    private double SCROLL_SPEED = 3; // 초기 스크롤 속도
    private static final int ITEM_SPACING = 50;
    
    private static final int TOTAL_STAGES = 4; // 총 4개의 스테이지
    private int currentStage = 0; // 현재 스테이지
    private static final long STAGE_DURATION = 5L * 1_000_000_000L; // 각 스테이지의 지속 시간 (15초)
    private long stageStartTime; // 각 스테이지 시작 시간 기록
    private List<Image> platformImages;
    private ImageView platform;
    
    private List<ImageView> platforms; // 여러 발판을 관리하는 리스트
    private static final double PLATFORM_HEIGHT = 20; // 발판 높이
    private static final double PLATFORM_SCROLL_SPEED = 2; // 발판 스크롤 속도

    
    private double characterY = 300;
    private double characterVelocityY = 0;
    private boolean isJumping = false;
    private static final double GRAVITY = 0.7;
    private static final double JUMP_STRENGTH = -15;
    
    private ImageView character;
    private ImageView enemy;
    private Rectangle[] obstacles;
    private Label  scoreLabel, battleLabel;
    private boolean gameOver = false;
    private boolean gameClear = false;
    private boolean inBattle = false;
    private int score = 0;
    private int lives = 30;
    private int enemyHealth = 30;
    
    private List<Circle> enemyProjectiles = new ArrayList<>();
    private long startTime; // 게임 시작 시간
    private long lastProjectileTime = 0;
    
    private List<ImageView> items = new ArrayList<>(); // 아이템 리스트
    private long lastItemSpawnTime = 0; // 마지막 아이템 생성 시간
    private static final long ITEM_SPAWN_INTERVAL = 300_000_000L; // 0.3초(나노초 단위)
    
    private List<Image> stageBackgrounds;
    private ImageView background;
    private LifeIndicator lifeIndicator;
    private Label enemyHealthLabel;private Pane root;
    // 체력바 관련 변수
    private ImageView healthBar; // 체력바 이미지
    private int currentHealthState = 6; // 체력 상태를 나타내는 변수 (총 6단계: 30 ~ 0)

    public Scene getScene(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 500);

        // 배경 이미지 리스트 초기화
        stageBackgrounds = new ArrayList<>();
        stageBackgrounds.add(new Image(getClass().getResourceAsStream("/application/img/stage1.png"))); // 스테이지 1 배경
        stageBackgrounds.add(new Image(getClass().getResourceAsStream("/application/img/stage2.png"))); // 스테이지 2 배경
        stageBackgrounds.add(new Image(getClass().getResourceAsStream("/application/img/stage3.png"))); // 스테이지 3 배경
        stageBackgrounds.add(new Image(getClass().getResourceAsStream("/application/img/stage4.png"))); // 스테이지 4 배경 (보스)

        // 초기 배경 이미지 설정
        background = new ImageView(stageBackgrounds.get(0));
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
            double randomY = (Math.random() < 0.5) ? 370 : 320; // Y 좌표를 370 또는 320 중 랜덤으로 선택
            obstacles[i] = new Rectangle(800 + i * 300, randomY, 30, 30);
            obstacles[i].setFill(Color.BLACK);
            root.getChildren().add(obstacles[i]);
        }

        // 라벨 생성
        scoreLabel = new Label("0");
        lifeIndicator = new LifeIndicator(5, root, 640, 20);
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
        initializePlatform(root);

        // 키 입력 처리
        setupKeyHandlers(scene);
        startTime = System.nanoTime(); // 게임 시작 시간 기록
        
        // 게임 루프
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver && !gameClear) {
                	spawnItem(root, now); // 아이템 생성
                    updateItems(root); // 아이템 이동 및 삭제
                    update(now);  // 캐릭터 및 기타 요소 업데이트
                    if (inBattle && now - lastProjectileTime > 2_000_000_000) {
 
                        spawnEnemyProjectile(root);
                        lastProjectileTime = now;
                    }
                }
            }
        };
        timer.start();

        return scene;
    }
    
 // 아이템 생성
    private void spawnItem(Pane root, long now) {
    	if (inBattle) {
            return; // 보스맵에서는 아이템 생성 중단
        }
        if (now - lastItemSpawnTime >= ITEM_SPAWN_INTERVAL) { // 0.3초마다 아이템 생성
            ImageView item = new ImageView(new Image(getClass().getResourceAsStream("/application/img/yakgwa.png")));
            item.setFitWidth(30);
            item.setFitHeight(30);
            item.setX(800); // 시작 X 좌표
            item.setY(350); // 시작 Y 좌표
            items.add(item);
            root.getChildren().add(item);

            lastItemSpawnTime = now; // 마지막 생성 시간 갱신
        }
    }

 // 아이템 이동, 충돌 및 삭제
    private void updateItems(Pane root) {
        Iterator<ImageView> iterator = items.iterator();
        while (iterator.hasNext()) {
            ImageView item = iterator.next();
            item.setX(item.getX() - SCROLL_SPEED); // 아이템 왼쪽으로 이동

            // 충돌 감지
            if (character.getBoundsInParent().intersects(item.getBoundsInParent())) {
                // 점수 증가
                score += 100;
                scoreLabel.setText(" " + score);

                // 아이템 삭제
                root.getChildren().remove(item);
                iterator.remove();
                continue; // 충돌한 아이템은 아래 코드 실행 생략
            }

            // 화면 밖으로 나가면 삭제
            if (item.getX() < -30) {
                root.getChildren().remove(item);
                iterator.remove();
            }
        }
    }
    
    // 발판 초기화 메서드 추가
    private void initializePlatform(Pane root) {
    	// 발판 이미지 리스트 초기화 (높이를 지정하여 생성)
    	platformImages = new ArrayList<>();
        platformImages.add(new Image(getClass().getResourceAsStream("/application/img/scaffolding1.png"))); // 스테이지 1 발판
        platformImages.add(new Image(getClass().getResourceAsStream("/application/img/scaffolding2.png"))); // 스테이지 2 발판
        platformImages.add(new Image(getClass().getResourceAsStream("/application/img/scaffolding3.png"))); // 스테이지 3 발판
        platformImages.add(new Image(getClass().getResourceAsStream("/application/img/scaffolding4.png"))); // 스테이지 4 발판

        // 초기 발판 설정 (맵에서 고정된 위치로 설정)
        platform = new ImageView(platformImages.get(0)); // 첫 번째 발판 이미지 사용
        platform.setFitWidth(800); // 발판 너비 설정 (맵 크기에 맞춤)
        platform.setFitHeight(120);
        platform.setY(395); // 발판의 Y 좌표를 고정 (맵 바닥 근처에 고정)
        platform.setX(0); // 발판의 X 좌표를 고정 (맵 왼쪽에 고정)

        // 발판을 루트 노드에 추가
        root.getChildren().add(platform);
    }

    // 발판 이미지 업데이트 메서드
    private void updatePlatformImage() {
        if (currentStage - 1 < platformImages.size()) {
            platform.setImage(platformImages.get(currentStage - 1));
        }
    }
    
    private void setupLabels(Pane root) {
    	// 점수 배경 이미지
        ImageView scoreBackground = new ImageView(new Image(getClass().getResourceAsStream("/application/img/score.png")));
        scoreBackground.setFitWidth(120); // 배경 크기 조정
        scoreBackground.setFitHeight(40);
        scoreBackground.setLayoutX(500); // X 좌표 설정
        scoreBackground.setLayoutY(20); // y 좌표 설정
        root.getChildren().add(scoreBackground);

        // 점수 라벨
        scoreLabel.setLayoutX(530);
        scoreLabel.setLayoutY(25);
        scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        root.getChildren().add(scoreLabel);

        // 기타 라벨
        setupMessageLabels(root);
    }

    private void setupMessageLabels(Pane root) {
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
        
        // 체력바 이미지 초기화
        healthBar = new ImageView(new Image(getClass().getResourceAsStream("/application/img/health_30.png")));
        healthBar.setFitWidth(150); // 체력바 크기 설정
        healthBar.setFitHeight(80);
        healthBar.setLayoutX(635); // 적 체력바 위치
        healthBar.setLayoutY(195); // 적 체력바 위치
        healthBar.setVisible(false); // 전투 시작 전에는 보이지 않음
        root.getChildren().add(healthBar);
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
    	        try {
    	            character.setImage(new Image(getClass().getResourceAsStream("/application/img/slideBy256.png"))); // 슬라이드 이미지 적용
    	        } catch (Exception e) {
    	            System.out.println("슬라이드 이미지 로드 중 오류: " + e.getMessage());
    	        }
    	        character.setFitWidth(RUNNING_WIDTH); // 러닝 이미지 크기 유지
    	        character.setFitHeight(RUNNING_HEIGHT); // 러닝 이미지 크기 유지
    	        character.setY(350); // 슬라이드 시 Y 좌표를 350으로 고정
    	    }
    	    // 적 공격 로직
    	    if (event.getCode() == KeyCode.A && inBattle && !gameOver && enemy.isVisible()) { 
    	        attackEnemy(); // 적 공격 메서드 호출
    	    }
    	});

    	scene.setOnKeyReleased(event -> {
    	    // 슬라이드에서 복구
    	    if (event.getCode() == KeyCode.S && isSliding) {
    	        isSliding = false;
    	        try {
    	            character.setImage(new Image(getClass().getResourceAsStream("/application/img/runningBy256.gif"))); // 원래 러닝 이미지 복구
    	        } catch (Exception e) {
    	            System.out.println("러닝 이미지 복구 중 오류: " + e.getMessage());
    	        }
    	        character.setY(350); // 원래 Y 좌표로 복구
    	    }
    	});

    }
    
    // 적을 공격하는 메서드
    private void attackEnemy() {
    	 // 적 체력을 1 감소
        enemyHealth--;

     // 체력이 0 이상일 때만 체력 바 업데이트
        if (enemyHealth > 0) {
            updateHealthBar();
        }

        // 적 체력이 0 이하일 경우 승리 처리
        if (enemyHealth <= 0) {
            winBattle();
        }
    }
    
    private void updateHealthBar() {
        // 체력이 0 이상이고 5의 배수일 때만 체력 바 이미지를 업데이트
        if (enemyHealth > 0 && enemyHealth % 5 == 0) {
            int healthState = enemyHealth / 5; // 현재 체력 상태 계산
            if (healthState != currentHealthState) {
                currentHealthState = healthState;
                String healthImagePath = "/application/img/health_" + (healthState * 5) + ".png";
                try {
                    healthBar.setImage(new Image(getClass().getResourceAsStream(healthImagePath)));
                } catch (Exception e) {
                    System.out.println("체력바 이미지 로드 실패: " + healthImagePath);
                }
            }
        }
    }
    
    // 전투에서 승리했을 때 처리
    private void winBattle() {
    	inBattle = false; // 전투 상태 종료
        gameClear = true; // 게임 클리어 상태로 전환

        // 게임 클리어 화면으로 전환
        GameClear gameClearScene = new GameClear();
        Stage primaryStage = (Stage) character.getScene().getWindow(); // 현재 Stage 가져오기
        primaryStage.setScene(gameClearScene.createGameClearScene(primaryStage, score)); // 새로운 클리어 화면으로 전환
    }
    
    private void update(long now) {
    	// 스테이지 전환 조건
        if (!inBattle && now - stageStartTime >= STAGE_DURATION) {
            if (currentStage < TOTAL_STAGES) {
                stageStartTime = now; // 새로운 스테이지 시작 시간 기록
                System.out.println("Stage " + currentStage + " 시작! 속도: " + SCROLL_SPEED);
                currentStage++; // 현재 스테이지 증가
                SCROLL_SPEED += 1; // 스크롤 속도 증가로 난이도 조절
                
                // 배경 이미지 변경
                if (currentStage - 1 < stageBackgrounds.size()) {
                    background.setImage(stageBackgrounds.get(currentStage - 1));
                    updatePlatformImage(); // 발판 이미지 업데이트
                }
                
                if (currentStage == TOTAL_STAGES) {
                    startBattle();
                }
            }
        }

        // 캐릭터 위치 업데이트
        updateCharacterPosition();

        // 게임 상태에 따라 업데이트 처리
        if (!inBattle) {
            handleObstacles();
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
                //System.out.println("Obstacle " + i + " positioned at X: " + newX + ", Y: " + newY);
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
    
    // 생명 5개 깎이면 gameOver
    private void reduceLife() {
    	// lifeLabel 대신 lifeIndicator로 수정
        lifeIndicator.reduceLife();
//        if (lifeIndicator.getLives() <= 0) {
//            gameOver();
//        }
    }
    
    private void startBattle() {
        inBattle = true;
        battleLabel.setVisible(true);
        enemy.setVisible(true);
        healthBar.setVisible(true);
        
        // 장애물 및 아이템 제거
        for (Rectangle obstacle : obstacles) {
            obstacle.setVisible(false); // 장애물 숨김
        }
        for (ImageView item : items) {
            item.setVisible(false); // 아이템 숨김
        }
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