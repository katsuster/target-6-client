package net.katsuster.scenario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

import net.katsuster.scenario.Scenario.SCORE_TYPE;

public class Score {
    public static final String DATA_DATE = "Date";
    public static final String FORMAT_DATE = "yyyy/MM/dd HH:mm:ss Z";

    private SCORE_TYPE scoreType = SCORE_TYPE.SCORE_EMPTY;
    private Date date;

    public Score() {
        setDate(new Date());
    }

    public Score(Date d) {
        setDate(d);
    }

    public Score(String str) throws ParseException {
        setDateString(str);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Score)) {
            return false;
        }

        Score obj = (Score)o;
        return date.equals(obj.date);
    }

    public boolean lessThanEqual(Score obj) {
        return false;
    }

    public String toRankingString() {
        return "";
    }

    public boolean load(int rank, Preferences prefs) {
        return false;
    }

    public void save(int rank, Preferences prefs) {
        //do nothing
    }

    public void erase(int rank, Preferences prefs) {
        //do nothing
    }

    protected String getPrefName(int rank, String datatype) {
        return getScoreType() + ":" + "Rank" + rank + ":" + datatype;
    }

    public SCORE_TYPE getScoreType() {
        return scoreType;
    }

    protected void setScoreType(SCORE_TYPE t) {
        scoreType = t;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date d) {
        date = d;
    }

    public String getDateString() {
        SimpleDateFormat sf = new SimpleDateFormat(FORMAT_DATE);

        return sf.format(date);
    }

    public void setDateString(String str) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(FORMAT_DATE);

        date = sf.parse(str);
    }

    @Override
    public String toString() {
        return "date:" + getDateString();
    }
}
