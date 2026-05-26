package es.upm.pproject.parkingjam.model.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreServiceImpl implements ScoreService {
    private static final Logger logger = LoggerFactory.getLogger(ScoreServiceImpl.class);

    @Override
    public int increaseLevelScore(int currentLevelScore) {
        int newScore = currentLevelScore + 1;
        logger.info("Level score increased from {} to {}", currentLevelScore, newScore);
        return newScore;
    }

    @Override
    public int resetLevelScore() {
        logger.info("Level score reset to 0");
        return 0;
    }

    @Override
    public int addLevelScoreToTotalScore(int totalScore, int levelScore) {
        int newTotal = totalScore + levelScore;
        logger.info("Total score updated: {} + {} = {}", totalScore, levelScore, newTotal);
        return newTotal;
    }
}
