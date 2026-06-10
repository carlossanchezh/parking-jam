package es.upm.pproject.parkingjam;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;


import es.upm.pproject.parkingjam.model.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Nested
@DisplayName("Level tests")
class LevelTest {

    private static Level level;
    private static Board board;

    @BeforeEach
    void setUp() {
        // Create a valid board for testing
        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        board = new Board(7, 7, walls, exit, vehicles);

        level = new Level("Test Level", 7, 7, board);
    }

    @Test
    @DisplayName("LevelTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(level);
    }

    @Test
    @DisplayName("LevelTest_02: should return correct name when getName is called")
    void testGetName() {
        assertEquals("Test Level", level.getName());
    }

    @Test
    @DisplayName("LevelTest_03: should return correct rows when getnRows is called")
    void testGetRows() {
        assertEquals(7, level.getnRows());
    }

    @Test
    @DisplayName("LevelTest_04: should return correct columns when getnCols is called")
    void testGetColumns() {
        assertEquals(7, level.getnCols());
    }

    @Test
    @DisplayName("LevelTest_05: should return correct board when getBoard is called")
    void testGetBoard() {
        assertEquals(board, level.getBoard());
    }

    @Test
    @DisplayName("LevelTest_06: should throw exception when name is null")
    void testConstructorNullName() {
        assertThrows(IllegalArgumentException.class, () -> new Level(null, 7, 7, board));
    }

    @Test
    @DisplayName("LevelTest_07: should throw exception when name is empty")
    void testConstructorEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Level("", 7, 7, board));
        assertThrows(IllegalArgumentException.class, () -> new Level("   ", 7, 7, board));
    }

    @Test
    @DisplayName("LevelTest_08: should throw exception when rows is invalid")
    void testConstructorInvalidRows() {
        assertThrows(IllegalArgumentException.class, () -> new Level("Test", 0, 7, board));
        assertThrows(IllegalArgumentException.class, () -> new Level("Test", -1, 7, board));
    }

    @Test
    @DisplayName("LevelTest_09: should throw exception when columns is invalid")
    void testConstructorInvalidColumns() {
        assertThrows(IllegalArgumentException.class, () -> new Level("Test", 7, 0, board));
        assertThrows(IllegalArgumentException.class, () -> new Level("Test", 7, -1, board));
    }

    @Test
    @DisplayName("LevelTest_10: should throw exception when board is null")
    void testConstructorNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> new Level("Test", 7, 7, null));
    }

}
