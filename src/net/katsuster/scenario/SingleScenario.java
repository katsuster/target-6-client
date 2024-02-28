package net.katsuster.scenario;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import net.katsuster.ble.BTDeviceEvent;
import net.katsuster.ble.BTDeviceListener;
import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;

public class SingleScenario extends AbstractScenario {
    public static final int FONT_SIZE_LARGE = 120;
    public static final int FONT_SIZE_MEDIUM = 80;
    public static final int FONT_SIZE_SMALL = 24;
    public static final Color COLOR_DARK_ORANGE = new Color(247, 119, 15);

    public static final String CMD_SINGLE = "single";
    public static final String CMD_HIT = "hit";

    public static final String PREFIX_DEVICE_ID = "d";
    public static final String PREFIX_SENSOR_ID = "s";

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
    private Font fontLarge;
    private Font fontMedium;
    private Font fontSmall;
    private ScenarioState state = ScenarioState.INIT;
    private List<Sensor> sensors = new ArrayList<>();
    private long tStart;
    private TextLine tlTime;
    private TextLine tlWarning;
    private TextLine tlResult;
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
        fontLarge = f.deriveFont(Font.PLAIN, FONT_SIZE_LARGE);
        fontMedium = f.deriveFont(Font.PLAIN, FONT_SIZE_MEDIUM);
        fontSmall = f.deriveFont(Font.PLAIN, FONT_SIZE_SMALL);

        GridBG bg = new GridBG();
        bg.setForeground(new Color(240, 240, 240));
        bg.setGridSize(48, 48);
        bg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlTime = new TextLine();
        tlTime.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.CENTER);
        tlTime.setForeground(Color.DARK_GRAY);
        tlTime.setFont(fontLarge);
        tlTime.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlWarning = new TextLine();
        tlWarning.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlWarning.setFont(fontSmall);
        tlWarning.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight() - 100);
        tlWarning.getContentBox().setMargin(20, 20, 20, 20);
        tlWarning.setVisible(false);

        tlResult = new TextLine();
        tlResult.setAlign(Drawable.H_ALIGN.RIGHT, Drawable.V_ALIGN.TOP);
        tlResult.setFont(fontMedium);
        tlResult.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlResult.getContentBox().setMargin(20, 20, 20, 20);
        tlResult.setVisible(false);

        clearDrawable();
        addDrawable(bg);
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
        boolean success = writeLine(2, CMD_SINGLE + "\n");
        if (!success) {
            getSwitcher().termBTIO();
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
                        0, (int)((i + 3) * FONT_SIZE_SMALL * 1.3),
                        100, (int)(FONT_SIZE_SMALL * 1.3));
                tl.getContentBox().setMargin(
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4,
                        FONT_SIZE_SMALL, FONT_SIZE_SMALL / 4);

                results.add(tl);
                addDrawable(tl);

                before = sen.getTimeHit();
            }

            tlTime.setText(String.format("Total %3d.%03d",
                    before / 1000, before % 1000));
            tlResult.setText("Result");
            tlResult.setForeground(Color.DARK_GRAY);
            tlResult.setVisible(true);
            tlWarning.setText("Press a button to next");
            tlWarning.setForeground(Color.DARK_GRAY);
            tlWarning.setVisible(true);
            getSwitcher().setTargetFPS(3);
            setState(ScenarioState.RESULT);
        }
    }

    protected void drawFrameInnerResult(Graphics2D g2) {
    }

    protected void drawFrameInnerFinish(Graphics2D g2) {
        getSwitcher().setNextScenario(new SingleScenario(getSwitcher()));
    }

    protected void drawFrameInnerClose(Graphics2D g2) {
        getSwitcher().setNextScenario(new ClosingScenario(getSwitcher()));
    }

    public ScenarioState getState() {
        return state;
    }

    public void setState(ScenarioState s) {
        synchronized (this) {
            state = s;
        }
    }

    public void tryToCancelScenario() {
        tlWarning.setText("Press a button 3 times to cancel");
        tlWarning.setForeground(COLOR_DARK_ORANGE);
        tlWarning.setVisible(true);
    }

    public void cancelScenario() {
        tlResult.setText("Canceled");
        tlResult.setForeground(COLOR_DARK_ORANGE);
        tlResult.setVisible(true);

        getSwitcher().setTargetFPS(3);
        setState(ScenarioState.RESULT);
    }

    public void nextScenario() {
        setState(ScenarioState.FINISH);
    }

    public void closeScenario() {
        setState(ScenarioState.CLOSE);
    }

    //TODO
    protected int getNumOfSensorsOfDev(int devid) {
        switch (devid) {
        case 0:
            return 0;
        case 1:
            return 0;
        case 2:
            return 6;
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

    public void resetTimeStart() {
        tStart = System.nanoTime();
    }

    public boolean isFinished() {
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

    protected class BTDeviceHandler implements BTDeviceListener {
        private SingleScenario scenario;

        public BTDeviceHandler(SingleScenario s) {
            scenario = s;
        }

        public void messageReceived(BTDeviceEvent e) {
            if (!scenario.getActivated()) {
                return;
            }

            try {
                parse(e);
            } catch (RuntimeException ex) {
                scenario.printError(CMD_HIT + ": Illegal format in answers.");
                System.err.println("  msg:" + ex.getMessage());
                System.err.println("  ans:" + e.getMessage());
            } catch (ParseException ex) {
                scenario.printError(CMD_HIT + ": Illegal format format (time) in answers.");
                System.err.println("  msg:" + ex.getMessage());
                System.err.println("  ans:" + e.getMessage());
            }
        }

        protected void parse(BTDeviceEvent e) throws ParseException {
            StringTokenizer st = new StringTokenizer(e.getMessage(), " ", false);
            SimpleDateFormat sd = new SimpleDateFormat("mm:ss.SSS");
            sd.setTimeZone(TimeZone.getTimeZone("GMT"));
            String next;

            int devid = parseID(st.nextToken(), PREFIX_DEVICE_ID);

            next = st.nextToken();
            if (next.equalsIgnoreCase(CMD_SINGLE)) {
                next = st.nextToken();

                if (next.equalsIgnoreCase("OK")) {
                    scenario.resetTimeStart();
                } else {
                    scenario.printError(CMD_SINGLE + ": Command is failed.");
                }
            } else {
                int senid = parseID(next, PREFIX_SENSOR_ID);
                Date datePast = sd.parse(st.nextToken());
                long msPast = datePast.getTime();
                Sensor sen = scenario.getSensor(devid, senid);

                scenario.printInfo(String.format("%s dev:%d sen:%d %3d.%03d",
                        tlTime.getText(), devid, senid, msPast / 1000, msPast % 1000));

                sen.setTimeHit(msPast);
                sen.setState(Sensor.SensorState.HIT);
            }
        }

        protected int parseID(String token, String prefix) {
            StringTokenizer st = new StringTokenizer(token, ":", false);
            String pre = st.nextToken();
            if (!pre.equalsIgnoreCase(prefix)) {
                throw new IllegalArgumentException(CMD_HIT + ": Answers have no prefix '" + prefix + "'.");
            }

            return Integer.parseInt(st.nextToken());
        }
    }

    protected class MouseHandler extends MouseAdapter {
        private SingleScenario scenario;
        private int cnt = 0;

        public MouseHandler(SingleScenario s) {
            scenario = s;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            synchronized (scenario) {
                switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    mouseLeftClicked(e);
                    break;
                case MouseEvent.BUTTON3:
                    mouseRightClicked(e);
                    break;
                }
            }
        }

        protected void mouseLeftClicked(MouseEvent e) {
            switch (scenario.getState()) {
            case RUN:
                cnt++;

                if (cnt > 0) {
                    scenario.tryToCancelScenario();
                }
                if (cnt >= 3) {
                    scenario.cancelScenario();
                }
                break;
            case RESULT:
                scenario.nextScenario();
                break;
            }
        }

        protected void mouseRightClicked(MouseEvent e) {
            switch (scenario.getState()) {
            case RESULT:
                scenario.closeScenario();
                break;
            }
        }
    }
}
