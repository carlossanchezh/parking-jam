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

}
