package net.katsuster.scenario;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.swing.*;

import net.katsuster.ble.BTDeviceEvent;
import net.katsuster.ble.BTDeviceListener;
import net.katsuster.ble.BTInOut;

public class OpeningScenario extends AbstractScenario {
    private BTInOut btIO;
    private BufferedWriter[] btWr;
    private BTDeviceHandler handler;
    private Font font;
    private long nanoStart;

    private boolean flag = false;

    public OpeningScenario(ScenarioSwitcher sw) {
        setSwitcher(sw);
    }

    @Override
    public void activate() {
        btIO = getSwitcher().getBTInOut();
        btWr = btIO.getBTWriters();
        handler = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handler);

        Font f = getSwitcher().getSetting().getFont();
        font = f.deriveFont(Font.PLAIN, 36);

        nanoStart = System.nanoTime();
    }

    @Override
    public void deactivate() {
        btIO.removeBTDeviceListener(handler);
    }

    @Override
    public void drawFrame(Graphics2D g2) {
        try {
            if (!flag) {
                btWr[0].write("start\n");
                btWr[0].flush();
                flag = true;
            }

            long nano = System.nanoTime() - nanoStart;
            long sec = nano / 1000000000;
            long mil = (nano / 1000000) % 1000;
            String curTime = String.format("%3d.%03d", sec, mil);

            g2.setFont(font);
            g2.drawString(curTime, 10, 50);

            if (nano > 15000000000L) {
                getSwitcher().setNextScenario(new ClosingScenario(getSwitcher()));
            }
        } catch (IOException ex) {
            //do nothing
        }
    }

    public void setFlag(boolean f) {
        flag = f;
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

            System.out.println("opening: " + e.getMessage());
            scenario.setFlag(false);
        }
    }
}
