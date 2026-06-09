package es.upm.pproject.parkingjam.model.dto;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Board board;
    private int currentLevel;
    private String currentLevelName;
    private int levelScore;
    private int totalScore;
    private boolean finished;
    private List<Move> movementHistory;

    public GameState(Board board, int currentLevel, String currentLevelName) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        if (currentLevel <= 0) {
            throw new IllegalArgumentException("Current level cannot be less than 1");
        }
        if (currentLevelName == null || currentLevelName.isBlank()) {
            throw new IllegalArgumentException("Current level name cannot be null, blank or empty");
        }
        this.board = board;
        this.currentLevel = currentLevel;
        this.currentLevelName = currentLevelName;
        this.levelScore = 0;
        this.totalScore = 0;
        this.finished = false;
        this.movementHistory = new ArrayList<>();
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

    public void setCurrentLevelName(String newLevelName) {
        // Checks level name is not null, empty or blank
        if (newLevelName == null || newLevelName.isBlank()) {
            throw new IllegalArgumentException("Current level name cannot be null, empty or blank");
        }
        this.currentLevelName = newLevelName;
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

    public void addMovement(Move movement) {
        if (movement == null) {
            throw new IllegalArgumentException("Movement cannot be null");
        }
        movementHistory.add(movement);
    }

    public boolean hasMoves() {
        return !movementHistory.isEmpty();
    }

    public Move removeLastMove() {
        if (movementHistory.isEmpty()) {
            throw new IllegalArgumentException("No movements to undo");
        }
        return movementHistory.remove(movementHistory.size() - 1);
    }

    public List<Move> getMovementHistory() {
        return List.copyOf(movementHistory);
    }
}
