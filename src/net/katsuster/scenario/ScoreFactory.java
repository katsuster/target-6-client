package net.katsuster.scenario;

import net.katsuster.scenario.Scenario.SCORE_TYPE;

public class ScoreFactory {
    private ScoreFactory() {
        //do nothing
    }

    public static Score createScore(SCORE_TYPE t) {
        return switch (t) {
            case SCORE_EMPTY -> new Score();
            case SCORE_COUNT_UP -> new ScoreCntup();
            case SCORE_TIME_ATTACK -> new ScoreTatk();

            case SCORE_TIME_ATTACK_TEST -> new ScoreTatkTest();
        };
    }
}
