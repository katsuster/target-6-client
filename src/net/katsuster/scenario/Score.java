package net.katsuster.scenario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Score {
    public static final String FORMAT_DATE = "yyyy/MM/dd HH:mm:ss Z";

    private long time;
    private Date date;

    public Score() {

    }

    public Score(long t) {
        setTime(t);
    }

    public Score(long t, Date d) {
        setTime(t);
        setDate(d);
    }

    public Score(long t, String str) throws ParseException {
        setTime(t);
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
        return time == obj.time && date.equals(obj.date);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long t) {
        time = t;
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
        return "time:" + time + ", date:" + getDateString();
    }
}
