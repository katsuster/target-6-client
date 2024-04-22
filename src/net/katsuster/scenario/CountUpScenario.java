package net.katsuster.scenario;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;
import net.katsuster.ui.MouseAdapterEx;

public class CountUpScenario extends AbstractScenario {
    public static final int RANKING_TOP_NUM = 5;
    public enum ScenarioState {
        INIT,
        WAIT,
        RUN,
        RESULT,
        FINISH,
        CLOSE,
    }

    private BTDeviceHandler handlerBT;
    private MouseHandler handlerMouse;
    private BTButtonHandler handlerBTButton;
    private Font fontTimer;
    private Font fontLargest;
    private Font fontMedium;
    private Font fontSmall;
    private Font fontDetail;
    private ScenarioState state = ScenarioState.INIT;
    private List<Sensor> sensors = new ArrayList<>();
    private long tStart;
    private TextLine tlTime;
    private TextLine tlMsgCancel;
    private TextLine tlMsgNext;
    private TextLine tlMsgClose;
    private TextLine tlRank;
    private TextLine tlRankHead;
    private TextLine tlResult;
    private List<TextLine> ranking = new ArrayList<>();
    private List<TextLine> results = new ArrayList<>();

    public CountUpScenario(ScenarioSwitcher sw) {
        super(sw);
        for (int i = 0; i < getNumAllSensors(); i++) {
            sensors.add(new Sensor());
        }
    }

    @Override
    public SCORE_TYPE getScoreType() {
        return SCORE_TYPE.SCORE_COUNT_UP;
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().setTargetFPS(60);

        handlerMouse = new MouseHandler(this);
        mainWnd.addMouseListener(handlerMouse);
        handlerBT = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handlerBT);
        handlerBTButton = new BTButtonHandler(this, handlerMouse);
        btIO.addBTDeviceListener(handlerBTButton);

        Font f = getSwitcher().getSetting().getFont();
        fontTimer = f.deriveFont(Font.PLAIN, FONT_SIZE_TIMER);
        fontLargest = f.deriveFont(Font.PLAIN, FONT_SIZE_LARGEST);
        fontMedium = f.deriveFont(Font.PLAIN, FONT_SIZE_MEDIUM);
        fontSmall = f.deriveFont(Font.PLAIN, FONT_SIZE_SMALL);
        fontDetail = f.deriveFont(Font.PLAIN, FONT_SIZE_DETAIL);

        GridBG bg = new GridBG();
        bg.setForeground(COLOR_BG_GRAY);
        bg.setGridSize(48, 48);
        bg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlTime = new TextLine();
        tlTime.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.CENTER);
        tlTime.setForeground(Color.DARK_GRAY);
        tlTime.setFont(fontTimer);
        tlTime.getContentBox().setBounds(0, FONT_SIZE_TIMER / 2 + FONT_SIZE_SMALL * 2,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlMsgCancel = new TextLine();
        tlMsgCancel.setText("Press(Long): Cancel Game");
        tlMsgCancel.setForeground(COLOR_DARK_ORANGE);
        tlMsgCancel.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlMsgCancel.setFont(fontMedium);
        tlMsgCancel.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlMsgCancel.getContentBox().setMargin(20, 20, 20, 20);
        tlMsgCancel.setVisible(false);

        tlMsgNext = new TextLine();
        tlMsgNext.setText("Press(Short): Next Game");
        tlMsgNext.setForeground(COLOR_DARK_BLUE);
        tlMsgNext.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlMsgNext.setFont(fontSmall);
        tlMsgNext.getContentBox().setBounds(0, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlMsgNext.getContentBox().setMargin(20, 20, 20, 20);
        tlMsgNext.setVisible(false);

        tlMsgClose = new TextLine();
        tlMsgClose.setText("Press(Long): Back to Title");
        tlMsgClose.setForeground(COLOR_DARK_BLUE);
        tlMsgClose.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlMsgClose.setFont(fontSmall);
        tlMsgClose.getContentBox().setBounds(mainWnd.getWidth() / 2, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlMsgClose.getContentBox().setMargin(20, 20, 20, 20);
        tlMsgClose.setVisible(false);

        tlRank = new TextLine();
        tlRank.setAlign(Drawable.H_ALIGN.LEFT, Drawable.V_ALIGN.TOP);
        tlRank.setForeground(Color.DARK_GRAY);
        tlRank.setFont(fontLargest);
        tlRank.getContentBox().setBounds(0, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlRank.getContentBox().setMargin(20, 20, 20, 20);
        tlRank.setVisible(false);

        tlRankHead = new TextLine();
        tlRankHead.setText("TOP " + RANKING_TOP_NUM);
        tlRankHead.setAlign(Drawable.H_ALIGN.LEFT, Drawable.V_ALIGN.TOP);
        tlRankHead.setForeground(Color.DARK_GRAY);
        tlRankHead.setFont(fontDetail);
        tlRankHead.getContentBox().setBounds(
                0, FONT_SIZE_LARGEST + (int)(FONT_SIZE_DETAIL * 1.3),
                mainWnd.getWidth() / 2, (int)(FONT_SIZE_DETAIL * 1.3));
        tlRankHead.getContentBox().setMargin(
                FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4,
                FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4);
        tlRankHead.setVisible(false);

        tlResult = new TextLine();
        tlResult.setAlign(Drawable.H_ALIGN.RIGHT, Drawable.V_ALIGN.TOP);
        tlResult.setFont(fontLargest);
        tlResult.getContentBox().setBounds(mainWnd.getWidth() / 2, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlResult.getContentBox().setMargin(20, 20, 20, 20);
        tlResult.setVisible(false);

        clearDrawable();
        addDrawable(bg);
        addDrawable(tlTime);
        addDrawable(tlMsgCancel);
        addDrawable(tlMsgNext);
        addDrawable(tlMsgClose);
        addDrawable(tlRankHead);
        addDrawable(tlRank);
        addDrawable(tlResult);
    }

    @Override
    public void deactivate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        btIO.removeBTDeviceListener(handlerBTButton);
        btIO.removeBTDeviceListener(handlerBT);
        mainWnd.removeMouseListener(handlerMouse);
    }

    @Override
    public void drawFrame(Graphics2D g2) {
        switch (getState()) {
        case INIT:
            drawFrameInit(g2);
            break;
        case WAIT:
            drawFrameWait(g2);
            break;
        case RUN:
            drawFrameRun(g2);
            break;
        case RESULT:
            drawFrameResult(g2);
            break;
        case FINISH:
            drawFrameFinish(g2);
            break;
        case CLOSE:
            drawFrameClose(g2);
            break;
        }

        drawAllDrawable(g2);
    }

    protected void drawFrameInit(Graphics2D g2) {
        boolean success;

        success = writeLine(DEV_SINGLE, CMD_CNTUP);
        if (!success) {
            return;
        }
        success = writeLine(DEV_CONTROLLER, CMD_SINGLE);
        if (!success) {
            return;
        }

        resetTimeStart();
        setState(ScenarioState.WAIT);
    }

    protected void drawFrameWait(Graphics2D g2) {
        long nano = System.nanoTime() - tStart;
        long sec = 3 - (nano / ScenarioSwitcher.NS_1SEC);
        String curTime = String.format("%3d", sec);
        tlTime.setText(curTime);

        if (nano > 3 * ScenarioSwitcher.NS_1SEC) {
            resetTimeStart();
            setState(ScenarioState.RUN);
        }
    }

    protected void drawFrameRun(Graphics2D g2) {
        long nano = System.nanoTime() - tStart;
        long sec = nano / ScenarioSwitcher.NS_1SEC;
        long mil = (nano / ScenarioSwitcher.NS_1MSEC) % 1000;
        String curTime = String.format("%3d.%03d", sec, mil);
        tlTime.setText(curTime);

        if (isFinished()) {
            sensors.sort((x, y) -> {
                return (int)(x.getTimeHit() - y.getTimeHit());
            });

            int cnt = 0;
            long last = 0;
            for (int i = 0; i < sensors.size(); i++) {
                MainWindow mainWnd = getSwitcher().getMainWindow();
                Sensor sen = sensors.get(i);
                TextLine tl = new TextLine();
                tl.setText(String.format("T%d:%3d",
                        i + 1, sen.getCountHit()));
                tl.setAlign(Drawable.H_ALIGN.RIGHT, Drawable.V_ALIGN.TOP);
                tl.setForeground(Color.DARK_GRAY);
                tl.setFont(fontDetail);
                tl.getContentBox().setBounds(
                        mainWnd.getWidth() / 2,
                        FONT_SIZE_LARGEST + (int)((i + 1) * FONT_SIZE_DETAIL * 1.3),
                        mainWnd.getWidth() / 2,
                        (int)(FONT_SIZE_DETAIL * 1.3));
                tl.getContentBox().setMargin(
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4,
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4);

                results.add(tl);
                addDrawable(tl);

                cnt += sen.getCountHit();
                last = sen.getTimeHit();
            }

            boolean success = writeLine(DEV_CONTROLLER, CMD_BEEP);
            if (!success) {
                return;
            }

            tlTime.setText(String.format("%3d", cnt));

            //Ranking
            ScoreBoard scboard = new ScoreBoard(getScoreType());
            Score sc = new ScoreCntup(cnt, last, new Date());
            int rank;
            scboard.loadScores();
            rank = scboard.getRank(sc);
            scboard.addScore(sc);
            scboard.saveScores();

            if (rank <= ScoreBoard.MAX_RECORDS) {
                tlRank.setText("Rank " + rank);
            } else {
                tlRank.setText("Rank --");
            }
            tlRank.setVisible(true);

            tlRankHead.setVisible(true);
            for (int i = 0; i < RANKING_TOP_NUM; i++) {
                MainWindow mainWnd = getSwitcher().getMainWindow();
                Score s;
                try {
                    s = scboard.getScoreByRank(i + 1);
                } catch (IndexOutOfBoundsException ex) {
                    break;
                }
                TextLine tl = new TextLine();
                tl.setText(String.format("%d:%s",
                        i + 1, s.toRankingString()));
                tl.setAlign(Drawable.H_ALIGN.LEFT, Drawable.V_ALIGN.TOP);
                if (i + 1 == rank) {
                    tl.setForeground(Scenario.COLOR_DARK_ORANGE);
                } else {
                    tl.setForeground(Color.DARK_GRAY);
                }
                tl.setFont(fontDetail);
                tl.getContentBox().setBounds(
                        0, FONT_SIZE_LARGEST + (int)((i + 2) * FONT_SIZE_DETAIL * 1.3),
                        mainWnd.getWidth() / 2, (int)(FONT_SIZE_DETAIL * 1.3));
                tl.getContentBox().setMargin(
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4,
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4);

                results.add(tl);
                addDrawable(tl);
            }

            //Other messages
            tlResult.setText("Result");
            tlResult.setForeground(Color.DARK_GRAY);
            tlResult.setVisible(true);

            tlMsgCancel.setVisible(false);
            tlMsgNext.setVisible(true);
            tlMsgClose.setVisible(true);

            getSwitcher().setTargetFPS(3);
            setState(ScenarioState.RESULT);
        }
    }

    protected void drawFrameResult(Graphics2D g2) {
    }

    protected void drawFrameFinish(Graphics2D g2) {
        getSwitcher().setNextScenario(new CountUpScenario(getSwitcher()));
    }

    protected void drawFrameClose(Graphics2D g2) {
        getSwitcher().setNextScenario(new SelectScenario(getSwitcher()));
    }

    public synchronized ScenarioState getState() {
        return state;
    }

    public synchronized void setState(ScenarioState s) {
        state = s;
    }

    public synchronized void tryToCancelScenario() {
        tlMsgCancel.setVisible(true);
    }

    public synchronized void cancelScenario() {
        boolean success;

        success = writeInitCommand(DEV_SINGLE);
        if (!success) {
            return;
        }
        success = writeInitCommand(DEV_CONTROLLER);
        if (!success) {
            return;
        }

        tlResult.setText("Canceled");
        tlResult.setForeground(COLOR_DARK_ORANGE);
        tlResult.setVisible(true);

        tlMsgCancel.setVisible(false);
        tlMsgNext.setVisible(true);
        tlMsgClose.setVisible(true);

        getSwitcher().setTargetFPS(3);
        setState(ScenarioState.RESULT);
    }

    public synchronized void nextScenario() {
        setState(ScenarioState.FINISH);
    }

    public synchronized void closeScenario() {
        setState(ScenarioState.CLOSE);
    }

    protected int getLinearID(int devid, int senid) {
        int sensorID = 0;

        if (devid < 0 || BTInOut.NUM_DEVICES <= devid) {
            throw new IllegalArgumentException("Illegal device ID:" + devid + ".");
        }
        if (senid < 0 || getNumSensors(devid) <= senid) {
            throw new IllegalArgumentException("Illegal sensor ID:" + senid + ".");
        }

        for (int i = 0; i < devid; i++) {
            sensorID += getNumSensors(i);
        }

        return sensorID + senid;
    }

    public Sensor getSensor(int devid, int senid) {
        int sensorID = getLinearID(devid, senid);

        return sensors.get(sensorID);
    }

    public synchronized void resetTimeStart() {
        tStart = System.nanoTime();
    }

    public synchronized boolean isFinished() {
        boolean finish = true;

        for (int i = 0; i < BTInOut.NUM_DEVICES; i++) {
            for (int j = 0; j < getNumSensors(i); j++) {
                Sensor.SensorState ss = getSensor(i, j).getState();

                if (ss != Sensor.SensorState.HIT) {
                    finish = false;
                }
            }
        }

        return finish;
    }

    protected class BTDeviceHandler extends BTCommandHandler {
        private CountUpScenario scenario;

        public BTDeviceHandler(CountUpScenario s) {
            super(s);

            scenario = s;
        }

        @Override
        public void cmdInit(StringTokenizer st, int devid) {
            String next = st.nextToken();

            if (!next.equalsIgnoreCase("OK")) {
                scenario.printError(CMD_INIT + ": Command is failed.", null);
            }
        }

        @Override
        public void cmdSingle(StringTokenizer st, int devid) {
            String next = st.nextToken();

            if (!next.equalsIgnoreCase("OK")) {
                scenario.printError(CMD_SINGLE + ": Command is failed.", null);
            }
        }

        @Override
        public void cmdBeep(StringTokenizer st, int devid) {
            String next = st.nextToken();

            if (!next.equalsIgnoreCase("OK")) {
                scenario.printError(CMD_BEEP + ": Command is failed.", null);
            }
        }

        @Override
        public void cmdCntup(StringTokenizer st, int devid) {
            String next = st.nextToken();

            if (next.equalsIgnoreCase("OK")) {
                scenario.resetTimeStart();
            } else {
                scenario.printError(CMD_CNTUP + ": Command is failed.", null);
            }
        }

        @Override
        public void cmdHit(StringTokenizer st, int devid) throws ParseException {
            String next = st.nextToken();
            SimpleDateFormat sd = new SimpleDateFormat("mm:ss.SSS");
            sd.setTimeZone(TimeZone.getTimeZone("GMT"));
            int senid = parseID(next, PREFIX_SENSOR_ID);
            Date datePast = sd.parse(st.nextToken());
            int hit = parseID(st.nextToken(), PREFIX_HIT_COUNT);
            long msPast = datePast.getTime();
            Sensor sen = scenario.getSensor(devid, senid);

            scenario.printInfo(String.format("%s dev:%d sen:%d %3d.%03d hit:%d",
                    tlTime.getText(), devid, senid, msPast / 1000, msPast % 1000, hit), null);

            sen.setTimeHit(msPast);
            sen.setCountHit(hit);
            sen.setState(Sensor.SensorState.HIT);
        }
    }

    protected class MouseHandler extends MouseAdapterEx {
        private CountUpScenario scenario;

        public MouseHandler(CountUpScenario s) {
            scenario = s;
        }

        @Override
        public void mouseLeftClicked() {
            switch (scenario.getState()) {
            case RUN:
                scenario.tryToCancelScenario();
                break;
            case RESULT:
                scenario.nextScenario();
                break;
            }
        }

        @Override
        public void mouseRightClicked() {
            switch (scenario.getState()) {
            case RESULT:
                scenario.closeScenario();
                break;
            }
        }

        @Override
        public void mouseLeftLongPressed() {
            switch (scenario.getState()) {
            case RUN:
                scenario.cancelScenario();
                break;
            case RESULT:
                scenario.closeScenario();
                break;
            }
        }
    }
}
