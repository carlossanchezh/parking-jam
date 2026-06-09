package es.upm.pproject.parkingjam.model.dto;

public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public Direction getOppositeDirection() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
            default:
                throw new IllegalArgumentException("Invalid direction: " + this);
        }
    }
}
