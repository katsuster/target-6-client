package net.katsuster.scenario;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;
import net.katsuster.ui.MouseAdapterEx;

public class SingleScenario extends AbstractScenario {
    public static final int DEV_CONTROLLER = 0;
    public static final int DEV_SINGLE = 1;

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
    private Font fontTimer;
    private Font fontLarge;
    private Font fontMedium;
    private Font fontSmall;
    private Font fontSmallest;
    private ScenarioState state = ScenarioState.INIT;
    private List<Sensor> sensors = new ArrayList<>();
    private long tStart;
    private TextLine tlTime;
    private TextLine tlWarning;
    private TextLine tlResult;
    private TextLine tlClock;
    private List<TextLine> results = new ArrayList<>();

    public SingleScenario(ScenarioSwitcher sw) {
        super(sw);
        for (int i = 0; i < getNumOfSensors(); i++) {
            sensors.add(new Sensor());
        }
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().setTargetFPS(60);

        handlerBT = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handlerBT);
        handlerMouse = new MouseHandler(this);
        mainWnd.addMouseListener(handlerMouse);

        Font f = getSwitcher().getSetting().getFont();
        fontTimer = f.deriveFont(Font.PLAIN, FONT_SIZE_TIMER);
        fontLarge = f.deriveFont(Font.PLAIN, FONT_SIZE_LARGEST);
        fontMedium = f.deriveFont(Font.PLAIN, FONT_SIZE_LARGE);
        fontSmall = f.deriveFont(Font.PLAIN, FONT_SIZE_SMALL);
        fontSmallest = f.deriveFont(Font.PLAIN, FONT_SIZE_SMALLEST);

        GridBG bg = new GridBG();
        bg.setForeground(COLOR_BG_GRAY);
        bg.setGridSize(48, 48);
        bg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlTime = new TextLine();
        tlTime.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.CENTER);
        tlTime.setForeground(Color.DARK_GRAY);
        tlTime.setFont(fontTimer);
        tlTime.getContentBox().setBounds(0, FONT_SIZE_TIMER / 2,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlWarning = new TextLine();
        tlWarning.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlWarning.setFont(fontSmall);
        tlWarning.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight() - FONT_SIZE_SMALL * 2);
        tlWarning.getContentBox().setMargin(20, 20, 20, 20);
        tlWarning.setVisible(false);

        tlResult = new TextLine();
        tlResult.setAlign(Drawable.H_ALIGN.RIGHT, Drawable.V_ALIGN.TOP);
        tlResult.setFont(fontMedium);
        tlResult.getContentBox().setBounds(0, FONT_SIZE_MEDIUM,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlResult.getContentBox().setMargin(20, 0, 20, 0);
        tlResult.setVisible(false);

        tlClock = new TextLine();
        tlClock.setAlign(Drawable.H_ALIGN.LEFT, Drawable.V_ALIGN.BOTTOM);
        tlClock.setForeground(Color.DARK_GRAY);
        tlClock.setFont(fontSmallest);
        tlClock.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlClock.getContentBox().setMargin(5, 0, FONT_SIZE_SMALL, 5);
        tlClock.setVisible(false);

        clearDrawable();
        addDrawable(bg);
        addDrawable(tlClock);
        addDrawable(tlTime);
        addDrawable(tlWarning);
        addDrawable(tlResult);
    }

    @Override
    public void deactivate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        btIO.removeBTDeviceListener(handlerBT);
        mainWnd.removeMouseListener(handlerMouse);
    }

    @Override
    public void drawFrame(Graphics2D g2) {
        try {
            drawFrameInner(g2);
        } catch (IOException ex) {
            //do nothing
        }

        drawAllDrawable(g2);
    }

    protected void drawFrameInner(Graphics2D g2) throws IOException {
        switch (getState()) {
        case INIT:
            drawFrameInnerInit(g2);
            break;
        case WAIT:
            drawFrameInnerWait(g2);
            break;
        case RUN:
            drawFrameInnerRun(g2);
            break;
        case RESULT:
            drawFrameInnerResult(g2);
            break;
        case FINISH:
            drawFrameInnerFinish(g2);
            break;
        case CLOSE:
            drawFrameInnerClose(g2);
            break;
        }
    }

    protected void drawFrameInnerInit(Graphics2D g2) {
        boolean success;

        success = writeLine(DEV_SINGLE, CMD_TATK);
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

    protected void drawFrameInnerWait(Graphics2D g2) {
        long nano = System.nanoTime() - tStart;
        long sec = 3 - (nano / ScenarioSwitcher.NS_1SEC);
        String curTime = String.format("%3d", sec);
        tlTime.setText(curTime);

        if (nano > 3 * ScenarioSwitcher.NS_1SEC) {
            resetTimeStart();
            setState(ScenarioState.RUN);
        }
    }

    protected void drawFrameInnerRun(Graphics2D g2) {
        long nano = System.nanoTime() - tStart;
        long sec = nano / ScenarioSwitcher.NS_1SEC;
        long mil = (nano / ScenarioSwitcher.NS_1MSEC) % 1000;
        String curTime = String.format("%3d.%03d", sec, mil);
        tlTime.setText(curTime);

        if (isFinished()) {
            sensors.sort((x, y) -> {
                return (int)(x.getTimeHit() - y.getTimeHit());
            });

            long before = 0;
            for (int i = 0; i < sensors.size(); i++) {
                Sensor sen = sensors.get(i);
                int diff = (int)(sen.getTimeHit() - before);
                TextLine tl = new TextLine();
                tl.setText(String.format("Target%2d: %3d.%03d (+%d.%03d)",
                        i + 1,
                        sen.getTimeHit() / 1000, sen.getTimeHit() % 1000,
                        diff / 1000, diff % 1000));
                tl.setForeground(Color.DARK_GRAY);
                tl.setFont(fontSmall);
                tl.getContentBox().setBounds(
                        0, (int)((i + 1) * FONT_SIZE_SMALL * 1.3),
                        100, (int)(FONT_SIZE_SMALL * 1.3));
                tl.getContentBox().setMargin(
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4,
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4);

                results.add(tl);
                addDrawable(tl);

                before = sen.getTimeHit();
            }

            boolean success = writeLine(DEV_CONTROLLER, CMD_BEEP);
            if (!success) {
                return;
            }

            tlTime.setText(String.format("%3d.%03d",
                    before / 1000, before % 1000));

            tlResult.setText("Result");
            tlResult.setForeground(Color.DARK_GRAY);
            tlResult.setVisible(true);

            tlWarning.setText("Press a button to next");
            tlWarning.setForeground(Color.DARK_GRAY);
            tlWarning.setVisible(true);

            tlClock.setVisible(true);

            getSwitcher().setTargetFPS(3);
            setState(ScenarioState.RESULT);
        }
    }

    protected void drawFrameInnerResult(Graphics2D g2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        tlClock.setText(df.format(new Date()));
    }

    protected void drawFrameInnerFinish(Graphics2D g2) {
        getSwitcher().setNextScenario(new SingleScenario(getSwitcher()));
    }

    protected void drawFrameInnerClose(Graphics2D g2) {
        getSwitcher().setNextScenario(new ClosingScenario(getSwitcher()));
    }

    public synchronized ScenarioState getState() {
        return state;
    }

    public synchronized void setState(ScenarioState s) {
        state = s;
    }

    public synchronized void tryToCancelScenario() {
        tlWarning.setText("Press a button 3 seconds to cancel");
        tlWarning.setForeground(COLOR_DARK_ORANGE);
        tlWarning.setVisible(true);
    }

    public synchronized void cancelScenario() {
        boolean success;

        success = writeLine(DEV_SINGLE, CMD_INIT + " " + DEV_SINGLE);
        if (!success) {
            return;
        }
        success = writeLine(DEV_CONTROLLER, CMD_INIT + " " + 0);
        if (!success) {
            return;
        }

        tlResult.setText("Canceled");
        tlResult.setForeground(COLOR_DARK_ORANGE);
        tlResult.setVisible(true);

        tlWarning.setText("Press a button to next");
        tlWarning.setForeground(Color.DARK_GRAY);
        tlWarning.setVisible(true);

        tlClock.setVisible(true);

        getSwitcher().setTargetFPS(3);
        setState(ScenarioState.RESULT);
    }

    public synchronized void nextScenario() {
        setState(ScenarioState.FINISH);
    }

    public synchronized void closeScenario() {
        setState(ScenarioState.CLOSE);
    }

    //TODO
    protected int getNumOfSensorsOfDev(int devid) {
        switch (devid) {
        case 0:
            return 0;
        case 1:
            return 6;
        case 2:
            return 0;
        default:
            return 0;
        }
    }

    //TODO
    protected int getNumOfSensors() {
        int n = 0;

        for (int i = 0; i < BTInOut.NUM_DEVICES; i++) {
            n += getNumOfSensorsOfDev(i);
        }

        return n;
    }

    protected int getLinearID(int devid, int senid) {
        int sensorID = 0;

        if (devid < 0 || BTInOut.NUM_DEVICES <= devid) {
            throw new IllegalArgumentException("Illegal device ID:" + devid + ".");
        }
        if (senid < 0 || getNumOfSensorsOfDev(devid) <= senid) {
            throw new IllegalArgumentException("Illegal sensor ID:" + senid + ".");
        }

        for (int i = 0; i < devid; i++) {
            sensorID += getNumOfSensorsOfDev(i);
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
            for (int j = 0; j < getNumOfSensorsOfDev(i); j++) {
                Sensor.SensorState ss = getSensor(i, j).getState();

                if (ss != Sensor.SensorState.HIT) {
                    finish = false;
                }
            }
        }

        return finish;
    }

    protected class BTDeviceHandler extends BTCommandHandler {
        private SingleScenario scenario;

        public BTDeviceHandler(SingleScenario s) {
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
        public void cmdSix(StringTokenizer st, int devid) {
            String next = st.nextToken();

            if (next.equalsIgnoreCase("OK")) {
                scenario.resetTimeStart();
            } else {
                scenario.printError(CMD_TATK + ": Command is failed.", null);
            }
        }

        @Override
        public void cmdButton(StringTokenizer st, int devid) {
            String next = st.nextToken();
            MainWindow wnd = scenario.getSwitcher().getMainWindow();

            if (next.equalsIgnoreCase("press")) {
                MouseEvent e = new MouseEvent(wnd, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                        0, 0, 0, 1, false, MouseEvent.BUTTON1);
                handlerMouse.mousePressed(e);
            } else if (next.equalsIgnoreCase("release")) {
                MouseEvent e = new MouseEvent(wnd, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(),
                        0, 0, 0, 1, false, MouseEvent.BUTTON1);
                handlerMouse.mouseReleased(e);
            } else {
                scenario.printError(RES_BUTTON + ": unknown event " + next + ".", null);
            }
        }

        @Override
        public void cmdHit(StringTokenizer st, int devid) throws ParseException {
            String next = st.nextToken();
            SimpleDateFormat sd = new SimpleDateFormat("mm:ss.SSS");
            sd.setTimeZone(TimeZone.getTimeZone("GMT"));
            int senid = parseID(next, PREFIX_SENSOR_ID);
            Date datePast = sd.parse(st.nextToken());
            long msPast = datePast.getTime();
            Sensor sen = scenario.getSensor(devid, senid);

            scenario.printInfo(String.format("%s dev:%d sen:%d %3d.%03d",
                    tlTime.getText(), devid, senid, msPast / 1000, msPast % 1000), null);

            sen.setTimeHit(msPast);
            sen.setState(Sensor.SensorState.HIT);
        }
    }

    protected class MouseHandler extends MouseAdapterEx {
        private SingleScenario scenario;

        public MouseHandler(SingleScenario s) {
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
            }
        }
    }
}
