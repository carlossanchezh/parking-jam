package es.upm.pproject.parkingjam.model.services;

public interface ScoreService {
    int increaseLevelScore(int currentLevelScore);

    int decreaseLevelScore(int currentLevelScore);

    int resetLevelScore();

    int addLevelScoreToTotalScore(int totalScore, int levelScore);
}
