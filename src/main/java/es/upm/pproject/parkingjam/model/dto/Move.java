package es.upm.pproject.parkingjam.model.dto;

public class Move {
    private final char vehicleId;
    private final Direction direction;

    public Move(final char vehicleId, final Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("direction cannot be null");
        }
        this.vehicleId = vehicleId;
        this.direction = direction;
    }

    public char getVehicleId() {
        return vehicleId;
    }

    public Direction getDirection() {
        return direction;
    }
}
