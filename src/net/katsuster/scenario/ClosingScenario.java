package net.katsuster.scenario;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.ShapeBox;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;

public class ClosingScenario extends AbstractScenario {
    private BTDeviceHandler handler;
    private Font fontLarge;
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

        getSwitcher().setTargetFPS(FPS_RESULT);

        handler = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handler);

        Font fUI = getSwitcher().getSetting().getFontUI();
        fontLarge = fUI.deriveFont(Font.PLAIN, FONT_SIZE_LARGE);
        fontSmall = fUI.deriveFont(Font.PLAIN, FONT_SIZE_SMALLEST);

        GridBG bg = new GridBG();
        bg.setForeground(COLOR_BG_GRAY);
        bg.setGridSize(48, 48);
        bg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlMsg = new TextLine();
        tlMsg.setText("Closing...");
        tlMsg.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.CENTER);
        tlMsg.setForeground(Color.DARK_GRAY);
        tlMsg.setFont(fontLarge);
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
        for (DevState ds : devState) {
            if (ds != DevState.RESET) {
                finish = false;
                break;
            }
        }
        if (finish) {
            getSwitcher().terminate();
        }

        drawAllDrawable(g2);
    }

    public synchronized DevState getDevState(int id) {
        return devState[id];
    }

    public synchronized void setDevState(int id, DevState s) {
        devState[id] = s;
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

                printInfo("Try to disconnect " + j, null);
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
                    printError("Failed to disconnect device " + id, ex);
                }

                if (btIO.getNumberOfConnectedDevices() == 0) {
                    done = true;
                    break;
                }
            }
            if (!done) {
                printWarn("Failed to disconnect, but forced to continue.", null);
            }
            printInfo("Disconnected.", null);
        }
    }

    protected class BTDeviceHandler extends BTCommandHandler {
        ClosingScenario scenario;

        public BTDeviceHandler(ClosingScenario s) {
            super(s);

            scenario = s;
        }
    }
}
