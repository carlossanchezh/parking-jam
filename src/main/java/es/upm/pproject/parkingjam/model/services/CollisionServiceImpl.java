package es.upm.pproject.parkingjam.model.services;

import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;

import java.util.Optional;

public class CollisionServiceImpl implements CollisionService {

    @Override
    public boolean collision(Board board, Position position, char vehicleId) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        if (!board.isInBoard(position)) {
            return !leftAtExit(board, position, vehicleId);
        }

        if (board.isInWall(position)) {
            return true;
        }

        Optional<Vehicle> vehicle = board.getVehicleAtPosition(position);
        return vehicle.isPresent() && vehicle.get().getId() != vehicleId;
    }

    private boolean leftAtExit(Board board, Position position, char vehicleId) {
        Optional<Vehicle> vehicle = board.getVehicle(vehicleId);

        if (vehicle.isEmpty() || !vehicle.get().isRedCar()) {
            return false;
        }

        Position exit = board.getExit();

        boolean exitAtTop = exit.getX() == 0;
        boolean exitAtBottom = exit.getX() == board.getRows() - 1;
        boolean exitAtLeft = exit.getY() == 0;
        boolean exitAtRight = exit.getY() == board.getColumns() - 1;

        if (exitAtTop) {
            return position.getX() < 0 && position.getY() == exit.getY();
        }
        if (exitAtBottom) {
            return position.getX() >= board.getRows() && position.getY() == exit.getY();
        }
        if (exitAtLeft) {
            return position.getY() < 0 && position.getX() == exit.getX();
        }
        if (exitAtRight) {
            return position.getY() >= board.getColumns() && position.getX() == exit.getX();
        }
        return false;
    }
}
