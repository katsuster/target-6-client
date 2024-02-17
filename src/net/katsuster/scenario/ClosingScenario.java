package net.katsuster.scenario;

import java.awt.*;
import java.io.BufferedWriter;

import net.katsuster.ble.BTDeviceEvent;
import net.katsuster.ble.BTDeviceListener;
import net.katsuster.ble.BTInOut;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;

public class ClosingScenario extends AbstractScenario {
    private BufferedWriter[] btWr;
    private BTDeviceHandler handler;
    private Font fontMedium;
    private TextLine tlMsg;

    public ClosingScenario(ScenarioSwitcher sw) {
        super(sw);
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().addLogLater("Entering " + getName() + "\n");
        getSwitcher().setTargetFPS(60);

        btWr = btIO.getBTWriters();
        handler = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handler);

        Font f = getSwitcher().getSetting().getFont();
        fontMedium = f.deriveFont(Font.PLAIN, 32);

        tlMsg = new TextLine();
        tlMsg.setAlign(TextLine.TEXT_HALIGN.CENTER, TextLine.TEXT_VALIGN.CENTER);
        tlMsg.setForeground(Color.BLACK);
        tlMsg.setFont(fontMedium);
        tlMsg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        addDrawable(tlMsg);
    }

    @Override
    public void deactivate() {
        BTInOut btIO = getSwitcher().getBTInOut();

        btIO.removeBTDeviceListener(handler);
    }

    @Override
    public void drawFrame(Graphics2D g2) {
        getSwitcher().terminate();

        drawAllDrawable(g2);
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
