package net.katsuster.scenario;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedWriter;
import java.util.Arrays;

import net.katsuster.ble.BTDeviceEvent;
import net.katsuster.ble.BTDeviceListener;
import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.ShapeBox;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;

public class ClosingScenario extends AbstractScenario {
    public static final int FONT_SIZE_LARGE = 120;
    public static final int FONT_SIZE_MEDIUM = 32;
    public static final int FONT_SIZE_SMALL = 16;

    private BTDeviceHandler handler;
    private Font fontMedium;
    private Font fontSmall;
    private DevState[] devState = new DevState[BTInOut.NUM_DEVICES];
    private TextLine tlMsg;
    private TextLine[] tlDevState = new TextLine[BTInOut.NUM_DEVICES];

    public ClosingScenario(ScenarioSwitcher sw) {
        super(sw);
        Arrays.fill(devState, DevState.INIT);
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().setTargetFPS(3);

        handler = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handler);

        Font f = getSwitcher().getSetting().getFont();
        fontMedium = f.deriveFont(Font.PLAIN, FONT_SIZE_MEDIUM);
        fontSmall = f.deriveFont(Font.PLAIN, FONT_SIZE_SMALL);

        GridBG bg = new GridBG();
        bg.setForeground(new Color(240, 240, 240));
        bg.setGridSize(48, 48);
        bg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlMsg = new TextLine();
        tlMsg.setText("Closing...");
        tlMsg.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.CENTER);
        tlMsg.setForeground(Color.BLACK);
        tlMsg.setFont(fontMedium);
        tlMsg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        ShapeBox[] shDevState = new ShapeBox[BTInOut.NUM_DEVICES];
        for (int i = 0; i < tlDevState.length; i++) {
            int scrw = mainWnd.getWidth() / tlDevState.length;
            int scrh = 80;

            tlDevState[i] = new TextLine();
            tlDevState[i].setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.TOP);
            tlDevState[i].setFont(fontSmall);
            tlDevState[i].getContentBox().setBounds(scrw * i, mainWnd.getHeight() - scrh, scrw, scrh);
            tlDevState[i].getContentBox().setMargin(FONT_SIZE_SMALL / 2, FONT_SIZE_SMALL / 2,
                    FONT_SIZE_SMALL / 2, FONT_SIZE_SMALL / 2);
            tlDevState[i].getContentBox().setPadding(5, 5, 5, 5);

            shDevState[i] = new ShapeBox();
            shDevState[i].setShape(new RoundRectangle2D.Double(1, FONT_SIZE_SMALL / 2,
                    scrw, FONT_SIZE_SMALL * 2, 30, 30));
            shDevState[i].setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.TOP);
            shDevState[i].setBackground(Color.WHITE);
            shDevState[i].setForeground(new Color(192, 192, 255));
            shDevState[i].setScale(Drawable.SCALE.SHRINK_AND_KEEP_ASPECT);
            shDevState[i].setStroke(new BasicStroke(2));
            shDevState[i].getContentBox().setBounds(tlDevState[i].getContentBox().getBounds());
            shDevState[i].getContentBox().setMargin(FONT_SIZE_SMALL, FONT_SIZE_SMALL / 2,
                    FONT_SIZE_SMALL, FONT_SIZE_SMALL / 2);
        }

        clearDrawable();
        addDrawable(bg);
        addDrawable(tlMsg);
        for (ShapeBox sh : shDevState) {
            addDrawable(sh);
        }
        for (TextLine tl : tlDevState) {
            addDrawable(tl);
        }

        Thread thBTTerm = new Thread(new BTTerm(this));
        thBTTerm.start();
    }

    @Override
    public void deactivate() {
        BTInOut btIO = getSwitcher().getBTInOut();

        btIO.removeBTDeviceListener(handler);
    }

    @Override
    public void drawFrame(Graphics2D g2) {
        for (int i = 0; i < devState.length; i++) {
            switch (devState[i]) {
            case FAILED:
                tlDevState[i].setText("Dev" + i + " Failed");
                tlDevState[i].setForeground(Color.RED);
                tlMsg.setText("ERROR! Please restart");
                tlMsg.setForeground(Color.RED);
                break;
            case RESET:
                tlDevState[i].setText("Dev" + i + " Disconnect [OK]");
                tlDevState[i].setForeground(Color.LIGHT_GRAY);
                break;
            case CONNECT_WAIT:
                tlDevState[i].setText("Dev" + i + " Disconnect");
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
            if (devState[i] != DevState.RESET) {
                finish = false;
            }
        }
        if (finish) {
            getSwitcher().terminate();
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

    protected class BTTerm implements Runnable {
        private ClosingScenario scenario;

        public BTTerm(ClosingScenario s) {
            scenario = s;
        }

        @Override
        public void run() {
            ScenarioSwitcher switcher = scenario.getSwitcher();
            BTInOut btIO = switcher.getBTInOut();
            int retry = 5;
            boolean done = false;

            switcher.setBTRecover(false);
            for (int j = 0; j < retry; j++) {
                int id = -1;

                switcher.addLogLater("Try to disconnect " + j + ".\n");
                try {
                    for (id = 0; id < BTInOut.NUM_DEVICES; id++) {
                        scenario.setDevState(id, DevState.CONNECT_WAIT);
                        btIO.disconnectBTDevice(id);

                        if (btIO.getDeviceStatus(id) == BTInOut.BTStatus.DISCONNECTED) {
                            scenario.setDevState(id, DevState.RESET);
                        }
                    }
                } catch (Exception ex) {
                    scenario.setDevState(id, DevState.FAILED);

                    switcher.addLogLater("Failed to disconnect device " + id + "\n");
                    System.err.println("  msg:" + ex.getMessage());
                }

                if (btIO.getNumberOfConnectedDevices() == 0) {
                    done = true;
                    break;
                }
            }
            if (!done) {
                switcher.addLogLater("Failed to disconnect, but forced to continue.\n");
            }
            switcher.addLogLater("Disconnected.\n");
        }
    }

    protected class BTDeviceHandler implements BTDeviceListener {
        ClosingScenario scenario;

        public BTDeviceHandler(ClosingScenario s) {
            scenario = s;
        }

        public void messageReceived(BTDeviceEvent e) {
            if (!scenario.getActivated()) {
                return;
            }

            System.out.println("opening: " + e.getMessage());
        }
    }
}
