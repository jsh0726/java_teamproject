package application;

import javafx.scene.image.ImageView;

public class Obstacle {
    private ImageView imageView; // 장애물 이미지
    private boolean hasCollided; // 충돌 여부
    private long collisionTime; // 마지막 충돌 시간

    public Obstacle(ImageView imageView) {
        this.imageView = imageView;
        this.hasCollided = false;
        this.collisionTime = 0;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public boolean hasCollided() {
        return hasCollided;
    }

    public void setCollided(boolean collided) {
        this.hasCollided = collided;
    }

    public long getCollisionTime() {
        return collisionTime;
    }

    public void setCollisionTime(long collisionTime) {
        this.collisionTime = collisionTime;
    }
}
