package es.upm.pproject.parkingjam.model.services;

import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;
import es.upm.pproject.parkingjam.model.exceptions.VehicleNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class MovementServiceImpl implements MovementService {
    private final CollisionService collisionService;

    public MovementServiceImpl() {
        this(new CollisionServiceImpl());
    }

    public MovementServiceImpl(CollisionService collisionService) {
        if (collisionService == null) {
            throw new IllegalArgumentException("CollisionService cannot be null");
        }
        this.collisionService = collisionService;
    }


    @Override
    public boolean move(Board board, char vehicleId, Direction direction) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }

        Vehicle vehicle = board.getVehicle(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        if (!isMovementAllowed(vehicle, direction)) {
            return false;
        }

        List<Position> newPositions = calculateNewPositions(vehicle, direction);

        for (Position newPosition : newPositions) {
            if (collisionService.collision(board, newPosition, vehicleId)) {
                return false;
            }
        }

        vehicle.setPositions(newPositions);
        return true;
    }


    private boolean isMovementAllowed(Vehicle vehicle, Direction direction) {
        if (vehicle.isHorizontal()) {
            return direction == Direction.EAST || direction == Direction.WEST;
        }

        if (vehicle.isVertical()) {
            return direction == Direction.NORTH || direction == Direction.SOUTH;
        }
        return false;
    }

    private List<Position> calculateNewPositions(Vehicle vehicle, Direction direction) {
        List<Position> newPositions = new ArrayList<>();
        for (Position position : vehicle.getPositions()) {
            switch (direction) {
                case NORTH:
                    newPositions.add(position.addCoordinates(-1, 0));
                    break;
                case SOUTH:
                    newPositions.add(position.addCoordinates(1, 0));
                    break;
                case EAST:
                    newPositions.add(position.addCoordinates(0, 1));
                    break;
                case WEST:
                    newPositions.add(position.addCoordinates(0, -1));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid direction");
            }
        }
        return newPositions;
    }
}


/*
1. Buscar el vehículo en el Board.
2. Comprobar si su orientación permite esa dirección.
3. Calcular nuevas posiciones y usar CollisionService.
 */