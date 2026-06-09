package es.upm.pproject.parkingjam.model.services;

import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.GameState;
import es.upm.pproject.parkingjam.model.dto.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServiceImpl implements GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

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
        logger.info("Game state set to level '{}' (Level {})", gameState.getCurrentLevelName(), gameState.getCurrentLevel());
    }

    @Override
    public boolean move(char vehicleId, Direction direction) {
        boolean moved = movementService.move(gameState.getBoard(), vehicleId, direction);

        if (moved) {
            gameState.setLevelScore(scoreService.increaseLevelScore(gameState.getLevelScore()));
            logger.info("Vehicle '{}' moved {} - Level score increased to {}", vehicleId, direction, gameState.getLevelScore());
            gameState.addMovement(new Move(vehicleId, direction)); // Store move in memory
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
        logger.info("Level '{}' finished. Level score: {}, Total score: {}", gameState.getCurrentLevelName(), gameState.getLevelScore(), totalScore);
    }

    @Override
    public boolean undoLastMovement() {
        if (!gameState.hasMoves()) {
            return false;
        }

        Move lastMove = gameState.removeLastMove();

        boolean undoMovement = movementService.move(gameState.getBoard(), lastMove.getVehicleId(), lastMove.getDirection().getOppositeDirection());

        if (undoMovement) {
            gameState.setLevelScore(scoreService.decreaseLevelScore(gameState.getLevelScore()));
        }
        return undoMovement;
    }
}
