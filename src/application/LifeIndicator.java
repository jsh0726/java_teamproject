package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;

public class LifeIndicator {
    private int lives;
    private final ImageView lifeImageView;
    private final String[] lifeImages; // 생명 상태별 이미지 경로

    public LifeIndicator(int initialLives, Pane root, double x, double y) {
        this.lives = initialLives;
        this.lifeImages = new String[]{
            "/application/img/zero.png",  
            "/application/img/one.png", 
            "/application/img/two.png",
            "/application/img/three.png", 
            "/application/img/four.png",  
            "/application/img/five.png" 
        };

        this.lifeImageView = new ImageView(new Image(getClass().getResourceAsStream(lifeImages[initialLives])));
        lifeImageView.setFitWidth(120); // 이미지 크기 조정
        lifeImageView.setFitHeight(40);
        lifeImageView.setX(x); // 전달받은 x 좌표
        lifeImageView.setY(y); // 전달받은 y 좌표

        root.getChildren().add(lifeImageView);
    }

    public void reduceLife() {
        if (lives > 0) {
            lives--;
            updateLifeImage();
        }
    }

    private void updateLifeImage() {
        // 생명 상태에 따라 이미지 업데이트
        lifeImageView.setImage(new Image(getClass().getResourceAsStream(lifeImages[lives])));
    }

    public int getLives() {
        return lives;
    }
}
