package es.upm.pproject.parkingjam.model.services;

public class ScoreServiceImpl implements ScoreService {
    @Override
    public int increaseLevelScore(int currentLevelScore) {
        return currentLevelScore + 1;
    }

    @Override
    public int resetLevelScore() {
        return 0;
    }

    @Override
    public int addLevelScoreToTotalScore(int totalScore, int levelScore) {
        return totalScore + levelScore;
    }
}
