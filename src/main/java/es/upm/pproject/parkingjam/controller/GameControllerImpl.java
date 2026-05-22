package es.upm.pproject.parkingjam.controller;

import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.GameState;
import es.upm.pproject.parkingjam.model.services.GameService;
import es.upm.pproject.parkingjam.view.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Vehicle;


/**
 * Controller that mediates between the model (GameService) and the view (MainView).
 *
 * Follows the same pattern as UsersController from the professor's example:
 *   - Receives the concrete View in the constructor
 *   - Calls the model services
 *   - Updates the view with the results
 */
public class GameControllerImpl implements GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    // Service where the main logic is located
    private GameService gameService;
    // UI
    private final MainView view =  new MainView(); //modify

    //LevelLoader levelLoader ??;

    // Initial board, kept for restart
    private Board initialBoard;



    public GameControllerImpl(MainView view) {
        //this(view, new LevelLoader());
    }

//    /** Package-private constructor that allows injecting a custom loader (useful in tests). */
//    GameControllerImpl(MainView view, LevelLoader levelLoader) {
//        this.view = view;
//        this.levelLoader = levelLoader;
//        //loadLevel(1, 0);
//    }



    //  Actions called by the View


    /*
     * Attempts to move a vehicle one step in the given direction.
     * Called by BoardPanel on mouse drag.
     */
    public boolean move(char vehicleId, Direction direction) {
        GameState gs = gameService.getGameState();
        Vehicle vehicle = gs.getBoard().getVehicle(vehicleId).orElse(null);
        if (vehicle == null) {
            logger.warn("move(): unknown vehicle '{}'", vehicleId);
            return false;
        }

        // Call service
        boolean moved = gameService.move(vehicleId, direction);
        if (!moved) {
            logger.debug("Move rejected: vehicle='{}' direction='{}'", vehicleId, direction);
            return false;
        }

        logger.info("Moved vehicle='{}' direction='{}'", vehicleId, direction);
        updateView();
        if (gameService.isLevelCompleted()) {
            onLevelCompleted();
        }
        return true;   
    }


    /**
     * Restarts the current level from its initial state.
     * Called by the Reset Level menu item in MainView.
     */
    public void restartLevel() {
        int currentLevel = gameService.getGameState().getCurrentLevel();
        int totalScore   = gameService.getGameState().getTotalScore();
        logger.info("Restarting level {}", currentLevel);
        //loadLevel(currentLevel, totalScore);
    }

    /**
     * Starts a brand-new game from level 1.
     * Called by the New Game menu item in MainView.
     */
    public void newGame() {
        logger.info("Starting new game");
        //loadLevel(1, 0);
    }




    @Override
    public GameState getGameState() {
        return gameService.getGameState();
    }

    // ------------------------------------------------------------------ //
    //  Internal helpers                                                    //
    // ------------------------------------------------------------------ //


    

    /** Called after a move confirms the level is completed. */
    private void onLevelCompleted() {
        gameService.finishLevel();
        int nextLevel = gameService.getGameState().getCurrentLevel() + 1;
        int updatedTotal = gameService.getGameState().getTotalScore();
        GameDialogs.showLevelVictory(view, gameService.getGameState().getCurrentLevelName(), gameService.getGameState().getLevelScore(), updatedTotal);
        logger.info("Level completed! Advancing to {}. Total score: {}", nextLevel, updatedTotal);
        //loadLevel(nextLevel, updatedTotal);
    }

    /**
     * Pushes the current model state to the view.
     * Mirrors what UsersController does with updateUsersList().
     */
    private void updateView() {
        GameState gs = gameService.getGameState();
        view.updateBoard(gs.getBoard());
        view.updateStatus(gs.getCurrentLevelName(), gs.getLevelScore(), gs.getTotalScore());
    }

}
