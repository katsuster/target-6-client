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
    public static final long DEFAULT_TIMEOUT_MS = 30000;
    public static final int RANKING_TOP_NUM = 5;
    public static final int SPACE_TOP = (int)(FONT_SIZE_LARGEST * 1.2);
    public enum ScenarioState {
        INIT,
        WAIT,
        RUN,
        RESULT,
        RESTART,
        FINISH,
        CLOSE,
    }

    private CancelSubScenario cancelSub;
    private BTDeviceHandler handlerBT;
    private MouseHandler handlerMouse;
    private BTButtonHandler handlerBTButton;
    private Font fontTimer;
    private Font fontTimerResult;
    private Font fontLargest;
    private Font fontLarge;
    private Font fontDetail;
    private ScenarioState state = ScenarioState.INIT;
    private List<Sensor> sensors = new ArrayList<>();
    private long tTimeoutMills = DEFAULT_TIMEOUT_MS;
    private long tStart;
    private TextLine tlTime;
    private TextLine tlRank;
    private TextLine tlRankHead;
    private TextLine tlResult;
    private List<TextLine> results = new ArrayList<>();

    public CountUpScenario(ScenarioSwitcher sw) {
        super(sw);
        for (int i = 0; i < getNumberOfTargets(); i++) {
            sensors.add(new Sensor());
        }

        cancelSub = new CancelSubScenario(sw);
    }

    public CountUpScenario(ScenarioSwitcher sw, CountUpScenario cs) {
        this(sw);
        tTimeoutMills = cs.tTimeoutMills;
    }

    @Override
    public SCORE_TYPE getScoreType() {
        switch ((int)tTimeoutMills) {
        case 30000:
            return SCORE_TYPE.SCORE_COUNT_UP_30;
        case 20000:
            return SCORE_TYPE.SCORE_COUNT_UP_20;
        case 15000:
            return SCORE_TYPE.SCORE_COUNT_UP_15;
        default:
            printError("Unknown timeout " + tTimeoutMills, null);
            return SCORE_TYPE.SCORE_EMPTY;
        }
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

        Font fUI = getSwitcher().getSetting().getFontUI();
        Font fMono = getSwitcher().getSetting().getFontMono();
        fontTimer = fMono.deriveFont(Font.PLAIN, FONT_SIZE_TIMER);
        fontTimerResult = fMono.deriveFont(Font.PLAIN, FONT_SIZE_TIMER_RESULT);
        fontLargest = fUI.deriveFont(Font.PLAIN, FONT_SIZE_LARGEST);
        fontLarge = fUI.deriveFont(Font.PLAIN, FONT_SIZE_LARGE);
        fontDetail = fMono.deriveFont(Font.PLAIN, FONT_SIZE_DETAIL);

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
                0, SPACE_TOP + (int)(FONT_SIZE_DETAIL * 1.3),
                mainWnd.getWidth() / 2, (int)(FONT_SIZE_DETAIL * 1.3));
        tlRankHead.getContentBox().setMargin(
                FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4,
                FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4);
        tlRankHead.setVisible(false);

        tlResult = new TextLine();
        tlResult.setText(getScoreType().toDisplay());
        tlResult.setAlign(Drawable.H_ALIGN.RIGHT, Drawable.V_ALIGN.TOP);
        tlResult.setForeground(Color.DARK_GRAY);
        tlResult.setFont(fontLarge);
        tlResult.getContentBox().setBounds(mainWnd.getWidth() / 2, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlResult.getContentBox().setMargin(20, 20, 20, 20);
        tlResult.setVisible(true);

        cancelSub.activate();

        clearDrawable();
        addDrawable(bg);
        addDrawable(tlTime);
        addDrawable(tlRankHead);
        addDrawable(tlRank);
        addDrawable(tlResult);
    }

    @Override
    public void deactivate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        cancelSub.deactivate();

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
        case RESTART:
            drawFrameRestart(g2);
            break;
        case FINISH:
            drawFrameFinish(g2);
            break;
        case CLOSE:
            drawFrameClose(g2);
            break;
        }

        drawAllDrawable(g2);
        cancelSub.drawFrame(g2);
    }

    protected void drawFrameInit(Graphics2D g2) {
        boolean success;

        success = writeLine(DEV_SINGLE, CMD_CNTUP + " " + Long.valueOf(getTimeoutInMills()).toString());
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
        long nano = System.nanoTime() - tStart - ScenarioSwitcher.DELAY_BLE_NS;
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
            finishScenario();
        }
    }

    protected void drawFrameResult(Graphics2D g2) {
    }

    protected void drawFrameRestart(Graphics2D g2) {
    }

    protected void drawFrameFinish(Graphics2D g2) {
        getSwitcher().setNextScenario(new CountUpScenario(getSwitcher(), this));
    }

    protected void drawFrameClose(Graphics2D g2) {
        getSwitcher().setNextScenario(new SelectScenario(getSwitcher()));
    }

    public synchronized long getTimeoutInMills() {
        return tTimeoutMills;
    }

    public synchronized void setTimeoutInMills(long ms) {
        tTimeoutMills = ms;
    }

    public synchronized ScenarioState getState() {
        return state;
    }

    public synchronized void setState(ScenarioState s) {
        state = s;
    }

    public synchronized void finishScenario() {
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
                    SPACE_TOP + (int)((i + 1) * FONT_SIZE_DETAIL * 1.3),
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

        tlTime.setFont(fontTimerResult);
        tlTime.setText(String.format("%3d@%2d.%03d", cnt,
                last / 1000, last % 1000));

        //Ranking
        ScoreBoard scboard = new ScoreBoard(getScoreType());
        Score sc = new ScoreCntup(getScoreType(), cnt, last, new Date());
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
                    0, SPACE_TOP + (int)((i + 2) * FONT_SIZE_DETAIL * 1.3),
                    mainWnd.getWidth() / 2, (int)(FONT_SIZE_DETAIL * 1.3));
            tl.getContentBox().setMargin(
                    FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4,
                    FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4);

            results.add(tl);
            addDrawable(tl);
        }

        cancelSub.finishScenario();

        getSwitcher().setTargetFPS(3);
        setState(ScenarioState.RESULT);
    }

    public synchronized void tryToCancelScenario() {
        cancelSub.tryToCancelScenario();
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

        tlTime.setText("---.---");

        cancelSub.cancelScenario();

        getSwitcher().setTargetFPS(3);
        setState(ScenarioState.RESULT);
    }

    public synchronized void restartScenario() {
        for (TextLine tl : results) {
            tl.setVisible(false);
        }
        tlRank.setVisible(false);
        tlRankHead.setVisible(false);
        tlTime.setVisible(false);

        cancelSub.restartScenario();

        setState(ScenarioState.RESTART);
    }

    public synchronized void nextScenario() {
        setState(ScenarioState.FINISH);
    }

    public synchronized void closeScenario() {
        setState(ScenarioState.CLOSE);
    }

    public Sensor getSensor(int devid, int senid) {
        return sensors.get(senid);
    }

    public synchronized void resetTimeStart() {
        tStart = System.nanoTime();
    }

    public synchronized boolean isFinished() {
        boolean finish = true;

        for (int j = 0; j < getNumberOfTargets(); j++) {
            Sensor.SensorState ss = getSensor(DEV_SINGLE, j).getState();

            if (ss != Sensor.SensorState.HIT) {
                finish = false;
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
                scenario.restartScenario();
                break;
            case RESTART:
                scenario.nextScenario();
                break;
            }
        }

        @Override
        public void mouseRightClicked() {
            switch (scenario.getState()) {
            case RESULT:
            case RESTART:
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
            case RESTART:
                scenario.closeScenario();
                break;
            }
        }
    }
}
