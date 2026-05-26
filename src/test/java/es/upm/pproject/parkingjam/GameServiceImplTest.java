package es.upm.pproject.parkingjam;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.GameState;
import es.upm.pproject.parkingjam.model.dto.Orientation;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;
import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.services.GameService;
import es.upm.pproject.parkingjam.model.services.GameServiceImpl;

@Nested
@DisplayName("GameService tests")
public class GameServiceImplTest {

    private GameService gameService;
    private GameState gameState;

    @BeforeEach
    void setUp() {

        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(1, 1));
        vehicleAPositions.add(new Position(1, 2));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        gameState = new GameState(board, 1, "Level 1");
        gameService = new GameServiceImpl();
        gameService.setGameState(gameState);
    }

    @Test
    @DisplayName("GameTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(gameService);
    }

    @Test
    @DisplayName("GameTest_02: should throw exception when constructor is called with null GameState")
    void testConstructorNullGameState() {
        GameService service = new GameServiceImpl();
        assertThrows(IllegalArgumentException.class, () -> service.setGameState(null));
    }

    @Test
    @DisplayName("GameTest_03: should return correct GameState when getGameState is called")
    void testGetGameState() {
        GameState retrieved = gameService.getGameState();
        assertNotNull(retrieved);
        assertEquals(gameState, retrieved);
        assertEquals(1, retrieved.getCurrentLevel());
        assertEquals("Level 1", retrieved.getCurrentLevelName());
    }

    @Test
    @DisplayName("GameTest_04: should return true and increase level score when move is successful")
    void testMoveSuccessful() {

        assertEquals(0, gameState.getLevelScore());

        boolean result = gameService.move('A', Direction.EAST);

        assertTrue(result);
        assertEquals(1, gameState.getLevelScore());
    }

    @Test
    @DisplayName("GameTest_05: should return false and not increase level score when move is unsuccessful")
    void testMoveUnsuccessful() {

        assertEquals(0, gameState.getLevelScore());

        boolean result = gameService.move('A', Direction.NORTH);

        assertFalse(result);
        assertEquals(0, gameState.getLevelScore());
    }

    @Test
    @DisplayName("GameTest_06: should return false when level is not completed")
    void testIsLevelCompletedFalse() {
        // Red car is still inside board
        boolean result = gameService.isLevelCompleted();

        assertFalse(result);
    }

    @Test
    @DisplayName("GameTest_07: should return true when level is completed")
    void testIsLevelCompletedTrue() {
        Set<Position> walls = new HashSet<>();

        Position exit = new Position(3, 6);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 7));
        redCarPositions.add(new Position(3, 8));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board newBoard = new Board(7, 7, walls, exit, vehicles);
        GameState newGameState = new GameState(newBoard, 1, "Level 1");
        GameService newGameService = new GameServiceImpl();
        newGameService.setGameState(newGameState);

        boolean result = newGameService.isLevelCompleted();

        assertTrue(result);
    }

    @Test
    @DisplayName("GameTest_08: should add level score to total score when finishLevel is called")
    void testFinishLevel() {

        gameState.setLevelScore(15);
        assertEquals(0, gameState.getTotalScore());

        gameService.finishLevel();

        assertEquals(15, gameState.getTotalScore());
    }

    @Test
    @DisplayName("GameTest_09: should add zero to total score when level score is zero")
    void testFinishLevelWithZeroScore() {
        gameState.setLevelScore(0);
        assertEquals(0, gameState.getTotalScore());

        gameService.finishLevel();

        assertEquals(0, gameState.getTotalScore());
    }

    @Test
    @DisplayName("GameTest_10: should accumulate total score across multiple finishLevel calls")
    void testMultipleFinishLevel() {
        gameState.setLevelScore(10);
        gameService.finishLevel();
        assertEquals(10, gameState.getTotalScore());

        gameState.setLevelScore(20);
        gameService.finishLevel();
        assertEquals(30, gameState.getTotalScore());
    }

    @Test
    @DisplayName("GameTest_11: should throw exception when moving non-existent vehicle")
    void testMoveNonExistentVehicle() {
        assertThrows(es.upm.pproject.parkingjam.model.exceptions.VehicleNotFoundException.class, () -> gameService.move('Z', Direction.EAST));
    }

    @Test
    @DisplayName("GameTest_12: should update gameState after multiple successful moves")
    void testMultipleMoves() {
        assertEquals(0, gameState.getLevelScore());

        assertTrue(gameService.move('A', Direction.EAST));
        assertEquals(1, gameState.getLevelScore());

        assertTrue(gameService.move('A', Direction.EAST));
        assertEquals(2, gameState.getLevelScore());

        assertTrue(gameService.move('A', Direction.WEST));
        assertEquals(3, gameState.getLevelScore());
    }

    @Test
    @DisplayName("GameTest_13: should not change level score when move is invalid due to orientation")
    void testInvalidMoveOrientation() {
        assertEquals(0, gameState.getLevelScore());

        assertFalse(gameService.move('A', Direction.NORTH));
        assertEquals(0, gameState.getLevelScore());

        assertFalse(gameService.move('A', Direction.SOUTH));
        assertEquals(0, gameState.getLevelScore());
    }

}
