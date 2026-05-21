package es.upm.pproject.parkingjam;

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
public class VehicleTest {

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
    @DisplayName("VehicleTest_08: should return immutable copy of positions when getPositions is called")
    void testGetPositionsReturnsImmutableCopy() {
        List<Position> positions = horizontalVehicle.getPositions();
        assertEquals(3, positions.size());
    }

    @Test
    @DisplayName("VehicleTest_09: should update positions correctly when setPositions is called")
    void testSetPositions() {
        List<Position> newPositions = List.of(new Position(5, 5), new Position(5, 6));

        horizontalVehicle.setPositions(newPositions);

        assertEquals(2, horizontalVehicle.getSize());
        assertEquals(new Position(5, 5), horizontalVehicle.getPositions().get(0));
        assertEquals(new Position(5, 6), horizontalVehicle.getPositions().get(1));
    }

    @Test
    @DisplayName("VehicleTest_10: should throw exception when setPositions is called with null")
    void testSetPositionsNull() {
        assertThrows(IllegalArgumentException.class, () -> horizontalVehicle.setPositions(null));
    }

    @Test
    @DisplayName("VehicleTest_11: should throw exception when setPositions is called with empty list")
    void testSetPositionsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> horizontalVehicle.setPositions(List.of()));
    }

    @Test
    @DisplayName("VehicleTest_12: should throw exception when constructor is called with null positions")
    void testConstructorNullPositions() {
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('X', null, false, Orientation.HORIZONTAL));
    }

    @Test
    @DisplayName("VehicleTest_13: should throw exception when constructor is called with empty positions")
    void testConstructorEmptyPositions() {
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('X', List.of(), false, Orientation.HORIZONTAL));
    }

    @Test
    @DisplayName("VehicleTest_14: should throw exception when constructor is called with null orientation")
    void testConstructorNullOrientation() {
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('X', horizontalPositions, false, null));
    }

    @Test
    @DisplayName("VehicleTest_15: should return true when comparing same object")
    void testEqualsSameObject() {
        assertEquals(horizontalVehicle, horizontalVehicle);
        assertEquals(verticalVehicle, verticalVehicle);
    }

    @Test
    @DisplayName("VehicleTest_16: should return true when comparing vehicles with same attributes")
    void testEqualsSameAttributes() {
        assertEquals(horizontalVehicle, sameHorizontalVehicle);
        assertEquals(sameHorizontalVehicle, horizontalVehicle);
    }

    @Test
    @DisplayName("VehicleTest_17: should return false when comparing vehicles with different attributes")
    void testEqualsDifferentAttributes() {
        assertNotEquals(horizontalVehicle, verticalVehicle);
        assertNotEquals(horizontalVehicle, differentVehicle);
    }

    @Test
    @DisplayName("VehicleTest_18: should return false when comparing with null")
    void testEqualsNull() {
        assertNotEquals(null, horizontalVehicle);
    }

    @Test
    @DisplayName("VehicleTest_19: should return false when comparing with different type")
    void testEqualsDifferentType() {
        assertNotEquals("coche", horizontalVehicle);
    }

    @Test
    @DisplayName("VehicleTest_20: should return same hash code for vehicles with same attributes")
    void testHashCodeSame() {
        assertEquals(horizontalVehicle.hashCode(), sameHorizontalVehicle.hashCode());
    }

    @Test
    @DisplayName("VehicleTest_21: should return different hash code for vehicles with different attributes")
    void testHashCodeDifferent() {
        assertNotEquals(horizontalVehicle.hashCode(), verticalVehicle.hashCode());
    }
}
