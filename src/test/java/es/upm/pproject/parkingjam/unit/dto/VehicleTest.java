package es.upm.pproject.parkingjam.unit.dto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;
import es.upm.pproject.parkingjam.model.dto.Orientation;


@Nested
@DisplayName("Vehicle tests")
class VehicleTest {

    private static List<Position> horizontalPositions;
    private static List<Position> verticalPositions;
    private Vehicle horizontalVehicle;
    private Vehicle verticalVehicle;
    private Vehicle redCar;
    private Vehicle sameHorizontalVehicle;
    private Vehicle differentVehicle;

    @BeforeAll
    static void setUpFirstOnce() {

        horizontalPositions = new ArrayList<>();
        horizontalPositions.add(new Position(2, 2));
        horizontalPositions.add(new Position(2, 3));
        horizontalPositions.add(new Position(2, 4));

        verticalPositions = new ArrayList<>();
        verticalPositions.add(new Position(2, 2));
        verticalPositions.add(new Position(3, 2));
        verticalPositions.add(new Position(4, 2));
    }

    @BeforeEach
    void setUp() {

        horizontalVehicle = new Vehicle('A', horizontalPositions, false, Orientation.HORIZONTAL);

        verticalVehicle = new Vehicle('B', verticalPositions, false, Orientation.VERTICAL);

        redCar = new Vehicle('R', horizontalPositions, true, Orientation.HORIZONTAL);

        sameHorizontalVehicle = new Vehicle('A', horizontalPositions, false, Orientation.HORIZONTAL);

        differentVehicle = new Vehicle('C', verticalPositions, false, Orientation.VERTICAL);
    }

    @Test
    @DisplayName("VehicleTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(horizontalVehicle);
        assertNotNull(verticalVehicle);
        assertNotNull(redCar);
    }

    @Test
    @DisplayName("VehicleTest_02: should return correct id when getId is called")
    void testGetId() {
        assertEquals('A', horizontalVehicle.getId());
        assertEquals('B', verticalVehicle.getId());
        assertEquals('R', redCar.getId());
    }

    @Test
    @DisplayName("VehicleTest_03: should return correct size when getSize is called")
    void testGetSize() {
        assertEquals(3, horizontalVehicle.getSize());
        assertEquals(3, verticalVehicle.getSize());
    }

    @Test
    @DisplayName("VehicleTest_04: should return correct redCar status when isRedCar is called")
    void testIsRedCar() {
        assertFalse(horizontalVehicle.isRedCar());
        assertFalse(verticalVehicle.isRedCar());
        assertTrue(redCar.isRedCar());
    }

    @Test
    @DisplayName("VehicleTest_05: should return correct orientation when getOrientation is called")
    void testGetOrientation() {
        assertEquals(Orientation.HORIZONTAL, horizontalVehicle.getOrientation());
        assertEquals(Orientation.VERTICAL, verticalVehicle.getOrientation());
    }

    @Test
    @DisplayName("VehicleTest_06: should return correct isHorizontal value")
    void testIsHorizontal() {
        assertTrue(horizontalVehicle.isHorizontal());
        assertFalse(verticalVehicle.isHorizontal());
    }

    @Test
    @DisplayName("VehicleTest_07: should return correct isVertical value")
    void testIsVertical() {
        assertFalse(horizontalVehicle.isVertical());
        assertTrue(verticalVehicle.isVertical());
    }

    @Test
    @DisplayName("VehicleTest_08: should update positions correctly when setPositions is called")
    void testSetPositions() {
        List<Position> newPositions = List.of(new Position(5, 5), new Position(5, 6));

        horizontalVehicle.setPositions(newPositions);

        assertEquals(2, horizontalVehicle.getSize());
        assertEquals(new Position(5, 5), horizontalVehicle.getPositions().get(0));
        assertEquals(new Position(5, 6), horizontalVehicle.getPositions().get(1));
    }

    @Test
    @DisplayName("VehicleTest_09: should throw exception when setPositions is called with null")
    void testSetPositionsNull() {
        assertThrows(IllegalArgumentException.class, () -> horizontalVehicle.setPositions(null));
    }

    @Test
    @DisplayName("VehicleTest_10: should throw exception when setPositions is called with empty list")
    void testSetPositionsEmpty() {
        List<Position> newPositions = List.of();
        assertThrows(IllegalArgumentException.class, () -> horizontalVehicle.setPositions(newPositions));
    }

    @Test
    @DisplayName("VehicleTest_11: should throw exception when constructor is called with null positions")
    void testConstructorNullPositions() {
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('X', null, false, Orientation.HORIZONTAL));
    }

    @Test
    @DisplayName("VehicleTest_12: should throw exception when constructor is called with empty positions")
    void testConstructorEmptyPositions() {
        List<Position> positions = List.of();
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('X', positions, false, Orientation.HORIZONTAL));
    }

    @Test
    @DisplayName("VehicleTest_13: should throw exception when constructor is called with null orientation")
    void testConstructorNullOrientation() {
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('X', horizontalPositions, false, null));
    }

    @Test
    @DisplayName("VehicleTest_14: should return true when comparing same object")
    void testEqualsSameObject() {
        assertEquals(horizontalVehicle, horizontalVehicle);
        assertEquals(verticalVehicle, verticalVehicle);
    }

    @Test
    @DisplayName("VehicleTest_15: should return true when comparing vehicles with same attributes")
    void testEqualsSameAttributes() {
        assertEquals(horizontalVehicle, sameHorizontalVehicle);
        assertEquals(sameHorizontalVehicle, horizontalVehicle);
    }

    @Test
    @DisplayName("VehicleTest_16: should return false when comparing vehicles with different attributes")
    void testEqualsDifferentAttributes() {
        assertNotEquals(horizontalVehicle, verticalVehicle);
        assertNotEquals(horizontalVehicle, differentVehicle);
    }

    @Test
    @DisplayName("VehicleTest_17: should return false when comparing with null")
    void testEqualsNull() {
        assertNotEquals(null, horizontalVehicle);
    }

    @Test
    @DisplayName("VehicleTest_18: should return false when comparing with different type")
    void testEqualsDifferentType() {
        assertNotEquals("coche", horizontalVehicle);
    }

    @Test
    @DisplayName("VehicleTest_21: should return same hash code for vehicles with same attributes")
    void testHashCodeSame() {
        assertEquals(horizontalVehicle.hashCode(), sameHorizontalVehicle.hashCode());
    }

    @Test
    @DisplayName("VehicleTest_22: should return different hash code for vehicles with different attributes")
    void testHashCodeDifferent() {
        assertNotEquals(horizontalVehicle.hashCode(), verticalVehicle.hashCode());
    }

    @Test
    @DisplayName("VehicleTest_23: should create immutable copy of positions in constructor")
    void testConstructorCreatesImmutableCopy() {
        List<Position> positions = new ArrayList<>();
        positions.add(new Position(1, 1));
        positions.add(new Position(1, 2));

        Vehicle vehicle = new Vehicle('A', positions, false, Orientation.HORIZONTAL);

        // Modify original list after creating vehicle
        positions.add(new Position(1, 3));

        // Vehicles positions should remain unchanged
        assertEquals(2, vehicle.getSize());
        assertEquals(new Position(1, 1), vehicle.getPositions().get(0));
        assertEquals(new Position(1, 2), vehicle.getPositions().get(1));
    }

    @Test
    @DisplayName("VehicleTest_24: should create immutable copy when setPositions is called")
    void testSetPositionsCreatesImmutableCopy() {
        List<Position> positions = new ArrayList<>();
        positions.add(new Position(1, 1));
        positions.add(new Position(1, 2));

        Vehicle vehicle = new Vehicle('A', positions, false, Orientation.HORIZONTAL);

        List<Position> newPositions = new ArrayList<>();
        newPositions.add(new Position(2, 2));
        newPositions.add(new Position(2, 3));

        vehicle.setPositions(newPositions);

        // Modify the list after setting
        newPositions.add(new Position(2, 4));

        // Vehicles positions should remain unchanged
        assertEquals(2, vehicle.getSize());
        assertEquals(new Position(2, 2), vehicle.getPositions().get(0));
        assertEquals(new Position(2, 3), vehicle.getPositions().get(1));
    }

    @Test
    @DisplayName("VehicleTest_25: should return immutable copy when getPositions is called")
    void testGetPositionsReturnsImmutableCopy() {
        List<Position> positions = new ArrayList<>();
        positions.add(new Position(1, 1));
        positions.add(new Position(1, 2));

        Vehicle vehicle = new Vehicle('A', positions, false, Orientation.HORIZONTAL);

        List<Position> getPositions = vehicle.getPositions();

        Position position = new Position(99, 99);

        assertThrows(UnsupportedOperationException.class, () -> getPositions.add(position));
    }
}
