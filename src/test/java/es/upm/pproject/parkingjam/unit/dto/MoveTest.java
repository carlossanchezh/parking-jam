package es.upm.pproject.parkingjam.unit.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import es.upm.pproject.parkingjam.model.dto.Move;
import es.upm.pproject.parkingjam.model.dto.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Nested
@DisplayName("Move tests")
public class MoveTest {

    private Move move;

    @Test
    @DisplayName("MoveTest_01: should create a non-null object when constructor is called with valid parameters")
    void testConstructorCreatesObject() {
        move = new Move('A', Direction.NORTH);
        assertNotNull(move);
    }

    @Test
    @DisplayName("MoveTest_02: should return correct vehicleId when getVehicleId is called")
    void testGetVehicleId() {
        move = new Move('R', Direction.SOUTH);
        assertEquals('R', move.getVehicleId());
    }

    @Test
    @DisplayName("MoveTest_03: should return correct direction when getDirection is called")
    void testGetVehicleDirection() {
        move = new Move('B', Direction.EAST);
        assertEquals(Direction.EAST, move.getDirection());
    }

    @Test
    @DisplayName("MoveTest_04: should work with all four directions")
    void testAllVehicleDirections() {
        Move north = new Move('A', Direction.NORTH);
        Move south = new Move('A', Direction.SOUTH);
        Move east = new Move('A', Direction.EAST);
        Move west = new Move('A', Direction.WEST);

        assertEquals(Direction.NORTH, north.getDirection());
        assertEquals(Direction.SOUTH, south.getDirection());
        assertEquals(Direction.EAST, east.getDirection());
        assertEquals(Direction.WEST, west.getDirection());
    }

    @Test
    @DisplayName("MoveTest_05: should work with different vehicle identifiers")
    void testDifferentVehicleIds() {
        Move move1 = new Move('A', Direction.NORTH);
        Move move2 = new Move('Z', Direction.NORTH);
        Move move3 = new Move('9', Direction.NORTH);
        Move move4 = new Move('*', Direction.NORTH);
        Move move5 = new Move(' ', Direction.NORTH);

        assertEquals('A', move1.getVehicleId());
        assertEquals('Z', move2.getVehicleId());
        assertEquals('9', move3.getVehicleId());
        assertEquals('*', move4.getVehicleId());
        assertEquals(' ', move5.getVehicleId());
    }

    @Test
    @DisplayName("MoveTest_06: should throw exception when direction is null")
    void testNullDirection() {
        assertThrows(IllegalArgumentException.class, () -> new Move('A', null));
    }


}
