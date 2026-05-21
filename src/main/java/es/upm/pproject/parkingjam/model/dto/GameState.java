package es.upm.pproject.parkingjam.model.dto;

public class GameState {
    private Board board;
    private int currentLevel;
    private String currentLevelName;
    private int levelScore;
    private int totalScore;
    private boolean finished;

    public GameState(Board board, int currentLevel, String currentLevelName) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        if (currentLevel <= 0) {
            throw new IllegalArgumentException("Current level cannot be less than 1");
        }
        if (currentLevelName == null || currentLevelName.isEmpty()) {
            throw new IllegalArgumentException("Current level name cannot be null  or empty");
        }
        this.board = board;
        this.currentLevel = currentLevel;
        this.currentLevelName = currentLevelName;
        this.levelScore = 0;
        this.totalScore = 0;
        this.finished = false;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        this.board = board;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        if (currentLevel <= 0) {
            throw new IllegalArgumentException("Current level cannot be less than 1");
        }
        this.currentLevel = currentLevel;
    }

    public String getCurrentLevelName() {
        return currentLevelName;
    }

    public void setCurrentLevelName(String currentLevelName) {
        // Checks level name matches level_1.txt, level_2.txt, etc. format
        if (!currentLevelName.matches("level_[1-9]\\d*\\.txt")) {
            throw new IllegalArgumentException("Current level name cannot be null or empty");
        }
        // Checks level name is not empty or blank
        if (currentLevelName.isBlank()) {
            throw new IllegalArgumentException("Current level name cannot be null or empty");
        }
        this.currentLevelName = currentLevelName;
    }

    public int getLevelScore() {
        return levelScore;
    }

    public void setLevelScore(int levelScore) {
        if (levelScore < 0) {
            throw new IllegalArgumentException("Level score cannot be negative");
        }
        this.levelScore = levelScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        if (totalScore < 0) {
            throw new IllegalArgumentException("Total score cannot be negative");
        }
        this.totalScore = totalScore;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
