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
import es.upm.pproject.parkingjam.model.dto.GameState;

import java.util.*;

@Nested
@DisplayName("GameState tests")
public class GameStateTest {

    private static Board board;
    private GameState gameState;

    @BeforeAll
    static void setUpFirstOnce() {
        // Create a simple board for testing
        Set<Position> walls = new HashSet<>();
        walls.add(new Position(0, 0));
        walls.add(new Position(0, 1));

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        board = new Board(7, 7, walls, exit, vehicles);
    }

    @BeforeEach
    void setUp() {
        gameState = new GameState(board, 1, "Level 1");
    }

    @Test
    @DisplayName("GameStateTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(gameState);
    }

    @Test
    @DisplayName("GameStateTest_02: should return correct board when getBoard is called")
    void testGetBoard() {
        assertEquals(board, gameState.getBoard());
    }

    @Test
    @DisplayName("GameStateTest_03: should return correct current level when getCurrentLevel is called")
    void testGetCurrentLevel() {
        assertEquals(1, gameState.getCurrentLevel());
    }

    @Test
    @DisplayName("GameStateTest_04: should return correct current level name when getCurrentLevelName is called")
    void testGetCurrentLevelName() {
        assertEquals("Level 1", gameState.getCurrentLevelName());
    }

    @Test
    @DisplayName("GameStateTest_05: should return 0 as initial level score when getLevelScore is called")
    void testGetLevelScoreInitial() {
        assertEquals(0, gameState.getLevelScore());
    }

    @Test
    @DisplayName("GameStateTest_06: should return 0 as initial total score when getTotalScore is called")
    void testGetTotalScoreInitial() {
        assertEquals(0, gameState.getTotalScore());
    }

    @Test
    @DisplayName("GameStateTest_07: should return false as initial finished status when isFinished is called")
    void testIsFinishedInitial() {
        assertFalse(gameState.isFinished());
    }

    @Test
    @DisplayName("GameStateTest_08: should update board correctly when setBoard is called")
    void testSetBoard() {
        // Create a new board
        Set<Position> newWalls = new HashSet<>();

        Position newExit = new Position(5, 5);

        Map<Character, Vehicle> newVehicles = new HashMap<>();


        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(0, 0));
        redCarPositions.add(new Position(0, 1));

        Vehicle newRedCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', newRedCar);

        Board newBoard = new Board(5, 5, newWalls, newExit, vehicles);

        gameState.setBoard(newBoard);

        assertEquals(newBoard, gameState.getBoard());
    }

    @Test
    @DisplayName("GameStateTest_09: should throw exception when setBoard is called with null")
    void testSetBoardNull() {
        assertThrows(IllegalArgumentException.class, () -> gameState.setBoard(null));
    }

    @Test
    @DisplayName("GameStateTest_10: should update current level correctly when setCurrentLevel is called")
    void testSetCurrentLevel() {
        gameState.setCurrentLevel(3);
        assertEquals(3, gameState.getCurrentLevel());
    }

    @Test
    @DisplayName("GameStateTest_11: should throw exception when setCurrentLevel is called with value less than 1")
    void testSetCurrentLevelInvalid() {
        assertThrows(IllegalArgumentException.class, () -> gameState.setCurrentLevel(0));
        assertThrows(IllegalArgumentException.class, () -> gameState.setCurrentLevel(-1));
    }

    @Test
    @DisplayName("GameStateTest_12: should update current level name correctly when setCurrentLevelName is called")
    void testSetCurrentLevelName() {
        gameState.setCurrentLevelName("Level 2");
        assertEquals("Level 2", gameState.getCurrentLevelName());
    }

    @Test
    @DisplayName("GameStateTest_13: should throw exception when setCurrentLevelName is called with null")
    void testSetCurrentLevelNameNull() {
        assertThrows(IllegalArgumentException.class, () -> gameState.setCurrentLevelName(null));
    }

    @Test
    @DisplayName("GameStateTest_14: should throw exception when setCurrentLevelName is called with empty string")
    void testSetCurrentLevelNameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> gameState.setCurrentLevelName(""));
        assertThrows(IllegalArgumentException.class, () -> gameState.setCurrentLevelName("   "));
    }

    @Test
    @DisplayName("GameStateTest_15: should update level score correctly when setLevelScore is called")
    void testSetLevelScore() {
        gameState.setLevelScore(10);
        assertEquals(10, gameState.getLevelScore());

        gameState.setLevelScore(0);
        assertEquals(0, gameState.getLevelScore());
    }

    @Test
    @DisplayName("GameStateTest_16: should throw exception when setLevelScore is called with negative value")
    void testSetLevelScoreNegative() {
        assertThrows(IllegalArgumentException.class, () -> gameState.setLevelScore(-1));
        assertThrows(IllegalArgumentException.class, () -> gameState.setLevelScore(-100));
    }

    @Test
    @DisplayName("GameStateTest_17: should update total score correctly when setTotalScore is called")
    void testSetTotalScore() {
        gameState.setTotalScore(50);
        assertEquals(50, gameState.getTotalScore());

        gameState.setTotalScore(0);
        assertEquals(0, gameState.getTotalScore());
    }

    @Test
    @DisplayName("GameStateTest_18: should throw exception when setTotalScore is called with negative value")
    void testSetTotalScoreNegative() {
        assertThrows(IllegalArgumentException.class, () -> gameState.setTotalScore(-1));
        assertThrows(IllegalArgumentException.class, () -> gameState.setTotalScore(-100));
    }

    @Test
    @DisplayName("GameStateTest_19: should update finished status correctly when setFinished is called")
    void testSetFinished() {
        gameState.setFinished(true);
        assertTrue(gameState.isFinished());

        gameState.setFinished(false);
        assertFalse(gameState.isFinished());
    }

    @Test
    @DisplayName("GameStateTest_20: should throw exception when constructor is called with null board")
    void testConstructorNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> new GameState(null, 1, "Level 1"));
    }

    @Test
    @DisplayName("GameStateTest_21: should throw exception when constructor is called with invalid current level")
    void testConstructorInvalidLevel() {
        assertThrows(IllegalArgumentException.class, () -> new GameState(board, 0, "Level 1"));
        assertThrows(IllegalArgumentException.class, () -> new GameState(board, -1, "Level 1"));
    }

    @Test
    @DisplayName("GameStateTest_22: should throw exception when constructor is called with null level name")
    void testConstructorNullLevelName() {
        assertThrows(IllegalArgumentException.class, () -> new GameState(board, 1, null));
    }

    @Test
    @DisplayName("GameStateTest_23: should throw exception when constructor is called with empty level name")
    void testConstructorEmptyLevelName() {
        assertThrows(IllegalArgumentException.class, () -> new GameState(board, 1, ""));
        assertThrows(IllegalArgumentException.class, () -> new GameState(board, 1, "   "));
    }

}
