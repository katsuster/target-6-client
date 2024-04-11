package net.katsuster.test;

import java.util.Date;

import org.junit.*;

import net.katsuster.scenario.Score;
import net.katsuster.scenario.ScoreBoard;

public class ScoreBoardTest {
    public static final String SCENARIO_NAME = "TestScenario";

    public void setupTestItems(ScoreBoard sb) throws Exception {
        Score s1 = new Score(100, new Date());
        Score s2 = new Score(200, new Date());
        Score s3_1 = new Score(300, new Date());
        Score s3_2 = new Score(300, new Date());

        sb.addScore(s2);
        sb.addScore(s1);
        sb.addScore(s3_1);
        sb.addScore(s3_2);
    }

    @org.junit.Test
    public void testGetRank() throws Exception {
        ScoreBoard sb = new ScoreBoard(SCENARIO_NAME);
        Score s1 = new Score(100, new Date());
        Score s2 = new Score(200, new Date());
        Score s3_1 = new Score(300, new Date());
        Score s3_2 = new Score(300, new Date());

        sb.addScore(s2);
        sb.addScore(s1);
        sb.addScore(s3_1);
        sb.addScore(s3_2);

        sb.getScoreByRank(1);
    }

    @org.junit.Test
    public void testOverflow() throws Exception {
        ScoreBoard sb = new ScoreBoard(SCENARIO_NAME);
        Score s1 = new Score(100, new Date());
        Score s2 = new Score(200, new Date());
        Score s3 = new Score(300, new Date());

        for (int i = 0; i < ScoreBoard.MAX_RECORDS - 2; i++) {
            sb.addScore(s2);
        }

        Score sl1_1 = sb.getScoreByRank(1);
        Score sl1_2 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 2);
        Assert.assertEquals("Failed to add.", 200, sl1_1.getTime());
        Assert.assertEquals("Failed to add.", 200, sl1_2.getTime());
        Assert.assertEquals("Failed to add.", ScoreBoard.MAX_RECORDS - 2, sb.getMaxRank());

        //Add to bottom
        sb.addScore(s3);
        Score sb1_1 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 2);
        Score sb1_2 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 1);
        Assert.assertEquals("Failed to add on bottom.", 200, sb1_1.getTime());
        Assert.assertEquals("Failed to add on bottom.", 300, sb1_2.getTime());
        Assert.assertEquals("Failed to add on bottom.", ScoreBoard.MAX_RECORDS - 1, sb.getMaxRank());

        //Add to bottom (2nd)
        sb.addScore(s3);
        Score sb2_1 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 2);
        Score sb2_2 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 1);
        Score sb2_3 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS);
        Assert.assertEquals("Failed to add on bottom (2nd).", 200, sb2_1.getTime());
        Assert.assertEquals("Failed to add on bottom (2nd).", 300, sb2_2.getTime());
        Assert.assertEquals("Failed to add on bottom (2nd).", 300, sb2_3.getTime());
        Assert.assertEquals("Failed to add on bottom (2nd).", ScoreBoard.MAX_RECORDS, sb.getMaxRank());

        //Add to bottom (3rd)
        sb.addScore(s3);
        Score sb3_1 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 2);
        Score sb3_2 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS - 1);
        Score sb3_3 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS);
        Assert.assertEquals("Failed to add on bottom (3rd).", 200, sb3_1.getTime());
        Assert.assertEquals("Failed to add on bottom (3rd).", 300, sb3_2.getTime());
        Assert.assertEquals("Failed to add on bottom (3rd).", 300, sb3_3.getTime());
        Assert.assertEquals("Failed to add on bottom (3rd).", ScoreBoard.MAX_RECORDS + 1, sb.getMaxRank());

        //Add to top
        sb.addScore(s1);
        Score st1_1 = sb.getScoreByRank(1);
        Score st1_2 = sb.getScoreByRank(2);
        Score st1_3 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS);
        Assert.assertEquals("Failed to add on top.", 100, st1_1.getTime());
        Assert.assertEquals("Failed to add on top.", 200, st1_2.getTime());
        Assert.assertEquals("Failed to add on top.", 300, st1_3.getTime());
        Assert.assertEquals("Failed to add on top.", ScoreBoard.MAX_RECORDS + 2, sb.getMaxRank());

        //Add to top (2nd)
        sb.addScore(s1);
        Score st2_1 = sb.getScoreByRank(1);
        Score st2_2 = sb.getScoreByRank(2);
        Score st2_3 = sb.getScoreByRank(3);
        Score st2_4 = sb.getScoreByRank(ScoreBoard.MAX_RECORDS);
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
        ScoreBoard sb = new ScoreBoard(SCENARIO_NAME);
        Assert.assertEquals("BoardName is wrong.", SCENARIO_NAME, sb.getBoardName());

        sb.setBoardName("Test2");
        Assert.assertNotEquals("BoardName is not changed.", SCENARIO_NAME, sb.getBoardName());
        Assert.assertEquals("BoardName is wrong.", "Test2", sb.getBoardName());
    }

    @org.junit.Test
    public void testAdd() throws Exception {
        ScoreBoard sb = new ScoreBoard(SCENARIO_NAME);
        Score s1 = new Score(100, new Date());
        Score s2 = new Score(200, new Date());
        Score s3_1 = new Score(300, new Date());
        Score s3_2 = new Score(300, new Date());

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
        sb = new ScoreBoard(SCENARIO_NAME);
        sb.loadScores();

        Assert.assertEquals("Number of items is not 4 after load.", 4, sb.getMaxRank());
    }

    @org.junit.Test
    public void testClear() throws Exception {
        ScoreBoard sb = new ScoreBoard(SCENARIO_NAME);
        setupTestItems(sb);
        sb.saveScores();
        sb.loadScores();
        Assert.assertNotEquals("Empty before clear.", 0, sb.getMaxRank());

        sb = new ScoreBoard(SCENARIO_NAME);
        sb.saveScores();
        sb.loadScores();
        Assert.assertEquals("Not empty after clear.", 0, sb.getMaxRank());
    }
}
