package net.katsuster.scenario;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.katsuster.ble.BTDeviceEvent;
import net.katsuster.ble.BTDeviceListener;
import net.katsuster.ble.BTInOut;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;

public class OpeningScenario extends AbstractScenario {
    private BufferedWriter[] btWr;
    private BTDeviceHandler handlerBT;
    private MouseHandler handlerMouse;
    private Font fontLarge;
    private Font fontMedium;
    private Font fontSmall;
    private long tStart;
    private boolean flagStart;
    private TextLine tlVersion;

    public OpeningScenario(ScenarioSwitcher sw) {
        super(sw);
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().addLogLater("Entering " + getName() + "\n");
        getSwitcher().setTargetFPS(3);

        btWr = btIO.getBTWriters();
        handlerBT = new BTDeviceHandler(this);
        btIO.addBTDeviceListener(handlerBT);
        handlerMouse = new MouseHandler(this);
        mainWnd.addMouseListener(handlerMouse);

        Font f = getSwitcher().getSetting().getFont();
        fontLarge = f.deriveFont(Font.PLAIN, 120);
        fontMedium = f.deriveFont(Font.PLAIN, 32);
        fontSmall = f.deriveFont(Font.PLAIN, 14);

        tStart = System.nanoTime();
        flagStart = false;

        TextLine tlTitle = new TextLine();
        tlTitle.setText("Titleタイトル");
        tlTitle.setAlign(TextLine.TEXT_HALIGN.CENTER, TextLine.TEXT_VALIGN.CENTER);
        tlTitle.setFont(fontLarge);
        tlTitle.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight() / 2);

        TextLine tlNext = new TextLine();
        tlNext.setText("Push Button to Start...");
        tlNext.setAlign(TextLine.TEXT_HALIGN.CENTER, TextLine.TEXT_VALIGN.CENTER);
        tlNext.setFont(fontMedium);
        tlNext.getContentBox().setBounds(0, mainWnd.getHeight() / 2,
                mainWnd.getWidth(), mainWnd.getHeight() / 2);

        tlVersion = new TextLine();
        tlVersion.setAlign(TextLine.TEXT_HALIGN.RIGHT, TextLine.TEXT_VALIGN.BOTTOM);
        tlVersion.setForeground(Color.DARK_GRAY);
        tlVersion.setFont(fontSmall);
        tlVersion.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlVersion.getContentBox().setMargin(0, 0, 15, 5);

        clearDrawable();
        addDrawable(tlTitle);
        addDrawable(tlNext);
        addDrawable(tlVersion);
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

        if (flagStart) {
            getSwitcher().setNextScenario(new CountUpScenario(getSwitcher()));
        }

        drawAllDrawable(g2);
    }

    public void setFlagStart(boolean f) {
        flagStart = f;
        tStart = System.nanoTime();
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

            System.out.println(getName() + ": " + e.getMessage());
            //scenario.setFlag(false);
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
            scenario.setFlagStart(true);
        }
    }
}
