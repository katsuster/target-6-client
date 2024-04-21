package net.katsuster.scenario;

import java.awt.*;
import java.util.List;
import java.util.*;

import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.TextLine;
import net.katsuster.draw.TimerAnimation;
import net.katsuster.ui.MainWindow;
import net.katsuster.ui.MouseAdapterEx;

public class RankingScenario extends AbstractScenario {
    public static final int RANKING_TOP_NUM = 20;
    public static final int INTERVAL_MSEC = 6000;

    private MouseHandler handlerMouse;
    private BTButtonHandler handlerBTButton;
    private Font fontLargest;
    private Font fontSmall;
    private Font fontDetail;
    private Font fontSmallest;
    private List<String> scenarioList = new ArrayList<>();
    private List<SCORE_TYPE> scoreTypeList = new ArrayList<>();
    private boolean flagErase = false;
    private boolean flagClose = false;
    private TextLine tlMsgScenario;
    private TextLine tlMsgClose;
    private TextLine tlMsgErase;
    private List<List<TextLine>> rankingTextList = new ArrayList<>();

    public RankingScenario(ScenarioSwitcher sw) {
        super(sw);
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().setTargetFPS(3);

        handlerMouse = new MouseHandler(this);
        mainWnd.addMouseListener(handlerMouse);
        handlerBTButton = new BTButtonHandler(this, handlerMouse);
        btIO.addBTDeviceListener(handlerBTButton);

        Font f = getSwitcher().getSetting().getFont();
        fontLargest = f.deriveFont(Font.PLAIN, FONT_SIZE_LARGEST);
        fontSmall = f.deriveFont(Font.PLAIN, FONT_SIZE_SMALL);
        fontDetail = f.deriveFont(Font.PLAIN, FONT_SIZE_DETAIL);
        fontSmallest = f.deriveFont(Font.PLAIN, FONT_SIZE_SMALLEST);

        GridBG bg = new GridBG();
        bg.setForeground(COLOR_BG_GRAY);
        bg.setGridSize(48, 48);
        bg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlMsgScenario = new TextLine();
        tlMsgScenario.setAlign(Drawable.H_ALIGN.LEFT, Drawable.V_ALIGN.TOP);
        tlMsgScenario.setForeground(Color.DARK_GRAY);
        tlMsgScenario.setFont(fontLargest);
        tlMsgScenario.getContentBox().setBounds(0, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlMsgScenario.getContentBox().setMargin(20, 20, 20, 20);

        tlMsgClose = new TextLine();
        tlMsgClose.setText("Press(Short): Back to Title");
        tlMsgClose.setForeground(COLOR_DARK_BLUE);
        tlMsgClose.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlMsgClose.setFont(fontSmall);
        tlMsgClose.getContentBox().setBounds(0, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlMsgClose.getContentBox().setMargin(20, 20, 20, 20);

        tlMsgErase = new TextLine();
        tlMsgErase.setText("Press(Long): Erase Ranking");
        tlMsgErase.setForeground(COLOR_DARK_BLUE);
        tlMsgErase.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlMsgErase.setFont(fontSmall);
        tlMsgErase.getContentBox().setBounds(mainWnd.getWidth() / 2, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlMsgErase.getContentBox().setMargin(20, 20, 20, 20);

        //Ranking
        for (int i = 0; i < scoreTypeList.size(); i++) {
            List<TextLine> listTl = createRankingText(i);
            rankingTextList.add(listTl);
        }
        showRankingText(0);

        TimerAnimation timerP = new TimerAnimation();
        timerP.schedule(new TaskRanking(this), INTERVAL_MSEC);

        clearDrawable();
        addDrawable(bg);
        addDrawable(tlMsgScenario);
        for (List<TextLine> listTl : rankingTextList) {
            for (Drawable d : listTl) {
                addDrawable(d);
            }
        }
        addDrawable(tlMsgClose);
        addDrawable(tlMsgErase);
        addDrawable(timerP);
    }

    @Override
    public void deactivate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        btIO.removeBTDeviceListener(handlerBTButton);
        mainWnd.removeMouseListener(handlerMouse);
    }

    @Override
    public void drawFrame(Graphics2D g2) {
        if (flagErase) {
            for (SCORE_TYPE st : scoreTypeList) {
                ScoreBoard scboard = new ScoreBoard(st);
                scboard.saveScores();
            }
            getSwitcher().setNextScenario(new SelectScenario(getSwitcher()));
        }

        if (flagClose) {
            getSwitcher().setNextScenario(new SelectScenario(getSwitcher()));
        }

        drawAllDrawable(g2);
    }

    protected List<TextLine> createRankingText(int ind) {
        List<TextLine> tlList = new ArrayList<>();
        SCORE_TYPE st = scoreTypeList.get(ind);
        MainWindow mainWnd = getSwitcher().getMainWindow();
        ScoreBoard scboard = new ScoreBoard(st);
        scboard.loadScores();

        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < RANKING_TOP_NUM / 2; i++) {
                int index = RANKING_TOP_NUM / 2 * j + i;
                Score s = null;
                try {
                    s = scboard.getScoreByRank(index + 1);
                } catch (IndexOutOfBoundsException ex) {
                    //do nothing
                }
                TextLine tl = new TextLine();
                if (s == null) {
                    tl.setText(String.format("%2d:-----", index + 1));
                } else {
                    tl.setText(String.format("%2d:%s",
                            index + 1, s.toRankingString()));
                }
                tl.setAlign(Drawable.H_ALIGN.LEFT, Drawable.V_ALIGN.TOP);
                tl.setForeground(Color.DARK_GRAY);
                tl.setFont(fontDetail);
                tl.getContentBox().setBounds(
                        mainWnd.getWidth() / 2 * j,
                        FONT_SIZE_LARGEST + (int)((i + 1) * FONT_SIZE_DETAIL * 1.3),
                        mainWnd.getWidth() / 2, (int)(FONT_SIZE_DETAIL * 1.3));
                tl.getContentBox().setMargin(
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4,
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4);
                tl.setVisible(false);

                tlList.add(tl);
            }
        }

        return tlList;
    }

    public synchronized void showRankingText(int ind) {
        String scr = scenarioList.get(ind);
        List<TextLine> listTl = rankingTextList.get(ind);

        tlMsgScenario.setText(scr);
        for (TextLine tl : listTl) {
            tl.setVisible(true);
        }
    }

    public synchronized void hideRankingText(int ind) {
        List<TextLine> listTl = rankingTextList.get(ind);

        tlMsgScenario.setText("");
        for (TextLine tl : listTl) {
            tl.setVisible(false);
        }
    }

    public synchronized void setScenarios(List<String> l) {
        List<SCORE_TYPE> ll = new ArrayList<>();

        for (String scr : l) {
            SCORE_TYPE st = SelectScenario.getScoreType(scr);
            ll.add(st);
        }

        scenarioList = l;
        scoreTypeList = ll;
    }

    public synchronized void eraseRanking() {
        flagErase = true;
    }

    public synchronized void closeScenario() {
        flagClose = true;
    }

    private class TaskRanking implements Runnable {
        private RankingScenario scenario;
        private int cnt = 0;

        public TaskRanking(RankingScenario s) {
            scenario = s;
        }

        @Override
        public void run() {
            scenario.hideRankingText(cnt);

            cnt++;
            if (cnt >= scenarioList.size()) {
                cnt = 0;
            }

            scenario.showRankingText(cnt);
        }
    }

    protected class MouseHandler extends MouseAdapterEx {
        private RankingScenario scenario;

        public MouseHandler(RankingScenario s) {
            scenario = s;
        }

        @Override
        public void mouseLeftClicked() {
            scenario.closeScenario();
        }

        @Override
        public void mouseLeftLongPressed() {
            scenario.eraseRanking();
        }
    }
}
