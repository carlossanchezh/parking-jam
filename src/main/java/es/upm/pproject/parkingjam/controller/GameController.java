package es.upm.pproject.parkingjam.controller;

import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.GameState;

// Controller interface that mediates between the view and the model.
// Handles all game actions: movement, level loading, save/load.
public interface GameController {

    // Returns the current game state.
    GameState getGameState();

    // Starts a new game from level 1.
    void newGame();

    // Restarts the current level from scratch.
    void restartLevel();

    // Attempts to move a vehicle in the given direction.
    // After a successful move, checks if the level is completed.
    void move(char vehicleId, Direction direction);

    // Undoes last movement
    void undoMove();
}
