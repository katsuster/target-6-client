package net.katsuster.scenario;

import net.katsuster.ble.BTDeviceEvent;
import net.katsuster.ble.BTDeviceListener;
import net.katsuster.ble.BTInOut;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class CountUpScenario extends AbstractScenario {
    private BufferedWriter[] btWr;
    private BTDeviceHandler handlerBT;
    private MouseHandler handlerMouse;
    private Font fontLarge;
    private Font fontSmall;
    private long tStart;
    private TextLine tlTime;

    private boolean flag = false;

    public CountUpScenario(ScenarioSwitcher sw) {
        super(sw);
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().addLogLater("Entering " + getName() + "\n");
        getSwitcher().setTargetFPS(60);

        btWr = btIO.getBTWriters();
        handlerBT = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handlerBT);
        handlerMouse = new MouseHandler(this);
        mainWnd.addMouseListener(handlerMouse);

        Font f = getSwitcher().getSetting().getFont();
        fontLarge = f.deriveFont(Font.PLAIN, 120);
        fontSmall = f.deriveFont(Font.PLAIN, 14);

        tStart = System.nanoTime();

        tlTime = new TextLine();
        tlTime.setAlign(TextLine.TEXT_HALIGN.CENTER, TextLine.TEXT_VALIGN.CENTER);
        tlTime.setForeground(Color.BLACK);
        tlTime.setFont(fontLarge);
        tlTime.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        addDrawable(tlTime);
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
            if (!flag) {
                //btWr[0].write("start\n");
                btWr[0].flush();
                flag = true;
            }

            long nano = System.nanoTime() - tStart;
            long sec = nano / 1000000000;
            long mil = (nano / 1000000) % 1000;
            String curTime = String.format("%3d.%03d", sec, mil);
            tlTime.setText(curTime);

            if (nano > 30000000000L) {
                getSwitcher().setNextScenario(new ClosingScenario(getSwitcher()));
            }
        } catch (IOException ex) {
            //do nothing
        }

        drawAllDrawable(g2);
    }

    public void setFlag(boolean f) {
        flag = f;
    }

    protected class BTDeviceHandler implements BTDeviceListener {
        CountUpScenario scenario;

        public BTDeviceHandler(CountUpScenario s) {
            scenario = s;
        }

        public void messageReceived(BTDeviceEvent e) {
            if (!scenario.getActivated()) {
                return;
            }

            System.out.println("countup: " + e.getMessage());
            scenario.setFlag(false);
        }
    }

    protected class MouseHandler extends MouseAdapter {
        CountUpScenario scenario;

        public MouseHandler(CountUpScenario s) {
            scenario = s;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println(getName() + ": click!");
            scenario.getSwitcher().addLogLater(getName() + ": click!\n");
        }
    }
}
