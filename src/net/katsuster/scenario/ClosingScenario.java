package net.katsuster.scenario;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;

import net.katsuster.ble.BTDeviceEvent;
import net.katsuster.ble.BTDeviceListener;
import net.katsuster.ble.BTInOut;

public class ClosingScenario extends AbstractScenario {
    private ScenarioSwitcher switcher;
    private BTInOut btIO;
    private BufferedWriter[] btWr;
    private BTDeviceHandler handler;
    private Font font;

    public ClosingScenario(ScenarioSwitcher sw) {
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
    }

    @Override
    public void deactivate() {
        btIO.removeBTDeviceListener(handler);
    }

    @Override
    public void drawFrame(Graphics2D g2) {
        g2.setFont(font);
        g2.drawString("Closing...", 10, 50);

        switcher.terminate();
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
