package net.katsuster.scenario;

import java.text.ParseException;
import java.util.Date;
import java.util.prefs.Preferences;

import net.katsuster.scenario.Scenario.SCORE_TYPE;

public class ScoreTatk extends Score {
    public static final String DATA_TIME = "Time";

    private long time;

    public ScoreTatk() {
        super();
        setScoreType(SCORE_TYPE.SCORE_TIME_ATTACK_6);
    }

    public ScoreTatk(SCORE_TYPE st) {
        super();
        setScoreType(st);
    }

    public ScoreTatk(SCORE_TYPE st, long tm, Date d) {
        super(d);
        setScoreType(st);
        setTime(tm);
    }

    public ScoreTatk(SCORE_TYPE st, long tm, String str) throws ParseException {
        super(str);
        setScoreType(st);
        setTime(tm);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScoreTatk)) {
            return false;
        }

        ScoreTatk obj = (ScoreTatk)o;
        return time == obj.time && getDate().equals(obj.getDate());
    }

    @Override
    public boolean lessThanEqual(Score o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScoreTatk)) {
            return false;
        }

        ScoreTatk obj = (ScoreTatk)o;
        return getTime() <= obj.getTime();
    }

    @Override
    public String toRankingString() {
        return String.format("%3d.%03d (%s)",
                getTime() / 1000, getTime() % 1000,
                getDateString().substring(0, 10));
    }

    @Override
    public boolean load(int rank, Preferences prefs) {
        long tm = prefs.getLong(getPrefName(rank + 1, DATA_TIME), -1);
        String strDate = prefs.get(getPrefName(rank + 1, DATA_DATE), "");

        if (tm == -1 || strDate.isEmpty()) {
            return false;
        }

        try {
            setTime(tm);
            setDateString(strDate);
        } catch (ParseException ex) {
            return false;
        }

        return true;
    }

    @Override
    public void save(int rank, Preferences prefs) {
        prefs.putLong(getPrefName(rank + 1, DATA_TIME), getTime());
        prefs.put(getPrefName(rank + 1, DATA_DATE), getDateString());
    }

    @Override
    public void erase(int rank, Preferences prefs) {
        prefs.putLong(getPrefName(rank + 1, DATA_TIME), -1);
        prefs.put(getPrefName(rank + 1, DATA_DATE), "");
    }

    public long getTime() {
        return time;
    }

    public void setTime(long t) {
        time = t;
    }

    @Override
    public String toString() {
        return "time:" + time + ", date:" + getDateString();
    }
}
