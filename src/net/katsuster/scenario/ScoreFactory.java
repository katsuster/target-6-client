package net.katsuster.scenario;

import net.katsuster.scenario.Scenario.SCORE_TYPE;

public class ScoreFactory {
    private ScoreFactory() {
        //do nothing
    }

    public static Score createScore(SCORE_TYPE t) {
        return switch (t) {
            case SCORE_EMPTY -> new Score();
            case SCORE_COUNT_UP_30 -> new ScoreCount(SCORE_TYPE.SCORE_COUNT_UP_30);
            case SCORE_COUNT_UP_20 -> new ScoreCount(SCORE_TYPE.SCORE_COUNT_UP_20);
            case SCORE_COUNT_UP_15 -> new ScoreCount(SCORE_TYPE.SCORE_COUNT_UP_15);
            case SCORE_SPEED_SHOOT_6 -> new ScoreTime(SCORE_TYPE.SCORE_SPEED_SHOOT_6);
            case SCORE_SPEED_SHOOT_5 -> new ScoreTime(SCORE_TYPE.SCORE_SPEED_SHOOT_5);
            case SCORE_SPEED_SHOOT_4 -> new ScoreTime(SCORE_TYPE.SCORE_SPEED_SHOOT_4);
            case SCORE_TIME_ATTACK_6 -> new ScoreTime(SCORE_TYPE.SCORE_TIME_ATTACK_6);
            case SCORE_TIME_ATTACK_5 -> new ScoreTime(SCORE_TYPE.SCORE_TIME_ATTACK_5);
            case SCORE_TIME_ATTACK_4 -> new ScoreTime(SCORE_TYPE.SCORE_TIME_ATTACK_4);
            case SCORE_TIME_ATTACK_TEST -> new ScoreTest(SCORE_TYPE.SCORE_TIME_ATTACK_TEST);
        };
    }
}
