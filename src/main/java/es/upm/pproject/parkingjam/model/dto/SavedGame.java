package es.upm.pproject.parkingjam.model.dto;

import java.util.ArrayList;
import java.util.List;

public class SavedGame {
    private int currentLevel;
    private String currentLevelName;
    private Board board;
    private int levelScore;
    private int totalScore;
    private boolean finished;
    private List<Move> movementHistory;

    public SavedGame(){
        movementHistory = new ArrayList<>();
    }
    public SavedGame(GameState gameState){
        this.currentLevel = gameState.getCurrentLevel();
        this.currentLevelName = gameState.getCurrentLevelName();
        this.board = gameState.getBoard();
        this.levelScore = gameState.getLevelScore();
        this.totalScore = gameState.getTotalScore();
        this.finished = gameState.isFinished();
        this.movementHistory = new ArrayList<>(gameState.getMovementHistory());
    }
    public GameState toGameState(){
        GameState gameState = new GameState(board, currentLevel, currentLevelName);
        gameState.setLevelScore(levelScore);
        gameState.setTotalScore(totalScore);
        gameState.setFinished(finished);

        for(Move move : movementHistory){
            gameState.addMovement(move);
        }
        return gameState;
    }

    public int getCurrentLevel(){
        return currentLevel;
    }
    public void setCurrentLevel(int currentLevel){
        this.currentLevel = currentLevel;
    }
    public String getCurrentLevelName(){
        return currentLevelName;
    }
    public void setCurrentLevelName(String currentLevelName){
        this.currentLevelName = currentLevelName;
    }
    public Board getBoard(){
        return board;
    }
    public void setBoard(Board board){
        this.board = board;
    }
    public int getLevelScore(){
        return levelScore;
    }
    public void setLevelScore(int levelScore){
        this.levelScore = levelScore;
    }
    public int getTotalScore(){
        return totalScore;
    }
    public void setTotalScore(int totalScore){
        this.totalScore = totalScore;
    }
    public boolean isFinished(){
        return finished;
    }
    public void setFinished(boolean finished){
        this.finished = finished;
    }
    public List<Move> getMovementHistory(){
        return List.copyOf(movementHistory);
    }
    public void setMovementHistory(List<Move> movementHistory){
        if(movementHistory == null){
            this.movementHistory = new ArrayList<>();
        }
        else{
            this.movementHistory = new ArrayList<>(movementHistory);
        }
    }
}
