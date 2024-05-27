package net.katsuster.scenario;

import java.text.ParseException;
import java.util.Date;

import net.katsuster.scenario.Scenario.SCORE_TYPE;

public class ScoreTatkTest extends ScoreTatk {
    public ScoreTatkTest() {
        super();
        setScoreType(SCORE_TYPE.SCORE_TIME_ATTACK_TEST);
    }

    public ScoreTatkTest(SCORE_TYPE st) {
        super();
        setScoreType(st);
    }

    public ScoreTatkTest(SCORE_TYPE st, long tm, Date d) {
        super(st, tm, d);
    }

    public ScoreTatkTest(SCORE_TYPE st, long tm, String str) throws ParseException {
        super(st, tm, str);
    }
}
