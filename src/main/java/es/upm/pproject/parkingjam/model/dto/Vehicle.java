package es.upm.pproject.parkingjam.model.dto;

import java.util.List;
import java.util.Objects;

public class Vehicle {
    private final char id;
    private List<Position> positions;
    private final boolean redCar;
    private final Orientation orientation;

    public Vehicle(char id, List<Position> positions, boolean redCar, Orientation orientation) {
        if (positions == null || positions.isEmpty()) {
            throw new IllegalArgumentException("Positions cannot be null or empty");
        }
        if (orientation == null) {
            throw new IllegalArgumentException("Orientation cannot be null");
        }
        this.id = id;
        this.positions = List.copyOf(positions);
        this.redCar = redCar;
        this.orientation = orientation;
    }

    public char getId() {
        return id;
    }

    public int getSize() {
        return positions.size();
    }

    public List<Position> getPositions() {
        return List.copyOf(positions);
    }

    public void setPositions(List<Position> positions) {
        if (positions == null || positions.isEmpty()) {
            throw new IllegalArgumentException("Positions cannot be null or empty");
        }
        this.positions = List.copyOf(positions);
    }

    public boolean isRedCar() {
        return redCar;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public boolean isVertical() {
        return orientation.equals(Orientation.VERTICAL);
    }

    public boolean isHorizontal() {
        return orientation.equals(Orientation.HORIZONTAL);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", positions=" + positions +
                ", redCar=" + redCar +
                ", orientation=" + orientation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vehicle)) return false;
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id && redCar == vehicle.redCar && Objects.equals(positions, vehicle.positions) && orientation == vehicle.orientation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, positions, redCar, orientation);
    }
}
