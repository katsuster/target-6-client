package net.katsuster.scenario;

import java.text.ParseException;
import java.util.Date;
import java.util.prefs.Preferences;

import net.katsuster.scenario.Scenario.SCORE_TYPE;

public class ScoreCount extends Score {
    public static final String DATA_COUNT = "Count";
    public static final String DATA_TIME = "Time";

    private int count;
    private long time;

    public ScoreCount() {
        super();
        setScoreType(SCORE_TYPE.SCORE_COUNT_UP_30);
    }

    public ScoreCount(SCORE_TYPE st) {
        super();
        setScoreType(st);
    }

    public ScoreCount(SCORE_TYPE st, int c, long tm, Date d) {
        super(d);
        setScoreType(st);
        setCount(c);
        setTime(tm);
    }

    public ScoreCount(SCORE_TYPE st, int c, long tm, String str) throws ParseException {
        super(str);
        setScoreType(st);
        setCount(c);
        setTime(tm);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScoreCount)) {
            return false;
        }

        ScoreCount obj = (ScoreCount)o;
        return count == obj.count && time == obj.time && getDate().equals(obj.getDate());
    }

    @Override
    public boolean lessThanEqual(Score o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScoreCount)) {
            return false;
        }

        ScoreCount obj = (ScoreCount)o;
        if (getCount() == obj.getCount()) {
            return getTime() <= obj.getTime();
        } else {
            return  getCount() >= obj.getCount();
        }
    }

    @Override
    public String toRankingString() {
        return String.format("%3d@%2d.%03d (%s)",
                getCount(),
                getTime() / 1000, getTime() % 1000,
                getDateString().substring(0, 10));
    }


    @Override
    public boolean load(int rank, Preferences prefs) {
        int cnt = prefs.getInt(getPrefName(rank + 1, DATA_COUNT), -1);
        long tm = prefs.getLong(getPrefName(rank + 1, DATA_TIME), -1);
        String strDate = prefs.get(getPrefName(rank + 1, DATA_DATE), "");

        if (cnt == -1 || tm == -1 || strDate.isEmpty()) {
            return false;
        }

        try {
            setCount(cnt);
            setTime(tm);
            setDateString(strDate);
        } catch (ParseException ex) {
            return false;
        }

        return true;
    }

    @Override
    public void save(int rank, Preferences prefs) {
        prefs.putLong(getPrefName(rank + 1, DATA_COUNT), getCount());
        prefs.putLong(getPrefName(rank + 1, DATA_TIME), getTime());
        prefs.put(getPrefName(rank + 1, DATA_DATE), getDateString());
    }

    @Override
    public void erase(int rank, Preferences prefs) {
        prefs.putLong(getPrefName(rank + 1, DATA_COUNT), -1);
        prefs.putLong(getPrefName(rank + 1, DATA_TIME), -1);
        prefs.put(getPrefName(rank + 1, DATA_DATE), "");
    }

    public int getCount() {
        return count;
    }

    public void setCount(int c) {
        count = c;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long t) {
        time = t;
    }

    @Override
    public String toString() {
        return "count:" + count + ", time:" + time + ", date:" + getDateString();
    }
}
