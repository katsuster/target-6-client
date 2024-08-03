package net.katsuster.test;

import java.util.Date;

import org.junit.*;

import net.katsuster.scenario.Scenario.SCORE_TYPE;
import net.katsuster.scenario.Score;
import net.katsuster.scenario.ScoreBoard;
import net.katsuster.scenario.ScoreTest;

public class ScoreBoardTest {
    public static final SCORE_TYPE TEST_SCORE_TYPE = SCORE_TYPE.SCORE_TIME_ATTACK_TEST;

    public void setupTestItems(ScoreBoard sb) throws Exception {
        Score s1 = new ScoreTest(TEST_SCORE_TYPE, 100, new Date());
        Score s2 = new ScoreTest(TEST_SCORE_TYPE, 200, new Date());
        Score s3_1 = new ScoreTest(TEST_SCORE_TYPE, 300, new Date());
        Score s3_2 = new ScoreTest(TEST_SCORE_TYPE, 300, new Date());

        sb.addScore(s2);
        sb.addScore(s1);
        sb.addScore(s3_1);
        sb.addScore(s3_2);
    }

    @org.junit.Test
    public void testGetRank() throws Exception {
        ScoreBoard sb = new ScoreBoard(TEST_SCORE_TYPE);
        Score s1 = new ScoreTest(TEST_SCORE_TYPE, 100, new Date());
        Score s2 = new ScoreTest(TEST_SCORE_TYPE, 200, new Date());
        Score s3_1 = new ScoreTest(TEST_SCORE_TYPE, 300, new Date());
        Score s3_2 = new ScoreTest(TEST_SCORE_TYPE, 300, new Date());

        sb.addScore(s2);
        sb.addScore(s1);
        sb.addScore(s3_1);
        sb.addScore(s3_2);

        sb.getScoreByRank(1);
    }

    @org.junit.Test
    public void testOverflow() throws Exception {
        ScoreBoard sb = new ScoreBoard(TEST_SCORE_TYPE);
        Score s1 = new ScoreTest(TEST_SCORE_TYPE, 100, new Date());
        Score s2 = new ScoreTest(TEST_SCORE_TYPE, 200, new Date());
        Score s3 = new ScoreTest(TEST_SCORE_TYPE, 300, new Date());

        for (int i = 0; i < ScoreBoard.MAX_RECORDS - 2; i++) {
            sb.addScore(s2);
        }

        ScoreTest sl1_1 = (ScoreTest)sb.getScoreByRank(1);
        ScoreTest sl1_2 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 2);
        Assert.assertEquals("Failed to add.", 200, sl1_1.getTime());
        Assert.assertEquals("Failed to add.", 200, sl1_2.getTime());
        Assert.assertEquals("Failed to add.", ScoreBoard.MAX_RECORDS - 2, sb.getMaxRank());

        //Add to bottom
        sb.addScore(s3);
        ScoreTest sb1_1 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 2);
        ScoreTest sb1_2 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 1);
        Assert.assertEquals("Failed to add on bottom.", 200, sb1_1.getTime());
        Assert.assertEquals("Failed to add on bottom.", 300, sb1_2.getTime());
        Assert.assertEquals("Failed to add on bottom.", ScoreBoard.MAX_RECORDS - 1, sb.getMaxRank());

        //Add to bottom (2nd)
        sb.addScore(s3);
        ScoreTest sb2_1 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 2);
        ScoreTest sb2_2 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 1);
        ScoreTest sb2_3 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS);
        Assert.assertEquals("Failed to add on bottom (2nd).", 200, sb2_1.getTime());
        Assert.assertEquals("Failed to add on bottom (2nd).", 300, sb2_2.getTime());
        Assert.assertEquals("Failed to add on bottom (2nd).", 300, sb2_3.getTime());
        Assert.assertEquals("Failed to add on bottom (2nd).", ScoreBoard.MAX_RECORDS, sb.getMaxRank());

        //Add to bottom (3rd)
        sb.addScore(s3);
        ScoreTest sb3_1 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 2);
        ScoreTest sb3_2 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 1);
        ScoreTest sb3_3 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS);
        Assert.assertEquals("Failed to add on bottom (3rd).", 200, sb3_1.getTime());
        Assert.assertEquals("Failed to add on bottom (3rd).", 300, sb3_2.getTime());
        Assert.assertEquals("Failed to add on bottom (3rd).", 300, sb3_3.getTime());
        Assert.assertEquals("Failed to add on bottom (3rd).", ScoreBoard.MAX_RECORDS + 1, sb.getMaxRank());

        //Add to top
        sb.addScore(s1);
        ScoreTest st1_1 = (ScoreTest)sb.getScoreByRank(1);
        ScoreTest st1_2 = (ScoreTest)sb.getScoreByRank(2);
        ScoreTest st1_3 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS);
        Assert.assertEquals("Failed to add on top.", 100, st1_1.getTime());
        Assert.assertEquals("Failed to add on top.", 200, st1_2.getTime());
        Assert.assertEquals("Failed to add on top.", 300, st1_3.getTime());
        Assert.assertEquals("Failed to add on top.", ScoreBoard.MAX_RECORDS + 2, sb.getMaxRank());

        //Add to top (2nd)
        sb.addScore(s1);
        ScoreTest st2_1 = (ScoreTest)sb.getScoreByRank(1);
        ScoreTest st2_2 = (ScoreTest)sb.getScoreByRank(2);
        ScoreTest st2_3 = (ScoreTest)sb.getScoreByRank(3);
        ScoreTest st2_4 = (ScoreTest)sb.getScoreByRank(ScoreBoard.MAX_RECORDS);
        Assert.assertEquals("Failed to add on top (2nd).", 100, st2_1.getTime());
        Assert.assertEquals("Failed to add on top (2nd).", 100, st2_2.getTime());
        Assert.assertEquals("Failed to add on top (2nd).", 200, st2_3.getTime());
        Assert.assertEquals("Failed to add on top (2nd).", 200, st2_4.getTime());
        Assert.assertEquals("Failed to add on top (2nd).", ScoreBoard.MAX_RECORDS + 3, sb.getMaxRank());

        //Save and Load
        sb.saveScores();
        sb.loadScores();
        Assert.assertEquals("Failed to drop above max records.", ScoreBoard.MAX_RECORDS, sb.getMaxRank());
    }

    @org.junit.Test
    public void testBoardName() throws Exception {
        ScoreBoard sb = new ScoreBoard(TEST_SCORE_TYPE);
        Assert.assertEquals("BoardName is wrong.", TEST_SCORE_TYPE, sb.getScoreType());

        sb.setScoreType(SCORE_TYPE.SCORE_EMPTY);
        Assert.assertNotEquals("BoardName is not changed.", TEST_SCORE_TYPE, sb.getScoreType());
        Assert.assertEquals("BoardName is wrong.", SCORE_TYPE.SCORE_EMPTY, sb.getScoreType());
    }

    @org.junit.Test
    public void testAdd() throws Exception {
        ScoreBoard sb = new ScoreBoard(TEST_SCORE_TYPE);
        Score s1 = new ScoreTest(TEST_SCORE_TYPE, 100, new Date());
        Score s2 = new ScoreTest(TEST_SCORE_TYPE, 200, new Date());
        Score s3_1 = new ScoreTest(TEST_SCORE_TYPE, 300, new Date());
        Score s3_2 = new ScoreTest(TEST_SCORE_TYPE, 300, new Date());

        //Add
        Assert.assertEquals("Empty before add.", 0, sb.getMaxRank());
        int r2 = sb.getRank(s2);
        sb.addScore(s2);
        Assert.assertEquals("Rank is not 1.", 1, r2);
        Assert.assertEquals("Failed to add 1.", 1, sb.getMaxRank());

        int r1 = sb.getRank(s1);
        sb.addScore(s1);
        Assert.assertEquals("Rank is not 1.", 1, r1);
        Assert.assertEquals("Failed to add 2.", 2, sb.getMaxRank());

        int r3_1 = sb.getRank(s3_1);
        sb.addScore(s3_1);
        Assert.assertEquals("Rank is not 3.", 3, r3_1);
        Assert.assertEquals("Failed to add 3.", 3, sb.getMaxRank());

        int r3_2 = sb.getRank(s3_2);
        sb.addScore(s3_2);
        Assert.assertEquals("Rank is not 4.", 4, r3_2);
        Assert.assertEquals("Failed to add 4.", 4, sb.getMaxRank());

        //Save/Load
        sb.saveScores();
        sb = new ScoreBoard(TEST_SCORE_TYPE);
        sb.loadScores();

        Assert.assertEquals("Number of items is not 4 after load.", 4, sb.getMaxRank());
    }

    @org.junit.Test
    public void testClear() throws Exception {
        ScoreBoard sb = new ScoreBoard(TEST_SCORE_TYPE);
        setupTestItems(sb);
        sb.saveScores();
        sb.loadScores();
        Assert.assertNotEquals("Empty before clear.", 0, sb.getMaxRank());

        sb = new ScoreBoard(TEST_SCORE_TYPE);
        sb.saveScores();
        sb.loadScores();
        Assert.assertEquals("Not empty after clear.", 0, sb.getMaxRank());
    }
}
