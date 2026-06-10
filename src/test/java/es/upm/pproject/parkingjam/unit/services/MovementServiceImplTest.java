package es.upm.pproject.parkingjam;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.Orientation;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;
import es.upm.pproject.parkingjam.model.exceptions.VehicleNotFoundException;
import es.upm.pproject.parkingjam.model.services.CollisionService;
import es.upm.pproject.parkingjam.model.services.CollisionServiceImpl;
import es.upm.pproject.parkingjam.model.services.MovementService;
import es.upm.pproject.parkingjam.model.services.MovementServiceImpl;

@Nested
@DisplayName("MovementService tests")
class MovementServiceImplTest {

    private MovementService movementService;

    @BeforeEach
    void setUp() {
        CollisionService collisionService = new CollisionServiceImpl();
        movementService = new MovementServiceImpl(collisionService);
    }

    @Test
    @DisplayName("MovementTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(movementService);
    }

    @Test
    @DisplayName("MovementTest_02: should throw exception when board is null")
    void testNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> movementService.move(null, 'A', Direction.EAST));
    }

    @Test
    @DisplayName("MovementTest_03: should throw exception when direction is null")
    void testNullDirection() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(1, 1));
        vehicleAPositions.add(new Position(1, 2));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertThrows(IllegalArgumentException.class, () -> movementService.move(board, 'A', null));
    }

    @Test
    @DisplayName("MovementTest_04: should throw exception when vehicle does not exist")
    void testVehicleNotFound() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(1, 1));
        vehicleAPositions.add(new Position(1, 2));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertThrows(VehicleNotFoundException.class, () -> movementService.move(board, 'Z', Direction.EAST));
    }

    @Test
    @DisplayName("MovementTest_05: should move horizontal vehicle EAST successfully")
    void testMoveHorizontalVehicleEast() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(3, 2));
        vehicleAPositions.add(new Position(3, 3));
        vehicleAPositions.add(new Position(3, 4));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.EAST);

        assertTrue(result);
        Vehicle movedVehicle = board.getVehicle('A').orElseThrow();
        assertEquals(new Position(3, 3), movedVehicle.getPositions().get(0));
        assertEquals(new Position(3, 4), movedVehicle.getPositions().get(1));
        assertEquals(new Position(3, 5), movedVehicle.getPositions().get(2));
    }

    @Test
    @DisplayName("MovementTest_06: should move horizontal vehicle WEST successfully")
    void testMoveHorizontalVehicleWest() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(3, 2));
        vehicleAPositions.add(new Position(3, 3));
        vehicleAPositions.add(new Position(3, 4));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.WEST);

        assertTrue(result);
        Vehicle movedVehicle = board.getVehicle('A').orElseThrow();
        assertEquals(new Position(3, 1), movedVehicle.getPositions().get(0));
        assertEquals(new Position(3, 2), movedVehicle.getPositions().get(1));
        assertEquals(new Position(3, 3), movedVehicle.getPositions().get(2));
    }

    @Test
    @DisplayName("MovementTest_07: should move vertical vehicle SOUTH successfully")
    void testMoveVerticalVehicleSouth() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(2, 3));
        vehicleAPositions.add(new Position(3, 3));
        vehicleAPositions.add(new Position(4, 3));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.VERTICAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.SOUTH);

        assertTrue(result);
        Vehicle movedVehicle = board.getVehicle('A').orElseThrow();
        assertEquals(new Position(3, 3), movedVehicle.getPositions().get(0));
        assertEquals(new Position(4, 3), movedVehicle.getPositions().get(1));
        assertEquals(new Position(5, 3), movedVehicle.getPositions().get(2));
    }

    @Test
    @DisplayName("MovementTest_08: should move vertical vehicle NORTH successfully")
    void testMoveVerticalVehicleNorth() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(2, 3));
        vehicleAPositions.add(new Position(3, 3));
        vehicleAPositions.add(new Position(4, 3));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.VERTICAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.NORTH);

        assertTrue(result);
        Vehicle movedVehicle = board.getVehicle('A').orElseThrow();
        assertEquals(new Position(1, 3), movedVehicle.getPositions().get(0));
        assertEquals(new Position(2, 3), movedVehicle.getPositions().get(1));
        assertEquals(new Position(3, 3), movedVehicle.getPositions().get(2));
    }

    @Test
    @DisplayName("MovementTest_09: should return false when horizontal vehicle tries to move NORTH")
    void testHorizontalVehicleCannotMoveNorth() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(3, 2));
        vehicleAPositions.add(new Position(3, 3));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.NORTH);

        assertFalse(result);

        Vehicle unchangedVehicle = board.getVehicle('A').orElseThrow();
        assertEquals(new Position(3, 2), unchangedVehicle.getPositions().get(0));
        assertEquals(new Position(3, 3), unchangedVehicle.getPositions().get(1));
    }

    @Test
    @DisplayName("MovementTest_10: should return false when horizontal vehicle tries to move SOUTH")
    void testHorizontalVehicleCannotMoveSouth() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(3, 2));
        vehicleAPositions.add(new Position(3, 3));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.SOUTH);

        assertFalse(result);
    }

    @Test
    @DisplayName("MovementTest_11: should return false when vertical vehicle tries to move EAST")
    void testVerticalVehicleCannotMoveEast() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(2, 3));
        vehicleAPositions.add(new Position(3, 3));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.VERTICAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.EAST);

        assertFalse(result);
    }

    @Test
    @DisplayName("MovementTest_12: should return false when vertical vehicle tries to move WEST")
    void testVerticalVehicleCannotMoveWest() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(2, 3));
        vehicleAPositions.add(new Position(3, 3));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.VERTICAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.WEST);

        assertFalse(result);
    }

    @Test
    @DisplayName("MovementTest_13: should return false when movement causes collision with wall")
    void testMoveBlockedByWall() {

        Set<Position> walls = new HashSet<>();
        walls.add(new Position(3, 5));  // Wall blocking EAST movement

        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(3, 2));
        vehicleAPositions.add(new Position(3, 3));
        vehicleAPositions.add(new Position(3, 4));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.EAST);

        assertFalse(result);
        Vehicle unchangedVehicle = board.getVehicle('A').orElseThrow();
        assertEquals(new Position(3, 2), unchangedVehicle.getPositions().get(0));
        assertEquals(new Position(3, 3), unchangedVehicle.getPositions().get(1));
        assertEquals(new Position(3, 4), unchangedVehicle.getPositions().get(2));
    }

    @Test
    @DisplayName("MovementTest_14: should return false when movement causes collision with another vehicle")
    void testMoveBlockedByOtherVehicle() {
        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(3, 2));
        vehicleAPositions.add(new Position(3, 3));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        List<Position> vehicleBPositions = new ArrayList<>();
        vehicleBPositions.add(new Position(3, 4));
        vehicleBPositions.add(new Position(3, 5));

        Vehicle vehicleB = new Vehicle('B', vehicleBPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);
        vehicles.put('B', vehicleB);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'A', Direction.EAST);

        assertFalse(result);
    }

    @Test
    @DisplayName("MovementTest_15: should allow red car to leave the board")
    void testRedCarCanLeaveBoard() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(3, 6);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 4));
        redCarPositions.add(new Position(3, 5));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        boolean result = movementService.move(board, 'R', Direction.EAST);

        assertTrue(result);
        Vehicle movedRedCar = board.getVehicle('R').orElseThrow();
        assertEquals(new Position(3, 5), movedRedCar.getPositions().get(0));
        assertEquals(new Position(3, 6), movedRedCar.getPositions().get(1));
    }

    @Test
    @DisplayName("MovementTest_16: should create MovementService with default CollisionService")
    void testDefaultConstructor() {
        MovementService defaultService = new MovementServiceImpl();
        assertNotNull(defaultService);
    }
}
