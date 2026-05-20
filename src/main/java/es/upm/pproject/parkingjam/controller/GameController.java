package es.upm.pproject.parkingjam.controller;

import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.GameState;

import java.io.File;

/**
 * Controller interface that mediates between the view and the model.
 * Handles all game actions: movement, level loading, save/load.
 */
public interface GameController {

    /**
     * Returns the current game state.
     */
    GameState getGameState();

    /**
     * Attempts to move a vehicle in the given direction.
     * After a successful move, checks if the level is completed.
     *
     * @param vehicleId the character identifier of the vehicle
     * @param direction the direction of the movement
     * @return true if the vehicle was moved successfully
     */
    boolean move(char vehicleId, Direction direction);

    /**
     * Restarts the current level from scratch.
     * Resets level score and clears movement history.
     */
    void restartLevel();

    /**
     * Starts a new game from level 1.
     */
    void newGame();
}
