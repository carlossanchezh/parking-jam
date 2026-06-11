package es.upm.pproject.parkingjam.unit.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.*;

@Nested
@DisplayName("SavedGame tests")
public class SavedGameTest {

    private Board board;
    private GameState gameState;
    private SavedGame savedGame;

    @BeforeEach
    void setUp() {

        // Create board
        Set<Position> walls = new HashSet<>();
        walls.add(new Position(0, 0));
        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));
        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        board = new Board(7, 7, walls, exit, vehicles);

        // Create GameState
        gameState = new GameState(board, 1, "Level 1");
        gameState.setLevelScore(15);
        gameState.setTotalScore(100);
        gameState.setFinished(false);
        gameState.addMovement(new Move('R', Direction.EAST));
        gameState.addMovement(new Move('R', Direction.EAST));

        // Create SavedGame from GameState
        savedGame = new SavedGame(gameState);
    }

    @Test
    @DisplayName("SavedGameTest_01: should create a non-null object when GameState constructor is called")
    void testDefaultConstructor() {
        SavedGame savedGame = new SavedGame();
        assertNotNull(savedGame);
        assertTrue(savedGame.getMovementHistory().isEmpty());
    }

    @Test
    @DisplayName("SavedGameTest_02: should create a non-null object when GameState constructor is called")
    void testGameStateConstructor() {
        assertNotNull(savedGame);
    }

    @Test
    @DisplayName("SavedGameTest_03: should correctly get currentLevel from GameState")
    void testGetCurrentLevel() {
        assertEquals(1, savedGame.getCurrentLevel());
    }

    @Test
    @DisplayName("SavedGameTest_04: should correctly get currentLevelName from GameState")
    void testGetCurrentLevelName() {
        assertEquals("Level 1", savedGame.getCurrentLevelName());
    }

    @Test
    @DisplayName("SavedGameTest_05: should correctly get board from GameState")
    void testGetBoard() {
        assertEquals(board, savedGame.getBoard());
    }

    @Test
    @DisplayName("SavedGameTest_06: should correctly get levelScore from GameState")
    void testGetLevelScore() {
        assertEquals(15, savedGame.getLevelScore());
    }

    @Test
    @DisplayName("SavedGameTest_07: should correctly get totalScore from GameState")
    void testGetTotalScore() {
        assertEquals(100, savedGame.getTotalScore());
    }

    @Test
    @DisplayName("SavedGameTest_08: should correctly get finished status from GameState")
    void testIsFinished() {
        assertFalse(savedGame.isFinished());
    }

    @Test
    @DisplayName("SavedGameTest_09: should correctly get movement history from GameState")
    void testGetMovementHistory() {
        List<Move> history = savedGame.getMovementHistory();

        assertEquals(2, history.size());
        assertEquals('R', history.get(0).getVehicleId());
        assertEquals(Direction.EAST, history.get(0).getDirection());
    }

    @Test
    @DisplayName("SavedGameTest_10: should set currentLevel correctly")
    void testSetCurrentLevel() {
        savedGame.setCurrentLevel(3);
        assertEquals(3, savedGame.getCurrentLevel());
    }

    @Test
    @DisplayName("SavedGameTest_11: should set currentLevelName correctly")
    void testSetCurrentLevelName() {
        savedGame.setCurrentLevelName("Level 2");
        assertEquals("Level 2", savedGame.getCurrentLevelName());
    }

    @Test
    @DisplayName("SavedGameTest_12: should set board correctly")
    void testSetBoard() {
        // New Board
        Set<Position> newWalls = new HashSet<>();
        Position newExit = new Position(5, 5);
        List<Position> newRedCarPositions = new ArrayList<>();
        newRedCarPositions.add(new Position(0, 0));
        newRedCarPositions.add(new Position(0, 1));
        Vehicle newRedCar = new Vehicle('R', newRedCarPositions, true, Orientation.HORIZONTAL);
        Map<Character, Vehicle> newVehicles = new HashMap<>();
        newVehicles.put('R', newRedCar);

        Board newBoard = new Board(5, 5, newWalls, newExit, newVehicles);

        savedGame.setBoard(newBoard);
        assertEquals(newBoard, savedGame.getBoard());
    }

    @Test
    @DisplayName("SavedGameTest_13: should set levelScore correctly")
    void testSetLevelScore() {
        savedGame.setLevelScore(30);
        assertEquals(30, savedGame.getLevelScore());
    }

    @Test
    @DisplayName("SavedGameTest_14: should set totalScore correctly")
    void testSetTotalScore() {
        savedGame.setTotalScore(200);
        assertEquals(200, savedGame.getTotalScore());
    }

    @Test
    @DisplayName("SavedGameTest_15: should set finished status correctly")
    void testSetFinished() {
        savedGame.setFinished(true);
        assertTrue(savedGame.isFinished());
    }

    @Test
    @DisplayName("SavedGameTest_16: should set movement history correctly with non-null list")
    void testSetMovementHistory() {
        List<Move> newMoves = new ArrayList<>();
        newMoves.add(new Move('A', Direction.NORTH));
        newMoves.add(new Move('B', Direction.SOUTH));
        savedGame.setMovementHistory(newMoves);

        assertEquals(2, savedGame.getMovementHistory().size());
        assertEquals('A', savedGame.getMovementHistory().get(0).getVehicleId());
    }

    @Test
    @DisplayName("SavedGameTest_17: should set empty list when setMovementHistory is called with null")
    void testSetMovementHistoryNull() {
        savedGame.setMovementHistory(null);
        assertTrue(savedGame.getMovementHistory().isEmpty());
    }

    @Test
    @DisplayName("SavedGameTest_18: should convert to GameState correctly")
    void testToGameState() {
        GameState restoredState = savedGame.toGameState();

        assertNotNull(restoredState);
        assertEquals(1, restoredState.getCurrentLevel());
        assertEquals("Level 1", restoredState.getCurrentLevelName());
        assertEquals(15, restoredState.getLevelScore());
        assertEquals(100, restoredState.getTotalScore());
        assertFalse(restoredState.isFinished());
        assertEquals(board, restoredState.getBoard());
        assertEquals(2, restoredState.getMovementHistory().size());
    }
}
