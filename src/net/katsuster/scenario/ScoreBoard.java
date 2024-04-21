package net.katsuster.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import net.katsuster.scenario.Scenario.SCORE_TYPE;
import net.katsuster.ui.MainWindow;

public class ScoreBoard {
    public static final int MAX_RECORDS = 30;

    private SCORE_TYPE scoreType = SCORE_TYPE.SCORE_EMPTY;
    private Preferences prefs = Preferences.userNodeForPackage(MainWindow.class);
    private List<Score> scores = new ArrayList<>();

    public ScoreBoard() {

    }

    public ScoreBoard(SCORE_TYPE st) {
        scoreType = st;
    }

    public SCORE_TYPE getScoreType() {
        return scoreType;
    }

    public void setScoreType(SCORE_TYPE st) {
        scoreType = st;
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
                .filter(w -> w.lessThanEqual(sc))
                .count();

        if (rankL >= Integer.MAX_VALUE) {
            throw new IllegalStateException("Ranking is too big.");
        }

        int rank = (int)rankL;
        return rank + 1;
    }

    protected String getPrefName(int rank, String datatype) {
        return getScoreType() + ":" + "Rank" + rank + ":" + datatype;
    }

    public void loadScores() {
        scores.clear();

        for (int i = 0; i < MAX_RECORDS; i++) {
            Score s = ScoreFactory.createScore(getScoreType());

            boolean success = s.load(i + 1, prefs);
            if (!success) {
                continue;
            }

            scores.add(s);
        }
    }

    public void saveScores() {
        int num, i;

        num = Integer.min(scores.size(), MAX_RECORDS);
        for (i = 0; i < num; i++) {
            Score s = scores.get(i);

            s.save(i + 1, prefs);
        }

        for (; i < MAX_RECORDS; i++) {
            Score s = ScoreFactory.createScore(getScoreType());

            s.erase(i + 1, prefs);
        }
    }
}
