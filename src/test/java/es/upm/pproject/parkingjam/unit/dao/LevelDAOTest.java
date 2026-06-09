package es.upm.pproject.parkingjam;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.Level;
import es.upm.pproject.parkingjam.model.dao.LevelDAO;
import es.upm.pproject.parkingjam.model.exceptions.LevelDAOException;

@Nested
@DisplayName("LevelDAO tests")
class LevelDAOTest {

    private LevelDAO levelDAO;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {

        levelDAO = new LevelDAO();

        // Create temporary levels directory
        tempDir = Paths.get("levels");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temporary files
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            // Ignore
                        }
                    });
        }
    }

    private void createLevelFile(String fileName, String content) throws IOException {
        Path filePath = tempDir.resolve(fileName);
        Files.writeString(filePath, content);
    }

    @Test
    @DisplayName("LevelDAOTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(levelDAO);
    }

    @Test
    @DisplayName("LevelDAOTest_02: should load a valid level file successfully")
    void testLoadValidLevel() throws LevelDAOException {

        Level level = levelDAO.loadLevel("level_1.txt");

        assertNotNull(level);
        assertEquals("Initial level", level.getName());
        assertEquals(8, level.getnRows());
        assertEquals(8, level.getnCols());
        assertNotNull(level.getBoard());
    }

    @Test
    @DisplayName("LevelDAOTest_03: should throw LevelDAOException when file does not exist")
    void testLoadNonExistentFile() {
        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("nonexistent.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_04: should throw LevelDAOException when level name is missing")
    void testLoadMissingName() throws IOException {
        String content = "\n" +
                "8 8\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d..*..+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "++++@+++\n";

        createLevelFile("level_missing_name.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_missing_name.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_05: should throw LevelDAOException when dimensions are missing")
    void testLoadMissingDimensions() throws IOException {
        String content = "Level 1\n";

        createLevelFile("level_missing_dim.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_missing_dim.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_06: should throw LevelDAOException when dimensions format is invalid")
    void testLoadInvalidDimensionsFormat() throws IOException {
        String content = "Level 1\n" +
                "8 8 8\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d..*..+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "++++@+++\n";

        createLevelFile("level_invalid_dim_format.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_invalid_dim_format.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_07: should throw LevelDAOException when dimensions are not numbers")
    void testLoadDimensionsNotNumbers() throws IOException {
        String content = "Level 1\n" +
                "a b\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d..*..+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "++++@+++\n";

        createLevelFile("level_dim_not_numbers.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_dim_not_numbers.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_08: should throw LevelDAOException when dimensions are invalid (zero or negative)")
    void testLoadInvalidDimensions() throws IOException {
        String content = "Level 1\n" +
                "0 8\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d..*..+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "++++@+++\n";

        createLevelFile("level_invalid_dimensions.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_invalid_dimensions.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_09: should throw LevelDAOException when board row is missing")
    void testLoadMissingBoardRow() throws IOException {
        String content = "Level 1\n" +
                "8 8\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d..*..+\n" +
                "+d.fff.+\n" +
                "+de....+\n";

        createLevelFile("level_missing_row.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_missing_row.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_10: should throw LevelDAOException when row has incorrect size")
    void testLoadIncorrectRowSize() throws IOException {
        String content = "Level 1\n" +
                "8 8\n" +
                "+++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d..*..+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "++++@+++\n";

        createLevelFile("level_incorrect_row_size.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_incorrect_row_size.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_11: should throw LevelDAOException when board has invalid character")
    void testLoadInvalidCharacter() throws IOException {
        String content = "Level 1\n" +
                "8 8\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d..X..+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "++++@+++\n";

        createLevelFile("level_invalid_char.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_invalid_char.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_12: should throw LevelDAOException when board has no exit")
    void testLoadNoExit() throws IOException {
        String content = "Level 1\n" +
                "8 8\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d..*..+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "+++++++ \n";

        createLevelFile("level_no_exit.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_no_exit.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_13: should throw LevelDAOException when board has multiple exits")
    void testLoadMultipleExits() throws IOException {
        String content = "Level 1\n" +
                "8 8\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d..*..+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "++++@++@\n";

        createLevelFile("level_multiple_exits.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_multiple_exits.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_14: should throw LevelDAOException when board has no red car")
    void testLoadNoRedCar() throws IOException {
        String content = "Level 1\n" +
                "8 8\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+.....c+\n" +
                "+d.....+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "++++@+++\n";

        createLevelFile("level_no_redcar.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_no_redcar.txt"));
    }

    @Test
    @DisplayName("LevelDAOTest_15: should throw LevelDAOException when red car has only one cell")
    void testLoadRedCarWithOneCell() throws IOException {
        String content = "Level 1\n" +
                "8 8\n" +
                "++++++++\n" +
                "+aabbbc+\n" +
                "+...*.c+\n" +
                "+d.....+\n" +
                "+d.fff.+\n" +
                "+de....+\n" +
                "+.e.ggg+\n" +
                "++++@+++\n";

        createLevelFile("level_redcar_one_cell.txt", content);

        assertThrows(LevelDAOException.class, () -> levelDAO.loadLevel("level_redcar_one_cell.txt"));
    }

}
