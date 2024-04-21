package net.katsuster.scenario;

import java.text.ParseException;
import java.util.Date;

import net.katsuster.scenario.Scenario.SCORE_TYPE;

public class ScoreTatkTest extends ScoreTatk {
    public ScoreTatkTest() {
        super();
        setScoreType(SCORE_TYPE.SCORE_TIME_ATTACK_TEST);
    }

    public ScoreTatkTest(long tm, Date d) {
        super(tm, d);
        setScoreType(SCORE_TYPE.SCORE_TIME_ATTACK_TEST);
    }

    public ScoreTatkTest(long tm, String str) throws ParseException {
        super(tm, str);
        setScoreType(SCORE_TYPE.SCORE_TIME_ATTACK_TEST);
    }
}
