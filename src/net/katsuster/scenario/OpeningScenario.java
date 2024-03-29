package net.katsuster.scenario;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.*;

import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.ShapeBox;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;
import net.katsuster.ui.MouseAdapterEx;

public class OpeningScenario extends AbstractScenario {
    private BTDeviceHandler handlerBT;
    private MouseHandler handlerMouse;
    private Font fontLarge;
    private Font fontMedium;
    private Font fontSmall;
    private DevState[] devState = new DevState[BTInOut.NUM_DEVICES];
    private boolean flagReady = false;
    private boolean flagFailed = false;
    private boolean flagRestart = false;
    private boolean flagStart = false;
    private TextLine tlMsg;
    private TextLine tlClock;
    private TextLine tlVersion;
    private TextLine[] tlDevState = new TextLine[BTInOut.NUM_DEVICES];
    private Timer timerParent;
    private Thread thBTInit;

    public OpeningScenario(ScenarioSwitcher sw) {
        super(sw);
        Arrays.fill(devState, DevState.RESET);
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().setTargetFPS(3);

        handlerBT = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handlerBT);
        handlerMouse = new MouseHandler(this);
        mainWnd.addMouseListener(handlerMouse);

        Font f = getSwitcher().getSetting().getFont();
        fontLarge = f.deriveFont(Font.PLAIN, FONT_SIZE_LARGEST);
        fontMedium = f.deriveFont(Font.PLAIN, FONT_SIZE_MEDIUM);
        fontSmall = f.deriveFont(Font.PLAIN, FONT_SIZE_SMALLEST);

        GridBG bg = new GridBG();
        bg.setForeground(COLOR_BG_GRAY);
        bg.setGridSize(48, 48);
        bg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        TextLine tlTitle = new TextLine();
        tlTitle.setText("Titleタイトル");
        tlTitle.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.CENTER);
        tlTitle.setForeground(COLOR_DARK_BLUE);
        tlTitle.setFont(fontLarge);
        tlTitle.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight() / 2);

        tlMsg = new TextLine();
        tlMsg.setText("Please Wait...");
        tlMsg.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlMsg.setForeground(Color.DARK_GRAY);
        tlMsg.setFont(fontMedium);
        tlMsg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight() - FONT_SIZE_MEDIUM * 2);
        tlMsg.getContentBox().setMargin(20, 20, 20, 20);

        tlClock = new TextLine();
        tlClock.setAlign(Drawable.H_ALIGN.LEFT, Drawable.V_ALIGN.BOTTOM);
        tlClock.setForeground(Color.DARK_GRAY);
        tlClock.setFont(fontSmall);
        tlClock.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlClock.getContentBox().setMargin(5, 0, FONT_SIZE_SMALLEST, 5);

        tlVersion = new TextLine();
        tlVersion.setText("Target-6 v0.1");
        tlVersion.setAlign(Drawable.H_ALIGN.RIGHT, Drawable.V_ALIGN.BOTTOM);
        tlVersion.setForeground(Color.DARK_GRAY);
        tlVersion.setFont(fontSmall);
        tlVersion.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlVersion.getContentBox().setMargin(5, 0, FONT_SIZE_SMALLEST, 5);

        ShapeBox[] shDevState = new ShapeBox[BTInOut.NUM_DEVICES];
        for (int i = 0; i < tlDevState.length; i++) {
            int scrw = mainWnd.getWidth() / tlDevState.length;
            int scrh = 80;

            tlDevState[i] = new TextLine();
            tlDevState[i].setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.TOP);
            tlDevState[i].setFont(fontSmall);
            tlDevState[i].getContentBox().setBounds(scrw * i, mainWnd.getHeight() - scrh, scrw, scrh);
            tlDevState[i].getContentBox().setMargin(FONT_SIZE_SMALLEST / 2, FONT_SIZE_SMALLEST / 2,
                    FONT_SIZE_SMALLEST / 2, FONT_SIZE_SMALLEST / 2);
            tlDevState[i].getContentBox().setPadding(5, 5, 5, 5);

            shDevState[i] = new ShapeBox();
            shDevState[i].setShape(new RoundRectangle2D.Double(1, FONT_SIZE_SMALLEST / 2,
                    scrw, FONT_SIZE_SMALLEST * 2, 30, 30));
            shDevState[i].setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.TOP);
            shDevState[i].setBackground(Color.WHITE);
            shDevState[i].setForeground(COLOR_LIGHT_BLUE);
            shDevState[i].setScale(Drawable.SCALE.SHRINK_AND_KEEP_ASPECT);
            shDevState[i].setStroke(new BasicStroke(2));
            shDevState[i].getContentBox().setBounds(tlDevState[i].getContentBox().getBounds());
            shDevState[i].getContentBox().setMargin(FONT_SIZE_SMALLEST, FONT_SIZE_SMALLEST / 2,
                    FONT_SIZE_SMALLEST, FONT_SIZE_SMALLEST / 2);
        }

        clearDrawable();
        addDrawable(bg);
        addDrawable(tlClock);
        addDrawable(tlVersion);
        for (ShapeBox sh : shDevState) {
            addDrawable(sh);
        }
        for (TextLine tl : tlDevState) {
            addDrawable(tl);
        }
        addDrawable(tlMsg);
        addDrawable(tlTitle);

        timerParent = new Timer();
        timerParent.schedule(new TaskClock(this), 0, 500);

        thBTInit = new Thread(new BTInit(this));
        thBTInit.start();
    }

    @Override
    public void deactivate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        btIO.removeBTDeviceListener(handlerBT);
        mainWnd.removeMouseListener(handlerMouse);

        timerParent.cancel();
        try {
            thBTInit.interrupt();
            thBTInit.join();
        } catch (InterruptedException ex) {
            //ignore
        }
    }

    @Override
    public void drawFrame(Graphics2D g2) {
        for (int i = 0; i < devState.length; i++) {
            switch (devState[i]) {
            case FAILED:
                timerParent.cancel();
                tlDevState[i].setText("Dev" + i + " Failed");
                tlDevState[i].setForeground(COLOR_DARK_ORANGE);
                tlMsg.setText("ERROR! Press button to retry");
                tlMsg.setForeground(COLOR_DARK_ORANGE);
                setFlagFailed(true);
                break;
            case RESET:
                tlDevState[i].setText("Dev" + i + " Reset");
                tlDevState[i].setForeground(Color.LIGHT_GRAY);
                break;
            case CONNECT_WAIT:
                tlDevState[i].setText("Dev" + i + " Connect");
                tlDevState[i].setForeground(Color.LIGHT_GRAY);
                break;
            case CONNECT:
                tlDevState[i].setText("Dev" + i + " Connect [OK]");
                tlDevState[i].setForeground(Color.LIGHT_GRAY);
                break;
            case INIT_WAIT:
                tlDevState[i].setText("Dev" + i + " Init");
                tlDevState[i].setForeground(Color.LIGHT_GRAY);
                break;
            case INIT:
                tlDevState[i].setText("Dev" + i + " Init [OK]");
                tlDevState[i].setForeground(COLOR_DARK_BLUE);
                break;
            }
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        tlClock.setText(df.format(new Date()));

        boolean finish = true;
        for (int i = 0; i < devState.length; i++) {
            if (devState[i] != DevState.INIT) {
                finish = false;
            }
        }
        flagReady = finish;

        if (flagFailed && flagRestart) {
            getSwitcher().setNextScenario(new OpeningScenario(getSwitcher()));
        }
        if (flagReady && flagStart) {
            getSwitcher().setNextScenario(new SingleScenario(getSwitcher()));
        }

        drawAllDrawable(g2);
    }

    public synchronized DevState getDevState(int id) {
        return devState[id];
    }

    public synchronized void setDevState(int id, DevState s) {
        devState[id] = s;
    }

    public synchronized boolean getFlagFailed() {
        return flagFailed;
    }

    public synchronized void setFlagFailed(boolean f) {
        flagFailed = f;
    }

    public synchronized boolean getFlagReady() {
        return flagReady;
    }

    public synchronized void setFlagReady(boolean f) {
        flagReady = f;
    }

    public synchronized void setFlagRestart(boolean f) {
        flagRestart = f;
    }

    public synchronized void setFlagStart(boolean f) {
        flagStart = f;
    }

    public synchronized void closeScenario() {
        getSwitcher().setNextScenario(new ClosingScenario(getSwitcher()));
    }

    private class TaskClock extends TimerTask {
        private int cnt = 0;

        public TaskClock(OpeningScenario s) {
            //do nothing
        }

        @Override
        public void run() {
            if (!flagReady) {
                switch (cnt % 3) {
                case 0:
                    tlMsg.setText("Please Wait.  ");
                    break;
                case 1:
                    tlMsg.setText("Please Wait.. ");
                    break;
                case 2:
                    tlMsg.setText("Please Wait...");
                    break;
                }
            } else {
                tlMsg.setText("Push Button to Start");

                if (cnt % 4 < 3) {
                    tlMsg.setVisible(true);
                } else {
                    tlMsg.setVisible(false);
                }
            }

            cnt++;
        }
    }

    protected class BTInit implements Runnable {
        private OpeningScenario scenario;

        public BTInit(OpeningScenario s) {
            scenario = s;
        }

        @Override
        public void run() {
            try {
                runInner();
            } catch (InterruptedException ex) {
                //ignore
            }
        }

        public void runInner() throws InterruptedException {
            ScenarioSwitcher switcher = scenario.getSwitcher();
            BTInOut btIO = switcher.getBTInOut();
            int retry = 5;
            boolean done = false;

            for (int j = 0; j < retry; j++) {
                int id = -1;

                printInfo("Try to connect " + j, null);
                try {
                    for (id = 0; id < BTInOut.NUM_DEVICES; id++) {
                        scenario.setDevState(id, DevState.CONNECT_WAIT);
                        btIO.connectBTDevice(id);

                        if (btIO.getDeviceStatus(id) == BTInOut.BTStatus.CONNECTED) {
                            scenario.setDevState(id, DevState.CONNECT);
                        }
                    }
                } catch (Exception ex) {
                    scenario.setDevState(id, DevState.FAILED);
                    printError("Failed to connect device " + id, ex);
                    break;
                }

                if (btIO.getNumberOfConnectedDevices() == BTInOut.NUM_DEVICES) {
                    done = true;
                    break;
                }
            }
            if (!done) {
                printError("Failed to connect. Please check bluetooth settings.", null);
                switcher.setBTRecover(true);
                return;
            }
            printInfo("Connected.", null);
            switcher.setBTRecover(true);

            for (int i = 0; i < BTInOut.NUM_DEVICES; i++) {
                if (scenario.getDevState(i) != DevState.CONNECT) {
                    continue;
                }

                boolean success = writeLine(i, CMD_INIT + " " + i);
                if (!success) {
                    printError("Failed to initialize dev " + i, null);
                    return;
                }
                scenario.setDevState(i, DevState.INIT_WAIT);
            }
            printInfo("Wait for initialized.", null);

            for (int i = 0; i < BTInOut.NUM_DEVICES; i++) {
                if (scenario.getDevState(i) == DevState.INIT) {
                    continue;
                }

                done = false;
                for (int j = 0; j < 30; j++) {
                    if (scenario.getDevState(i) == DevState.INIT) {
                        done = true;
                        break;
                    }
                    Thread.sleep(100);
                }
                if (!done) {
                    printError("Failed to init in id:" + i, null);
                    scenario.setDevState(i, DevState.FAILED);
                    return;
                }
            }
            printInfo("Initialized.", null);
        }
    }

    protected class BTDeviceHandler extends BTCommandHandler {
        private OpeningScenario scenario;

        public BTDeviceHandler(OpeningScenario s) {
            super(s);

            scenario = s;
        }

        @Override
        public void cmdInit(StringTokenizer st, int devid) {
            String next = st.nextToken();

            if (!next.equalsIgnoreCase("OK")) {
                scenario.printError(CMD_INIT + ": Command is failed.", null);
                return;
            }

            scenario.setDevState(devid, DevState.INIT);

            checkAllDevices();
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

        protected void checkAllDevices() {
            boolean finish = true;

            for (int i = 0; i < BTInOut.NUM_DEVICES; i++) {
                DevState ds = scenario.getDevState(i);

                if (ds != DevState.INIT) {
                    finish = false;
                }
            }
            scenario.setFlagReady(finish);
        }
    }

    protected class MouseHandler extends MouseAdapterEx {
        private OpeningScenario scenario;

        public MouseHandler(OpeningScenario s) {
            scenario = s;
        }

        @Override
        public void mouseLeftClicked() {
            if (scenario.getFlagFailed()) {
                scenario.setFlagRestart(true);
            } else if (getFlagReady()) {
                scenario.setFlagStart(true);
            } else {
                scenario.printWarn("Devices are not ready.", null);
            }
        }

        @Override
        public void mouseRightClicked() {
            scenario.closeScenario();
        }

        @Override
        public void mouseLeftLongPressed() {
            scenario.closeScenario();
        }
    }
}
