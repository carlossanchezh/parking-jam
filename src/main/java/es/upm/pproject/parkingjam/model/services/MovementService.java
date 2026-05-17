package es.upm.pproject.parkingjam.model.services;

import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Direction;

public interface MovementService {
    boolean move(Board board, char vehicleId, Direction direction);
}
