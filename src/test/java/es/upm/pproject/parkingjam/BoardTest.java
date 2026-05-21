package es.upm.pproject.parkingjam;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.Vehicle;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Orientation;

import java.util.*;

@Nested
@DisplayName("Board tests")
public class BoardTest {

    private static Set<Position> walls;
    private static Position exit;
    private static Map<Character, Vehicle> vehicles;
    private static Vehicle redCar;
    private static Vehicle vehicleA;
    private static Vehicle vehicleB;

    private Board board;

    @BeforeAll
    static void setUpFirstOnce() {
        // Walls
        walls = new HashSet<>();
        walls.add(new Position(0, 0));
        walls.add(new Position(0, 1));
        walls.add(new Position(5, 5));

        // Exit
        exit = new Position(6, 3);

        // Vehicles
        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(1, 1));
        vehicleAPositions.add(new Position(1, 2));
        vehicleAPositions.add(new Position(1, 3));

        vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        List<Position> vehicleBPositions = new ArrayList<>();
        vehicleBPositions.add(new Position(2, 4));
        vehicleBPositions.add(new Position(3, 4));
        vehicleBPositions.add(new Position(4, 4));

        vehicleB = new Vehicle('B', vehicleBPositions, false, Orientation.VERTICAL);

        vehicles = new HashMap<>();
        vehicles.put('R', redCar);
        vehicles.put('A', vehicleA);
        vehicles.put('B', vehicleB);
    }

    @BeforeEach
    void setUp() {
        board = new Board(7, 7, walls, exit, vehicles);
    }

    @Test
    @DisplayName("BoardTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(board);
    }

    @Test
    @DisplayName("BoardTest_02: should return correct rows when getRows is called")
    void testGetRows() {
        assertEquals(7, board.getRows());
    }

    @Test
    @DisplayName("BoardTest_03: should return correct columns when getColumns is called")
    void testGetColumns() {
        assertEquals(7, board.getColumns());
    }

    @Test
    @DisplayName("BoardTest_04: should return a copy of walls when getWalls is called")
    void testGetWalls() {

        Set<Position> retrievedWalls = board.getWalls();

        assertEquals(3, retrievedWalls.size());
        assertTrue(retrievedWalls.contains(new Position(0, 0)));
        assertTrue(retrievedWalls.contains(new Position(0, 1)));
        assertTrue(retrievedWalls.contains(new Position(5, 5)));
    }

    @Test
    @DisplayName("BoardTest_05: should return correct exit when getExit is called")
    void testGetExit() {
        assertEquals(exit, board.getExit());
        assertEquals(6, board.getExit().getX());
        assertEquals(3, board.getExit().getY());
    }

    @Test
    @DisplayName("BoardTest_06: should return a copy of vehicles when getVehicles is called")
    void testGetVehicles() {
        Map<Character, Vehicle> retrievedVehicles = board.getVehicles();
        assertEquals(3, retrievedVehicles.size());
        assertTrue(retrievedVehicles.containsKey('R'));
        assertTrue(retrievedVehicles.containsKey('A'));
        assertTrue(retrievedVehicles.containsKey('B'));
    }

    @Test
    @DisplayName("BoardTest_07: should return vehicle when getVehicle is called with existing id")
    void testGetVehicleExisting() {
        Optional<Vehicle> result = board.getVehicle('R');
        assertTrue(result.isPresent());
        assertEquals(redCar, result.get());

        result = board.getVehicle('A');
        assertTrue(result.isPresent());
        assertEquals(vehicleA, result.get());

        result = board.getVehicle('B');
        assertTrue(result.isPresent());
        assertEquals(vehicleB, result.get());
    }

    @Test
    @DisplayName("BoardTest_08: should return empty optional when getVehicle is called with non-existing id")
    void testGetVehicleNonExisting() {
        Optional<Vehicle> result = board.getVehicle('Z');
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("BoardTest_09: should return true when position is inside board")
    void testIsInBoardTrue() {
        assertTrue(board.isInBoard(new Position(3, 3)));
        assertTrue(board.isInBoard(new Position(0, 0)));
        assertTrue(board.isInBoard(new Position(6, 6)));
    }

    @Test
    @DisplayName("BoardTest_10: should return false when position is outside board")
    void testIsInBoardFalse() {
        assertFalse(board.isInBoard(new Position(-1, 3)));
        assertFalse(board.isInBoard(new Position(3, -1)));
        assertFalse(board.isInBoard(new Position(7, 3)));
        assertFalse(board.isInBoard(new Position(3, 7)));
    }

    @Test
    @DisplayName("BoardTest_11: should return true when position is a wall")
    void testIsInWallTrue() {
        assertTrue(board.isInWall(new Position(0, 0)));
        assertTrue(board.isInWall(new Position(0, 1)));
        assertTrue(board.isInWall(new Position(5, 5)));
    }

    @Test
    @DisplayName("BoardTest_12: should return false when position is not a wall")
    void testIsInWallFalse() {
        assertFalse(board.isInWall(new Position(1, 1)));
        assertFalse(board.isInWall(new Position(3, 3)));
        assertFalse(board.isInWall(new Position(6, 3)));
    }

    @Test
    @DisplayName("BoardTest_13: should return true when position is the exit")
    void testIsInExitTrue() {
        assertTrue(board.isInExit(new Position(6, 3)));
    }

    @Test
    @DisplayName("BoardTest_14: should return false when position is not the exit")
    void testIsInExitFalse() {
        assertFalse(board.isInExit(new Position(3, 3)));
        assertFalse(board.isInExit(new Position(0, 0)));
    }

    @Test
    @DisplayName("BoardTest_15: should return true when position is occupied by a vehicle")
    void testIsOccupiedTrue() {
        // Red car positions
        assertTrue(board.isOccupied(new Position(3, 2)));
        assertTrue(board.isOccupied(new Position(3, 3)));

        // Vehicle A positions
        assertTrue(board.isOccupied(new Position(1, 1)));
        assertTrue(board.isOccupied(new Position(1, 2)));
        assertTrue(board.isOccupied(new Position(1, 3)));

        // Vehicle B positions
        assertTrue(board.isOccupied(new Position(2, 4)));
        assertTrue(board.isOccupied(new Position(3, 4)));
        assertTrue(board.isOccupied(new Position(4, 4)));
    }

    @Test
    @DisplayName("BoardTest_16: should return false when position is not occupied")
    void testIsOccupiedFalse() {
        assertFalse(board.isOccupied(new Position(0, 2)));
        assertFalse(board.isOccupied(new Position(5, 0)));
        assertFalse(board.isOccupied(new Position(6, 6)));
    }

    @Test
    @DisplayName("BoardTest_17: should return vehicle when getVehicleAtPosition is called with occupied position")
    void testGetVehicleAtPositionOccupied() {
        Optional<Vehicle> result = board.getVehicleAtPosition(new Position(3, 2));
        assertTrue(result.isPresent());
        assertEquals(redCar, result.get());

        result = board.getVehicleAtPosition(new Position(1, 2));
        assertTrue(result.isPresent());
        assertEquals(vehicleA, result.get());

        result = board.getVehicleAtPosition(new Position(3, 4));
        assertTrue(result.isPresent());
        assertEquals(vehicleB, result.get());
    }

    @Test
    @DisplayName("BoardTest_18: should return empty optional when getVehicleAtPosition is called with unoccupied position")
    void testGetVehicleAtPositionUnoccupied() {
        Optional<Vehicle> result = board.getVehicleAtPosition(new Position(0, 2));
        assertFalse(result.isPresent());

        result = board.getVehicleAtPosition(new Position(6, 6));
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("BoardTest_20: should throw exception when constructor is called with invalid columns")
    void testConstructorInvalidColumns() {
        Set<Position> validWalls = Set.of();
        Position validExit = new Position(0, 0);
        Map<Character, Vehicle> validVehicles = Map.of('R', redCar);

        assertThrows(IllegalArgumentException.class, () -> new Board(5, 0, validWalls, validExit, validVehicles));
        assertThrows(IllegalArgumentException.class, () -> new Board(5, -1, validWalls, validExit, validVehicles));
    }

    @Test
    @DisplayName("BoardTest_21: should throw exception when constructor is called with null walls")
    void testConstructorNullWalls() {
        assertThrows(IllegalArgumentException.class, () -> new Board(7, 7, null, exit, vehicles));
    }

    @Test
    @DisplayName("BoardTest_22: should throw exception when constructor is called with null exit")
    void testConstructorNullExit() {
        assertThrows(IllegalArgumentException.class, () -> new Board(7, 7, walls, null, vehicles));
    }

    @Test
    @DisplayName("BoardTest_23: should throw exception when constructor is called with null vehicles")
    void testConstructorNullVehicles() {
        assertThrows(IllegalArgumentException.class, () -> new Board(7, 7, walls, exit, null));
    }

    @Test
    @DisplayName("BoardTest_24: should throw exception when constructor is called with empty vehicles")
    void testConstructorEmptyVehicles() {
        assertThrows(IllegalArgumentException.class, () -> new Board(7, 7, walls, exit, new HashMap<>()));
    }

}
