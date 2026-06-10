package es.upm.pproject.parkingjam.unit.services;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.services.ScoreService;
import es.upm.pproject.parkingjam.model.services.ScoreServiceImpl;

@Nested
@DisplayName("ScoreService tests")
class ScoreServiceImplTest {

    private ScoreService scoreService;

    @BeforeEach
    void setUp() {
        scoreService = new ScoreServiceImpl();
    }

    @Test
    @DisplayName("ScoreServiceTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(scoreService);
    }

    @Test
    @DisplayName("ScoreServiceTest_02: should increase level score by 1 when increaseLevelScore is called")
    void testIncreaseLevelScore() {
        assertEquals(1, scoreService.increaseLevelScore(0));
        assertEquals(5, scoreService.increaseLevelScore(4));
        assertEquals(10, scoreService.increaseLevelScore(9));
        assertEquals(100, scoreService.increaseLevelScore(99));
    }

    @Test
    @DisplayName("ScoreServiceTest_03: should return 0 when resetLevelScore is called")
    void testResetLevelScore() {
        assertEquals(0, scoreService.resetLevelScore());
    }

    @Test
    @DisplayName("ScoreServiceTest_04: should add level score to total score correctly when addLevelScoreToTotalScore is called")
    void testAddLevelScoreToTotalScore() {
        assertEquals(10, scoreService.addLevelScoreToTotalScore(5, 5));
        assertEquals(0, scoreService.addLevelScoreToTotalScore(0, 0));
        assertEquals(100, scoreService.addLevelScoreToTotalScore(50, 50));
        assertEquals(25, scoreService.addLevelScoreToTotalScore(20, 5));
    }

    @Test
    @DisplayName("ScoreServiceTest_05: should increase level score multiple times correctly")
    void testMultipleIncreases() {
        int score = 0;
        score = scoreService.increaseLevelScore(score);
        assertEquals(1, score);
        score = scoreService.increaseLevelScore(score);
        assertEquals(2, score);
        score = scoreService.increaseLevelScore(score);
        assertEquals(3, score);
    }

    @Test
    @DisplayName("ScoreServiceTest_06: should add level score to total score multiple times correctly")
    void testMultipleAdditions() {
        int total = 0;
        total = scoreService.addLevelScoreToTotalScore(total, 10);
        assertEquals(10, total);
        total = scoreService.addLevelScoreToTotalScore(total, 20);
        assertEquals(30, total);
        total = scoreService.addLevelScoreToTotalScore(total, 5);
        assertEquals(35, total);
    }

    @Test
    @DisplayName("ScoreServiceTest_07: should decrease level score by 1 when decreaseLevelScore is called")
    void testDecreaseLevelScore() {
        assertEquals(-1, scoreService.decreaseLevelScore(0));
        assertEquals(3, scoreService.decreaseLevelScore(4));
        assertEquals(8, scoreService.decreaseLevelScore(9));
        assertEquals(98, scoreService.decreaseLevelScore(99));
    }

    @Test
    @DisplayName("ScoreServiceTest_08: should decrease level score multiple times correctly")
    void testMultipleDecreases() {
        int score = 10;
        score = scoreService.decreaseLevelScore(score);
        assertEquals(9, score);
        score = scoreService.decreaseLevelScore(score);
        assertEquals(8, score);
        score = scoreService.decreaseLevelScore(score);
        assertEquals(7, score);
    }

    @Test
    @DisplayName("ScoreServiceTest_09: should handle multiple increases and decreases correctly")
    void testMultipleIncreasesAndDecreases() {
        int score = 0;

        // Increase three times
        score = scoreService.increaseLevelScore(score);
        assertEquals(1, score);
        score = scoreService.increaseLevelScore(score);
        assertEquals(2, score);
        score = scoreService.increaseLevelScore(score);
        assertEquals(3, score);

        // Decrease two times
        score = scoreService.decreaseLevelScore(score);
        assertEquals(2, score);
        score = scoreService.decreaseLevelScore(score);
        assertEquals(1, score);

        // Increase again
        score = scoreService.increaseLevelScore(score);
        assertEquals(2, score);

        // Decrease again
        score = scoreService.decreaseLevelScore(score);
        assertEquals(1, score);
    }

    @Test
    @DisplayName("ScoreServiceTest_10: should handle increase, decrease and add to total correctly together")
    void testIncreaseDecreaseAndAddToTotal() {
        int score = 0;
        int total = 0;

        // Increase two times
        score = scoreService.increaseLevelScore(score);
        assertEquals(1, score);
        score = scoreService.increaseLevelScore(score);
        assertEquals(2, score);

        // Add level score to total
        total = scoreService.addLevelScoreToTotalScore(total, score);
        assertEquals(2, total);

        // increase again
        score = scoreService.increaseLevelScore(score);
        assertEquals(3, score);

        // decrease
        score = scoreService.decreaseLevelScore(score);
        assertEquals(2, score);

        // Add level score to total again
        total = scoreService.addLevelScoreToTotalScore(total, score);
        assertEquals(4, total);

        // decrease again
        score = scoreService.decreaseLevelScore(score);
        assertEquals(1, score);

        // Add level score to total again
        total = scoreService.addLevelScoreToTotalScore(total, score);
        assertEquals(5, total);
    }

}
