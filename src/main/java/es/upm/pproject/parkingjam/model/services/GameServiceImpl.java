package es.upm.pproject.parkingjam.model.services;

import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.GameState;

public class GameServiceImpl implements GameService {

    private GameState gameState;
    private final MovementService movementService;
    private final ScoreService scoreService;
    private final VictoryService victoryService;

    public GameServiceImpl() {
        this.movementService = new MovementServiceImpl();
        this.scoreService = new ScoreServiceImpl();
        this.victoryService = new VictoryServiceImpl();
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public void setGameState(GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("gameState must not be null");
        }
        this.gameState = gameState;
    }

    @Override
    public boolean move(char vehicleId, Direction direction) {
        boolean moved = movementService.move(gameState.getBoard(), vehicleId, direction);

        if (moved) {
            gameState.setLevelScore(scoreService.increaseLevelScore(gameState.getLevelScore()));
        }

        return moved;
    }

    @Override
    public boolean isLevelCompleted() {
        return victoryService.isLevelCompleted(gameState.getBoard());
    }

    @Override
    public void finishLevel() {
        int totalScore = scoreService.addLevelScoreToTotalScore(gameState.getTotalScore(),  gameState.getLevelScore());

        gameState.setTotalScore(totalScore);
    }
}
