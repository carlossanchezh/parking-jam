package es.upm.pproject.parkingjam.model.services;

import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VictoryServiceImpl implements VictoryService {
    private static final Logger logger = LoggerFactory.getLogger(VictoryServiceImpl.class);

    @Override
    public boolean isLevelCompleted(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board can't be null");
        }

        Vehicle redCar = board.getVehicles()
                .values()
                .stream()
                .filter(Vehicle::isRedCar)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Board has no red car"));

        boolean completed = redCar.getPositions()
                .stream()
                .noneMatch(board::isInBoard)
                && isAlignedWithExit(redCar, board);

        if (completed) {
            logger.info("Level completed! Red car has escaped through the exit at {}", board.getExit());
        }

        return completed;
    }

    private boolean isAlignedWithExit(Vehicle redCar, Board board) {
        Position exit = board.getExit();

        if (redCar.isHorizontal()) {
            return redCar.getPositions()
                    .stream()
                    .allMatch(position -> position.getX() == exit.getX());
        }

        if (redCar.isVertical()) {
            return redCar.getPositions()
                    .stream()
                    .allMatch(position -> position.getY() == exit.getY());
        }
        return false;
    }
}
