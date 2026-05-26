package es.upm.pproject.parkingjam.controller;

import es.upm.pproject.parkingjam.model.dao.LevelDAO;
import es.upm.pproject.parkingjam.model.dto.*;
import es.upm.pproject.parkingjam.model.exceptions.LevelDAOException;
import es.upm.pproject.parkingjam.model.services.GameService;
import es.upm.pproject.parkingjam.view.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// Controller that mediates between the model (GameService) and the view (MainView).
public class GameControllerImpl implements GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameControllerImpl.class);

    // Service where the main game logic is located
    private final GameService gameService;
    // UI controlled by this controller
    private final MainView view;
    // DAO used only for loading level files
    private final LevelDAO levelDAO;


    public GameControllerImpl(MainView view, GameService gameService, LevelDAO levelDAO) {
        if (view == null) {
            throw new IllegalArgumentException("MainView cannot be null");
        }
        if (gameService == null) {
            throw new IllegalArgumentException("GameService cannot be null");
        }
        if (levelDAO == null) {
            throw new IllegalArgumentException("LevelDAO cannot be null");
        }

        this.view = view;
        this.gameService = gameService;
        this.levelDAO = levelDAO;
    }


    //---------------------Actions called by the View---------------------

    @Override
    public GameState getGameState() {
        return gameService.getGameState();
    }


    // Starts a brand-new game from level 1.
    // Called by the New Game menu item in MainView.
    @Override
    public void newGame() {
        logger.info("Starting new game");
        try {
            loadLevel(1, 0);
        } catch (LevelDAOException e) {
            logger.error("Error loading first level", e);
            GameDialogs.showLevelError(view, "level_1.txt");
        }
    }


    // Restarts the current level from its initial state.
    //Called by the Reset Level menu item in MainView.
    @Override
    public void restartLevel() {
        try {
            int currentLevel = gameService.getGameState().getCurrentLevel();
            int totalScore = gameService.getGameState().getTotalScore();
            logger.info("Restarting level {}", currentLevel);
            loadLevel(currentLevel, totalScore);
        } catch (LevelDAOException e) {
            logger.error("Error restarting level", e);
            GameDialogs.showLevelError(view, "level_" + gameService.getGameState().getCurrentLevel() + ".txt");
        }
    }


    // Attempts to move a vehicle one step in the given direction.
    // Called by BoardPanel on mouse drag.
    public void move(char vehicleId, Direction direction) {
        // Call service
        boolean moved = gameService.move(vehicleId, direction);
        if (!moved) {
            logger.debug("Move rejected: vehicle='{}' direction='{}'", vehicleId, direction);
            return;
        }

        logger.info("Moved vehicle='{}' direction='{}'", vehicleId, direction);
        updateView();

        if (gameService.isLevelCompleted()) {
            onLevelCompleted();
        }
    }


    // ----------------------Helpers-----------------------------

    private void loadLevel(int levelNumber, int totalScore) throws LevelDAOException {
        Level level = levelDAO.loadLevel("level_" + levelNumber +".txt");
        GameState newGameState = new GameState(
                level.getBoard(),
                levelNumber,
                level.getName()
        );
        newGameState.setTotalScore(totalScore);
        gameService.setGameState(newGameState);
        updateView();
    }

    // Called after a move confirms the level is completed
    private void onLevelCompleted() {
        gameService.finishLevel();

        int nextLevel = gameService.getGameState().getCurrentLevel() + 1;
        int updatedTotal = gameService.getGameState().getTotalScore();

        GameDialogs.showLevelVictory(
                view,
                gameService.getGameState().getCurrentLevelName(),
                gameService.getGameState().getLevelScore(),
                updatedTotal
        );

        try {
            loadLevel(nextLevel, updatedTotal);
        }  catch (LevelDAOException e) {
            // If no more levels to load, win game
            GameDialogs.showVictory(view, updatedTotal);
            logger.info("No more levels to load. Game finished with score: {}", updatedTotal);
        }
    }

    // Pushes the current model state to the view.
    private void updateView() {
        GameState gs = gameService.getGameState();
        view.updateBoard(gs.getBoard());
        view.updateStatus(gs.getCurrentLevelName(), gs.getLevelScore(), gs.getTotalScore());
    }

}
