package net.katsuster.scenario;

import net.katsuster.ui.MainWindow;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class ScoreBoard {
    public static final int MAX_RECORDS = 30;
    public static final String DATA_TIME = "Time";
    public static final String DATA_DATE = "Date";

    private String boardName = "Global";
    private Preferences prefs = Preferences.userNodeForPackage(MainWindow.class);
    private List<Score> scores = new ArrayList<>();

    public ScoreBoard() {

    }

    public ScoreBoard(String n) {
        boardName = n;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String n) {
        boardName = n;
    }

    public Score getScoreByRank(int r) {
        return scores.get(r - 1);
    }

    public void addScore(Score sc) {
        int rank = getRank(sc);
        if (rank == 0) {
            throw new IllegalArgumentException("Cannot get valid rank.");
        }

        scores.add(rank - 1, sc);
    }

    public void removeScore(Score sc) {
        scores.remove(sc);
    }

    public int getMaxRank() {
        return scores.size();
    }

    public int getRank(Score sc) {
        long rankL = scores.stream()
                .filter(w -> w.getTime() <= sc.getTime())
                .count();

        if (rankL >= Integer.MAX_VALUE) {
            throw new IllegalStateException("Ranking is too big.");
        }

        int rank = (int)rankL;
        return rank + 1;
    }

    protected String getPrefName(int rank, String datatype) {
        return getBoardName() + ":" + "Rank" + rank + ":" + datatype;
    }

    public void loadScores() {
        scores.clear();

        for (int i = 0; i < MAX_RECORDS; i++) {
            long t = prefs.getLong(getPrefName(i + 1, DATA_TIME), -1);
            String strDate = prefs.get(getPrefName(i + 1, DATA_DATE), "");

            if (t == -1 || strDate.isEmpty()) {
                //Skip empty data
                continue;
            }

            try {
                Score s = new Score(t, strDate);
                scores.add(s);
            } catch (ParseException ex) {
                //Skip broken data
            }
        }
    }

    public void saveScores() {
        int num, i;

        num = Integer.min(scores.size(), MAX_RECORDS);
        for (i = 0; i < num; i++) {
            Score sc = scores.get(i);

            prefs.putLong(getPrefName(i + 1, DATA_TIME), sc.getTime());
            prefs.put(getPrefName(i + 1, DATA_DATE), sc.getDateString());
        }

        for (; i < MAX_RECORDS; i++) {
            prefs.putLong(getPrefName(i + 1, DATA_TIME), -1);
            prefs.put(getPrefName(i + 1, DATA_DATE), "");
        }
    }
}
