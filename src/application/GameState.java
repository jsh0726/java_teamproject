package application;

public class GameState {
    private int score = 0;
    private int lives = 3;

    public int getScore() {
        return score;
    }

    public void addScore(int value) {
        score += value;
    }

    public int getLives() {
        return lives;
    }

    public void reduceLives() {
        lives--;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }
}
