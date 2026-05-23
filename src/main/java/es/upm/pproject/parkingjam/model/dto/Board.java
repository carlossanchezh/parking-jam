package es.upm.pproject.parkingjam.model.dto;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Board {
    private final int rows;
    private final int columns;
    private final Set<Position> walls;
    private final Position exit;
    private final Map<Character, Vehicle> vehicles;

    public Board(int rows, int columns, Set<Position> walls, Position exit,  Map<Character, Vehicle> vehicles) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Rows and columns must be greater than 0");
        }
        if (walls ==  null) {
            throw new IllegalArgumentException("Walls cannot be null");
        }
        if (exit == null) {
            throw new IllegalArgumentException("Exit cannot be null");
        }
        if (vehicles == null || vehicles.isEmpty()) {
            throw new IllegalArgumentException("Vehicles cannot be null or empty");
        }

        this.rows = rows;
        this.columns = columns;
        this.walls = Set.copyOf(walls);
        this.exit = exit;
        this.vehicles = Map.copyOf(vehicles);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Set<Position> getWalls() {
        return Set.copyOf(walls);
    }

    public Position getExit() {
        return exit;
    }

    public Map<Character, Vehicle> getVehicles() {
        return Map.copyOf(vehicles);
    }

    public Optional<Vehicle> getVehicle(char id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    public boolean isInBoard(Position position) {
        return position.getX() >= 0
                && position.getX() < rows
                && position.getY() >= 0
                && position.getY() < columns;
    }

    public boolean isInWall(Position position) {
        return walls.contains(position);
    }

    public boolean isInExit(Position position) {
        return position.equals(exit);
    }

    public boolean isOccupied(Position position) {
        for (Vehicle vehicle : vehicles.values()) {
            if (vehicle.getPositions().contains(position)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Vehicle> getVehicleAtPosition(Position position) {
        for (Vehicle vehicle : vehicles.values()) {
            if (vehicle.getPositions().contains(position)) {
                return Optional.of(vehicle);
            }
        }
        return Optional.empty();
    }
}
