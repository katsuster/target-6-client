package net.katsuster.scenario;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;

public class OpeningScenario extends AbstractScenario {
    private ScenarioSwitcher switcher;
    private BTInOut btIO;
    private BufferedWriter[] btWr;
    private BTDeviceHandler handler;
    private Font font;

    private boolean flag = false;
    private long nanoStart;

    public OpeningScenario(ScenarioSwitcher sw) {
        switcher = sw;
        handler = new BTDeviceHandler(this);
    }

    @Override
    public void activate() {
        btIO = switcher.getBTInOut();
        btWr = btIO.getBTWriters();
        btIO.addBTDeviceListener(handler);

        Font f = UIManager.getFont("Panel.font");
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
                switcher.setNextScenario(new ClosingScenario(switcher));
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
