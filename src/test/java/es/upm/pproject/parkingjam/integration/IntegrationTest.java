package es.upm.pproject.parkingjam.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dao.LevelDAO;
import es.upm.pproject.parkingjam.model.dao.SaveGameDAO;
import es.upm.pproject.parkingjam.model.dto.*;
import es.upm.pproject.parkingjam.model.exceptions.LevelDAOException;
import es.upm.pproject.parkingjam.model.exceptions.SaveGameDAOException;
import es.upm.pproject.parkingjam.model.services.*;

@Nested
@DisplayName("Game Integration Tests")
public class IntegrationTest {

    private LevelDAO levelDAO;
    private GameService gameService;
    private SaveGameDAO saveGameDAO;
    private GameState gameState;
    private Board board;
    private Path saveDir;

    @BeforeEach
    void setUp() throws LevelDAOException, IOException {
        levelDAO = new LevelDAO();
        gameService = new GameServiceImpl();
        saveGameDAO = new SaveGameDAO();
        saveDir = Paths.get("saves");

        // Clean up before test
        if (Files.exists(saveDir)) {
            // Delete all files inside the directory
            for (File file : saveDir.toFile().listFiles()) {
                file.delete();
            }
            // Delete the empty directory
            Files.deleteIfExists(saveDir);
        }

        // Create directory if it doesn't exist
        Files.createDirectories(saveDir);

        // Load a valid level
        Level level = levelDAO.loadLevel("level_1.txt");
        board = level.getBoard();
        gameState = new GameState(board, 1, level.getName());
        gameService.setGameState(gameState);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temporary files
        // Delete all files inside the directory
        for (File file : saveDir.toFile().listFiles()) {
            file.delete();
        }
        // Delete the empty directory
        Files.deleteIfExists(saveDir);
    }

    @Test
    @DisplayName("IntegrationTest_01: Move vehicle D vertically, undo move and move again")
    void testMoveVehicleD() {
        // Get vehicle D
        Vehicle vehicleD = board.getVehicles().get('d');
        assertNotNull(vehicleD);

        // Record initial positions
        List<Position> initialPositions = vehicleD.getPositions();
        assertEquals(new Position(3, 1), initialPositions.get(0));
        assertEquals(new Position(4, 1), initialPositions.get(1));
        assertEquals(new Position(5, 1), initialPositions.get(2));

        // Move SOUTH
        boolean moved = gameService.move('d', Direction.SOUTH);
        assertTrue(moved);
        assertEquals(1, gameState.getLevelScore());
        assertEquals(1, gameState.getMovementHistory().size());

        // Verify new positions
        List<Position> newPositions = vehicleD.getPositions();
        assertEquals(new Position(4, 1), newPositions.get(0));
        assertEquals(new Position(5, 1), newPositions.get(1));
        assertEquals(new Position(6, 1), newPositions.get(2));

        // Undo move
        boolean undone = gameService.undoLastMovement();
        assertTrue(undone);
        assertEquals(0, gameState.getLevelScore());
        assertEquals(0, gameState.getMovementHistory().size());

        // Verify positions restored
        assertEquals(initialPositions, vehicleD.getPositions());

        // Move again
        moved = gameService.move('d', Direction.SOUTH);
        assertTrue(moved);
        assertEquals(1, gameState.getLevelScore());
        assertEquals(1, gameState.getMovementHistory().size());
        assertNotEquals(initialPositions, vehicleD.getPositions());
    }

    @Test
    @DisplayName("IntegrationTest_02: Move vehicle F horizontally, undo move and move again")
    void testMoveVehicleF() {
        // Get vehicle F
        Vehicle vehicleF = board.getVehicles().get('f');
        assertNotNull(vehicleF);

        // Record initial positions
        List<Position> initialPositions = vehicleF.getPositions();
        assertEquals(new Position(4, 3), initialPositions.get(0));
        assertEquals(new Position(4, 4), initialPositions.get(1));
        assertEquals(new Position(4, 5), initialPositions.get(2));

        // Move EAST
        boolean moved = gameService.move('f', Direction.EAST);
        assertTrue(moved);
        assertEquals(1, gameState.getLevelScore());
        assertEquals(1, gameState.getMovementHistory().size());

        // Verify new positions
        List<Position> newPositions = vehicleF.getPositions();
        assertEquals(new Position(4, 4), newPositions.get(0));
        assertEquals(new Position(4, 5), newPositions.get(1));
        assertEquals(new Position(4, 6), newPositions.get(2));

        // Undo move
        boolean undone = gameService.undoLastMovement();
        assertTrue(undone);
        assertEquals(0, gameState.getLevelScore());
        assertEquals(0, gameState.getMovementHistory().size());

        // Verify positions restored
        assertEquals(initialPositions, vehicleF.getPositions());

        // Move again
        moved = gameService.move('f', Direction.EAST);
        assertTrue(moved);
        assertEquals(1, gameState.getLevelScore());
        assertEquals(1, gameState.getMovementHistory().size());
        assertNotEquals(initialPositions, vehicleF.getPositions());
    }

    @Test
    @DisplayName("IntegrationTest_03: Move vehicle B and verify collision detection with other vehicle ")
    void testMoveVehicleBAndCollision() {
        // Get vehicle B
        Vehicle vehicleB = board.getVehicles().get('b');
        assertNotNull(vehicleB);

        // Record initial positions
        List<Position> initialPositions = vehicleB.getPositions();
        assertEquals(new Position(1, 3), initialPositions.get(0));
        assertEquals(new Position(1, 4), initialPositions.get(1));
        assertEquals(new Position(1, 5), initialPositions.get(2));

        // Move WEST (should collide)
        boolean moved = gameService.move('b', Direction.WEST);
        assertFalse(moved);

        // Score should not increase
        assertEquals(0, gameState.getLevelScore());
        assertEquals(0, gameState.getMovementHistory().size());
        assertEquals(initialPositions, vehicleB.getPositions());
    }

    @Test
    @DisplayName("IntegrationTest_04: Move vehicle G and verify collision detection with border ")
    void testMoveVehicleGAndCollision() {
        // Get vehicle G
        Vehicle vehicleG = board.getVehicles().get('g');
        assertNotNull(vehicleG);

        // Record initial positions
        List<Position> initialPositions = vehicleG.getPositions();
        assertEquals(new Position(6, 4), initialPositions.get(0));
        assertEquals(new Position(6, 5), initialPositions.get(1));
        assertEquals(new Position(6, 6), initialPositions.get(2));

        // Move EAST (should collide)
        boolean moved = gameService.move('g', Direction.EAST);
        assertFalse(moved);

        // Score should not increase
        assertEquals(0, gameState.getLevelScore());
        assertEquals(0, gameState.getMovementHistory().size());
        assertEquals(initialPositions, vehicleG.getPositions());
    }

    @Test
    @DisplayName("IntegrationTest_05: Save and load game after multiple moves then do undo")
    void testSaveAndLoadAfterMultipleMoves() throws SaveGameDAOException, LevelDAOException {

        Vehicle vehicleD = board.getVehicles().get('d');
        Vehicle vehicleE = board.getVehicles().get('e');
        Vehicle vehicleF = board.getVehicles().get('f');

        // Make moves
        gameService.move('d', Direction.SOUTH);
        gameService.move('e', Direction.NORTH);
        gameService.move('f', Direction.EAST);

        assertEquals(3, gameState.getLevelScore());
        assertEquals(3, gameState.getMovementHistory().size());

        List<Position> initialPositionsD = vehicleD.getPositions();
        List<Position> initialPositionsE = vehicleE.getPositions();
        List<Position> initialPositionsF = vehicleF.getPositions();

        // Save game
        saveGameDAO.saveGame(gameState);

        // Load saved game
        GameState loadedState = saveGameDAO.loadGame();
        gameService.setGameState(loadedState);

        // Verify loaded state matches the state after moves
        assertEquals(3, loadedState.getLevelScore());
        assertEquals(3, loadedState.getMovementHistory().size());
        assertEquals(1, loadedState.getCurrentLevel());
        assertEquals("Initial level", loadedState.getCurrentLevelName());

        // Verify board positions are preserved
        Vehicle loadedVehicleD = loadedState.getBoard().getVehicles().get('d');
        Vehicle loadedVehicleE = loadedState.getBoard().getVehicles().get('e');
        Vehicle loadedVehicleF = loadedState.getBoard().getVehicles().get('f');

        assertEquals(initialPositionsD, loadedVehicleD.getPositions());
        assertEquals(initialPositionsE, loadedVehicleE.getPositions());
        assertEquals(initialPositionsF, loadedVehicleF.getPositions());

        // Undo
        boolean undone = gameService.undoLastMovement();
        assertTrue(undone);
        assertEquals(2, gameService.getGameState().getLevelScore());
        assertEquals(2, gameService.getGameState().getMovementHistory().size());
    }

    @Test
    @DisplayName("IntegrationTest_06: Complete the level moving all necessary cars and red car towards exit and verify victory detection, after finishing initial level load level_2 and move")
    void testRedCarVictory() throws LevelDAOException {
        // Get vehicle RED
        Vehicle vehicleRED = board.getVehicles().get('*');
        assertNotNull(vehicleRED);

        // Record initial positions
        List<Position> initialPositions = vehicleRED.getPositions();
        assertEquals(new Position(2, 4), initialPositions.get(0));
        assertEquals(new Position(3, 4), initialPositions.get(1));

        // Get vehicle A
        Vehicle vehicleA = board.getVehicles().get('a');
        assertNotNull(vehicleA);

        // Get vehicle B
        Vehicle vehicleB = board.getVehicles().get('b');
        assertNotNull(vehicleB);

        // Get vehicle C
        Vehicle vehicleC = board.getVehicles().get('c');
        assertNotNull(vehicleC);

        // Get vehicle D
        Vehicle vehicleD = board.getVehicles().get('d');
        assertNotNull(vehicleD);

        // Get vehicle E
        Vehicle vehicleE = board.getVehicles().get('e');
        assertNotNull(vehicleE);

        // Get vehicle F
        Vehicle vehicleF = board.getVehicles().get('f');
        assertNotNull(vehicleF);

        // Get vehicle G
        Vehicle vehicleG = board.getVehicles().get('g');
        assertNotNull(vehicleG);

        // Do movements to win the game
        assertTrue(gameService.move('c', Direction.SOUTH));
        assertTrue(gameService.move('b', Direction.EAST));
        assertTrue(gameService.move('a', Direction.EAST));
        assertTrue(gameService.move('d', Direction.NORTH));
        assertTrue(gameService.move('d', Direction.NORTH));
        assertTrue(gameService.move('e', Direction.NORTH));
        assertTrue(gameService.move('e', Direction.NORTH));
        assertTrue(gameService.move('e', Direction.NORTH));
        assertTrue(gameService.move('f', Direction.WEST));
        assertTrue(gameService.move('f', Direction.WEST));
        assertTrue(gameService.move('g', Direction.WEST));
        assertTrue(gameService.move('g', Direction.WEST));
        assertTrue(gameService.move('g', Direction.WEST));

        assertEquals(13, gameState.getLevelScore());
        assertEquals(13, gameState.getMovementHistory().size());

        //Move red car to win
        assertTrue(gameService.move('*', Direction.SOUTH));
        assertTrue(gameService.move('*', Direction.SOUTH));
        assertTrue(gameService.move('*', Direction.SOUTH));
        assertTrue(gameService.move('*', Direction.SOUTH));
        assertTrue(gameService.move('*', Direction.SOUTH));
        assertTrue(gameService.move('*', Direction.SOUTH));

        assertEquals(19, gameState.getLevelScore());
        assertEquals(19, gameState.getMovementHistory().size());

        // After moving red car out, level should be completed
        assertTrue(gameService.isLevelCompleted());

        // Capture scores before calling finishLevel
        int totalBefore = gameState.getTotalScore();
        int levelScore = gameState.getLevelScore();

        // Execute finishLevel this should transfer level score to total score
        gameService.finishLevel();

        // TotalScore should increase by levelScore
        assertEquals(totalBefore + levelScore, gameState.getTotalScore());

        // LevelScore should remain unchanged
        assertEquals(levelScore, gameState.getLevelScore());

        //Load next level
        Level level2 = levelDAO.loadLevel("level_2.txt");

        // Create new GameState
        GameState game2State = new GameState(level2.getBoard(), 2, level2.getName());
        game2State.setTotalScore(gameState.getTotalScore());

        // Set new state
        gameService.setGameState(game2State);

        // Verificar level 2 is loaded correctly
        assertEquals(2, gameService.getGameState().getCurrentLevel());
        assertEquals("Cross Traffic", gameService.getGameState().getCurrentLevelName()); // Ajusta según tu level_2.txt

        assertEquals(0, gameService.getGameState().getLevelScore());
        assertEquals(19, gameService.getGameState().getTotalScore());

        assertTrue(gameService.getGameState().getMovementHistory().isEmpty());
        assertNotNull(gameService.getGameState().getBoard());

        // Get Level 2 vehicle A
        Vehicle vehicleA2 = gameService.getGameState().getBoard().getVehicles().get('a');
        assertNotNull(vehicleA2);

        //Move should work after load level 2
        assertTrue(gameService.move('a', Direction.EAST));

        assertEquals(1, gameService.getGameState().getLevelScore());
        assertEquals(1, gameService.getGameState().getMovementHistory().size());
    }

    @Test
    @DisplayName("IntegrationTest_07: Restart level resets board, score and move history after multiple moves")
    void testRestartLevel() throws LevelDAOException {

        // Get some vehicles
        Vehicle vehicleD = board.getVehicles().get('d');
        assertNotNull(vehicleD);

        Vehicle vehicleF = board.getVehicles().get('f');
        assertNotNull(vehicleF);

        // Record initial positions (before any moves)
        List<Position> initialPositionsD = new ArrayList<>(vehicleD.getPositions());
        List<Position> initialPositionsF = new ArrayList<>(vehicleF.getPositions());

        // Record original Total Score should be 0
        int originalTotalScore = gameState.getTotalScore();

        // Make several valid moves
        assertTrue(gameService.move('d', Direction.SOUTH));
        assertTrue(gameService.move('f', Direction.EAST));

        // Verify state has changed after moves
        assertEquals(2, gameState.getLevelScore());
        assertEquals(2, gameState.getMovementHistory().size());
        assertNotEquals(initialPositionsD, vehicleD.getPositions());
        assertNotEquals(initialPositionsF, vehicleF.getPositions());

        // Reload the level from file (simulates restartLevel logic)
        Level reloadedLevel = levelDAO.loadLevel("level_1.txt");
        Board reloadedBoard = reloadedLevel.getBoard();

        // Create new game state for the same level
        GameState restartedState = new GameState(reloadedBoard, 1, reloadedLevel.getName());
        restartedState.setTotalScore(originalTotalScore);

        // Apply the restarted state
        gameService.setGameState(restartedState);

        // Verify level score is reset to 0
        assertEquals(0, gameService.getGameState().getLevelScore());

        // Verify total score is preserved (not changed by restart)
        assertEquals(originalTotalScore, gameService.getGameState().getTotalScore());

        // Verify move history is empty
        assertEquals(0, gameService.getGameState().getMovementHistory().size());

        // Verify all vehicle positions are restored to initial
        Vehicle restartedVehicleD = gameService.getGameState().getBoard().getVehicles().get('d');
        Vehicle restartedVehicleF = gameService.getGameState().getBoard().getVehicles().get('f');

        assertEquals(initialPositionsD, restartedVehicleD.getPositions());
        assertEquals(initialPositionsF, restartedVehicleF.getPositions());
    }

}
