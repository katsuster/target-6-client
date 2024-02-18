package net.katsuster.scenario;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.katsuster.ble.BTDeviceEvent;
import net.katsuster.ble.BTDeviceListener;
import net.katsuster.ble.BTInOut;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;

public class OpeningScenario extends AbstractScenario {
    public static final String CMD_INIT = "init";

    private BTDeviceHandler handlerBT;
    private MouseHandler handlerMouse;
    private Font fontLarge;
    private Font fontMedium;
    private Font fontSmall;
    private DevState[] devState = new DevState[BTInOut.NUM_DEVICES];
    private boolean flagReady = false;
    private boolean flagStart = false;
    private TextLine tlMsg;
    private TextLine tlVersion;
    private TextLine[] tlDevState = new TextLine[BTInOut.NUM_DEVICES];

    public OpeningScenario(ScenarioSwitcher sw) {
        super(sw);
        Arrays.fill(devState, DevState.RESET);
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().addLogLater("Entering " + getName() + "\n");
        getSwitcher().setTargetFPS(3);

        handlerBT = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handlerBT);
        handlerMouse = new MouseHandler(this);
        mainWnd.addMouseListener(handlerMouse);

        Font f = getSwitcher().getSetting().getFont();
        fontLarge = f.deriveFont(Font.PLAIN, 120);
        fontMedium = f.deriveFont(Font.PLAIN, 32);
        fontSmall = f.deriveFont(Font.PLAIN, 14);

        TextLine tlTitle = new TextLine();
        tlTitle.setText("Titleタイトル");
        tlTitle.setAlign(TextLine.TEXT_HALIGN.CENTER, TextLine.TEXT_VALIGN.CENTER);
        tlTitle.setFont(fontLarge);
        tlTitle.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight() / 2);

        tlMsg = new TextLine();
        tlMsg.setText("Please Wait...");
        tlMsg.setAlign(TextLine.TEXT_HALIGN.CENTER, TextLine.TEXT_VALIGN.CENTER);
        tlMsg.setFont(fontMedium);
        tlMsg.getContentBox().setBounds(0, mainWnd.getHeight() / 2,
                mainWnd.getWidth(), mainWnd.getHeight() / 2);

        tlVersion = new TextLine();
        tlVersion.setAlign(TextLine.TEXT_HALIGN.RIGHT, TextLine.TEXT_VALIGN.BOTTOM);
        tlVersion.setForeground(Color.DARK_GRAY);
        tlVersion.setFont(fontSmall);
        tlVersion.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlVersion.getContentBox().setMargin(0, 0, 15, 5);

        for (int i = 0; i < tlDevState.length; i++) {
            int scrw = mainWnd.getWidth() / tlDevState.length;
            int scrh = 80;

            tlDevState[i] = new TextLine();
            tlDevState[i].setAlign(TextLine.TEXT_HALIGN.CENTER, TextLine.TEXT_VALIGN.TOP);
            tlDevState[i].setFont(fontSmall);
            tlDevState[i].getContentBox().setBounds(scrw * i, mainWnd.getHeight() - scrh, scrw, scrh);
            tlDevState[i].getContentBox().setMargin(10, 10, 10, 10);
        }

        clearDrawable();
        addDrawable(tlTitle);
        addDrawable(tlMsg);
        addDrawable(tlVersion);
        for (TextLine tl : tlDevState) {
            addDrawable(tl);
        }

        Thread thBTInit = new Thread(new BTInit(this));
        thBTInit.start();
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
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        tlVersion.setText(df.format(new Date()) + " Application v0.1 Copyright(c) Name 2023-2024.");

        for (int i = 0; i < devState.length; i++) {
            switch (devState[i]) {
            case FAILED:
                tlDevState[i].setText("Dev" + i + " Failed");
                tlDevState[i].setForeground(Color.RED);
                tlMsg.setText("ERROR! Please restart");
                tlMsg.setForeground(Color.RED);
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
                tlDevState[i].setForeground(Color.BLUE);
                break;
            }
        }

        boolean finish = true;
        for (int i = 0; i < devState.length; i++) {
            if (devState[i] != DevState.INIT) {
                finish = false;
            }
        }
        flagReady = finish;

        if (flagReady) {
            tlMsg.setText("Push Button to Start...");
        }
        if (flagReady && flagStart) {
            getSwitcher().setNextScenario(new CountUpScenario(getSwitcher()));
        }

        drawAllDrawable(g2);
    }

    public DevState getDevState(int id) {
        return devState[id];
    }

    public void setDevState(int id, DevState s) {
        synchronized (this) {
            devState[id] = s;
        }
    }

    public boolean getFlagReady() {
        return flagReady;
    }

    public void setFlagReady(boolean f) {
        synchronized (this) {
            flagReady = f;
        }
    }

    public void setFlagStart(boolean f) {
        synchronized (this) {
            flagStart = f;
        }
    }

    protected class BTInit implements Runnable {
        private OpeningScenario scenario;

        public BTInit(OpeningScenario s) {
            scenario = s;
        }

        @Override
        public void run() {
            ScenarioSwitcher switcher = scenario.getSwitcher();
            BTInOut btIO = switcher.getBTInOut();
            BufferedWriter[] btWr = btIO.getWriters();
            int retry = 5;
            boolean done = false;

            for (int j = 0; j < retry; j++) {
                int id = -1;

                switcher.addLogLater("Try to connect " + j + ".\n");
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

                    switcher.addLogLater("Failed to connect device " + id + "\n");
                    System.err.println("  msg:" + ex.getMessage());
                }

                if (btIO.getNumberOfConnectedDevices() == BTInOut.NUM_DEVICES) {
                    done = true;
                    break;
                }
            }
            if (!done) {
                switcher.addLogLater("Failed to connect. Please check bluetooth settings.\n");
                return;
            }
            switcher.addLogLater("Connected.\n");
            switcher.setBTRecover(true);

            for (int i = 0; i < BTInOut.NUM_DEVICES; i++) {
                if (scenario.getDevState(i) != DevState.CONNECT) {
                    continue;
                }

                try {
                    synchronized (scenario) {
                        btWr[i].write(CMD_INIT + " " + i);
                        btWr[i].flush();
                        scenario.setDevState(i, DevState.INIT_WAIT);
                    }

                    Thread.sleep(300);
                } catch (IOException ex) {
                    scenario.printError(CMD_INIT + "I/O error in write.");
                } catch (InterruptedException ex) {
                    //Do nothing
                }
            }
            switcher.addLogLater("Initialized.\n");
        }
    }

    protected class BTDeviceHandler implements BTDeviceListener {
        OpeningScenario scenario;

        public BTDeviceHandler(OpeningScenario s) {
            scenario = s;
        }

        public void messageReceived(BTDeviceEvent e) {
            if (!scenario.getActivated()) {
                return;
            }

            try {
                StringTokenizer st = new StringTokenizer(e.getMessage(), " ", false);

                int devid = parseDeviceID(st.nextToken());
                String cmdEcho = st.nextToken();
                if (!cmdEcho.equalsIgnoreCase(CMD_INIT)) {
                    //answers of another cmd, ignored
                    scenario.printWarn("Ignore answers of " + cmdEcho + ".");
                    return;
                }

                scenario.setDevState(devid, DevState.INIT);
            } catch (NoSuchElementException ex) {
                scenario.printError(CMD_INIT + ": Illegal number format in answers.");
                System.err.println("  msg:" + ex.getMessage());
            } catch (NumberFormatException ex) {
                scenario.printError(CMD_INIT + ": Illegal number format in answers.");
                System.err.println("  msg:" + ex.getMessage());
            }

            checkAllDevices();
        }

        protected int parseDeviceID(String token) {
            StringTokenizer st = new StringTokenizer(token, ":", false);

            String prefix = st.nextToken();
            if (!prefix.equalsIgnoreCase("d")) {
                throw new IllegalArgumentException(CMD_INIT + ": Answers have no prefix 'd'.");
            }

            return Integer.parseInt(st.nextToken());
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

    protected class MouseHandler extends MouseAdapter {
        OpeningScenario scenario;

        public MouseHandler(OpeningScenario s) {
            scenario = s;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println(getName() + ": click!");
            scenario.getSwitcher().addLogLater(getName() + ": click!\n");

            if (scenario.getFlagReady()) {
                scenario.setFlagStart(true);
            } else {
                scenario.printWarn("Devices are not ready.");
            }
        }
    }
}
