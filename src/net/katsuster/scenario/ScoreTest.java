package net.katsuster.scenario;

import java.text.ParseException;
import java.util.Date;

import net.katsuster.scenario.Scenario.SCORE_TYPE;

public class ScoreTest extends ScoreTime {
    public ScoreTest() {
        super();
        setScoreType(SCORE_TYPE.SCORE_TIME_ATTACK_TEST);
    }

    public ScoreTest(SCORE_TYPE st) {
        super();
        setScoreType(st);
    }

    public ScoreTest(SCORE_TYPE st, long tm, Date d) {
        super(st, tm, d);
    }

    public ScoreTest(SCORE_TYPE st, long tm, String str) throws ParseException {
        super(st, tm, str);
    }
}
