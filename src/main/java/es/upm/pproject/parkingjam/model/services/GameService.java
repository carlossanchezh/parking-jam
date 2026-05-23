package es.upm.pproject.parkingjam.model.services;

import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.GameState;

public interface GameService {
    GameState getGameState();

    void setGameState(GameState gameState);

    boolean move(char vehicleId, Direction direction);

    boolean isLevelCompleted();

    void finishLevel();
}
