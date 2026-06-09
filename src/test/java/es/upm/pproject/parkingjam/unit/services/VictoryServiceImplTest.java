package es.upm.pproject.parkingjam;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Orientation;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;
import es.upm.pproject.parkingjam.model.services.VictoryService;
import es.upm.pproject.parkingjam.model.services.VictoryServiceImpl;

@Nested
@DisplayName("VictoryService tests")
class VictoryServiceImplTest {

    private VictoryService victoryService;

    @BeforeEach
    void setUp() {
        victoryService = new VictoryServiceImpl();
    }

    @Test
    @DisplayName("VictoryTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(victoryService);
    }

    @Test
    @DisplayName("VictoryTest_02: should return false when red car is still inside board")
    void testRedCarInsideBoard() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertFalse(victoryService.isLevelCompleted(board));
    }

    @Test
    @DisplayName("VictoryTest_03: should return true when red car has left the board horizontally aligned with exit")
    void testRedCarLeftHorizontallyAligned() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(3, 5);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, -2));
        redCarPositions.add(new Position(3, -1));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertTrue(victoryService.isLevelCompleted(board));
    }

    @Test
    @DisplayName("VictoryTest_04: should return true when red car has left the board vertically aligned with exit")
    void testRedCarLeftVerticallyAligned() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(5, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(-2, 3));
        redCarPositions.add(new Position(-1, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.VERTICAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertTrue(victoryService.isLevelCompleted(board));
    }

    @Test
    @DisplayName("VictoryTest_05: should return false when red car has left the board but is not aligned with exit")
    void testRedCarLeftButNotAligned() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(0, -2));
        redCarPositions.add(new Position(0, -1));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertFalse(victoryService.isLevelCompleted(board));
    }

    @Test
    @DisplayName("VictoryTest_06: should return false when red car is partially outside but still inside board")
    void testRedCarPartiallyOutside() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, -1));  // outside
        redCarPositions.add(new Position(3, 0));   // inside

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertFalse(victoryService.isLevelCompleted(board));
    }

    @Test
    @DisplayName("VictoryTest_07: should throw exception when board is null")
    void testNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> victoryService.isLevelCompleted(null));
    }

    @Test
    @DisplayName("VictoryTest_08: should throw exception when board has no red car")
    void testBoardWithoutRedCar() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(6, 3);

        List<Position> vehiclePositions = new ArrayList<>();
        vehiclePositions.add(new Position(1, 1));
        vehiclePositions.add(new Position(1, 2));

        Vehicle regularVehicle = new Vehicle('A', vehiclePositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', regularVehicle);

        Board boardWithoutRedCar = new Board(7, 7, walls, exit, vehicles);

        assertThrows(IllegalStateException.class, () -> victoryService.isLevelCompleted(boardWithoutRedCar));
    }

    @Test
    @DisplayName("VictoryTest_09: should return false when red car is inside board with correct alignment but not at exit")
    void testRedCarInsideAlignedButNotAtExit() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 4));
        redCarPositions.add(new Position(3, 5));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertFalse(victoryService.isLevelCompleted(board));
    }

}
