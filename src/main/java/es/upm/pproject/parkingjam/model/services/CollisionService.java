package es.upm.pproject.parkingjam.model.services;

import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;

public interface CollisionService {
    boolean collision(Board board, Position position, char vehicleId);
}
