package es.upm.pproject.parkingjam.unit.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dao.SaveGameDAO;
import es.upm.pproject.parkingjam.model.dto.*;
import es.upm.pproject.parkingjam.model.exceptions.SaveGameDAOException;

@Nested
@DisplayName("SaveGameDAO tests")
public class SaveGameDAOTest {

    private SaveGameDAO saveGameDAO;
    private Path saveDir;
    private GameState gameState;
    private Board board;

    @BeforeEach
    void setUp() throws IOException {
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

        // Create board
        Set<Position> walls = new HashSet<>();
        walls.add(new Position(0, 0));
        walls.add(new Position(0, 1));
        walls.add(new Position(5, 5));

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        board = new Board(7, 7, walls, exit, vehicles);

        // Create game state
        gameState = new GameState(board, 1, "Level 1");
        gameState.setLevelScore(15);
        gameState.setTotalScore(100);

        List<Move> moves = new ArrayList<>();
        moves.add(new Move('R', Direction.EAST));
        moves.add(new Move('R', Direction.EAST));
        for (Move move : moves) {
            gameState.addMovement(move);
        }
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
    @DisplayName("SaveGameDAOTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(saveGameDAO);
    }

    @Test
    @DisplayName("SaveGameDAOTest_02: should save game successfully")
    void testSaveGame() throws SaveGameDAOException {
        saveGameDAO.saveGame(gameState);
        Path saveFile = saveDir.resolve("savegame.txt");
        assertTrue(Files.exists(saveFile));
    }

    @Test
    @DisplayName("SaveGameDAOTest_03: should throw SaveGameDAOException when saving null GameState")
    void testSaveNullGameState() {
        assertThrows(SaveGameDAOException.class, () -> saveGameDAO.saveGame(null));
    }

    @Test
    @DisplayName("SaveGameDAOTest_04: should load game successfully")
    void testLoadGame() throws SaveGameDAOException {
        saveGameDAO.saveGame(gameState);

        GameState loadedState = saveGameDAO.loadGame();

        assertNotNull(loadedState);
        assertEquals(gameState.getCurrentLevel(), loadedState.getCurrentLevel());
        assertEquals(gameState.getCurrentLevelName(), loadedState.getCurrentLevelName());
        assertEquals(gameState.getLevelScore(), loadedState.getLevelScore());
        assertEquals(gameState.getTotalScore(), loadedState.getTotalScore());
        assertEquals(gameState.isFinished(), loadedState.isFinished());
        assertEquals(gameState.getMovementHistory().size(), loadedState.getMovementHistory().size());
    }

    @Test
    @DisplayName("SaveGameDAOTest_05: should throw SaveGameDAOException when loading from non-existent file")
    void testLoadNonExistentFile() {
        assertThrows(SaveGameDAOException.class, () -> saveGameDAO.loadGame());
    }

    @Test
    @DisplayName("SaveGameDAOTest_06: should save and load game with empty movement history")
    void testSaveAndLoadEmptyHistory() throws SaveGameDAOException {
        GameState emptyHistoryState = new GameState(board, 1, "Level 1");
        emptyHistoryState.setLevelScore(5);
        emptyHistoryState.setTotalScore(10);

        saveGameDAO.saveGame(emptyHistoryState);
        GameState loadedState = saveGameDAO.loadGame();

        assertEquals(0, loadedState.getMovementHistory().size());
        assertEquals(5, loadedState.getLevelScore());
        assertEquals(10, loadedState.getTotalScore());
    }

    @Test
    @DisplayName("SaveGameDAOTest_07: should save and load game with multiple moves")
    void testSaveAndLoadWithMultipleMoves() throws SaveGameDAOException {
        gameState.addMovement(new Move('A', Direction.WEST));
        gameState.addMovement(new Move('B', Direction.SOUTH));
        gameState.addMovement(new Move('C', Direction.NORTH));

        saveGameDAO.saveGame(gameState);
        GameState loadedState = saveGameDAO.loadGame();

        assertEquals(5, loadedState.getMovementHistory().size());
    }

    @Test
    @DisplayName("SaveGameDAOTest_08: should preserve board details after save and load")
    void testPreserveBoardDetails() throws SaveGameDAOException {
        saveGameDAO.saveGame(gameState);
        GameState loadedState = saveGameDAO.loadGame();

        Board originalBoard = gameState.getBoard();
        Board loadedBoard = loadedState.getBoard();

        assertEquals(originalBoard.getRows(), loadedBoard.getRows());
        assertEquals(originalBoard.getColumns(), loadedBoard.getColumns());
        assertEquals(originalBoard.getExit(), loadedBoard.getExit());
        assertEquals(originalBoard.getWalls().size(), loadedBoard.getWalls().size());
        assertEquals(originalBoard.getVehicles().size(), loadedBoard.getVehicles().size());
    }

    @Test
    @DisplayName("SaveGameDAOTest_09: should overwrite existing save file")
    void testOverwriteSaveFile() throws SaveGameDAOException {
        saveGameDAO.saveGame(gameState);

        gameState.setLevelScore(30);
        gameState.setTotalScore(200);

        saveGameDAO.saveGame(gameState);

        GameState loadedState = saveGameDAO.loadGame();
        assertEquals(30, loadedState.getLevelScore());
        assertEquals(200, loadedState.getTotalScore());
    }

    @Test
    @DisplayName("SaveGameDAOTest_10: should load saved game with finished flag set to true")
    void testSaveAndLoadFinishedTrue() throws SaveGameDAOException {
        gameState.setFinished(true);
        saveGameDAO.saveGame(gameState);

        GameState loadedState = saveGameDAO.loadGame();

        assertTrue(loadedState.isFinished());
    }

    @Test
    @DisplayName("SaveGameDAOTest_11: should load saved game with finished flag set to false")
    void testSaveAndLoadFinishedFalse() throws SaveGameDAOException {
        gameState.setFinished(false);
        saveGameDAO.saveGame(gameState);

        GameState loadedState = saveGameDAO.loadGame();

        assertFalse(loadedState.isFinished());
    }
}
