package net.katsuster.scenario;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.ShapeBox;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;
import net.katsuster.ui.MouseAdapterEx;

public class SelectScenario extends AbstractScenario {
    private BTDeviceHandler handlerBT;
    private MouseHandler handlerMouse;
    private Font fontLarge;
    private Font fontSmall;
    private boolean flagStart = false;
    private TextLine tlMsg;
    private TextLine tlClock;

    public SelectScenario(ScenarioSwitcher sw) {
        super(sw);
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
        fontLarge = f.deriveFont(Font.PLAIN, FONT_SIZE_LARGE);
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
        tlMsg.setText("Select the game");
        tlMsg.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.CENTER);
        tlMsg.setForeground(Color.DARK_GRAY);
        tlMsg.setFont(fontLarge);
        tlMsg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        tlClock = new TextLine();
        tlClock.setAlign(Drawable.H_ALIGN.LEFT, Drawable.V_ALIGN.BOTTOM);
        tlClock.setForeground(Color.DARK_GRAY);
        tlClock.setFont(fontSmall);
        tlClock.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlClock.getContentBox().setMargin(5, 0, FONT_SIZE_SMALLEST, 5);

        TextLine tlVersion = new TextLine();
        tlVersion.setText("Target-6 v0.1");
        tlVersion.setAlign(Drawable.H_ALIGN.RIGHT, Drawable.V_ALIGN.BOTTOM);
        tlVersion.setForeground(Color.DARK_GRAY);
        tlVersion.setFont(fontSmall);
        tlVersion.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlVersion.getContentBox().setMargin(5, 0, FONT_SIZE_SMALLEST, 5);

        clearDrawable();
        addDrawable(bg);
        addDrawable(tlTitle);
        addDrawable(tlClock);
        addDrawable(tlVersion);
        addDrawable(tlMsg);
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
        tlClock.setText(df.format(new Date()));

        if (flagStart) {
            getSwitcher().setNextScenario(new SingleScenario(getSwitcher()));
        }

        drawAllDrawable(g2);
    }

    public synchronized void setFlagStart(boolean f) {
        flagStart = f;
    }

    protected class BTDeviceHandler extends BTCommandHandler {
        SelectScenario scenario;

        public BTDeviceHandler(SelectScenario s) {
            super(s);

            scenario = s;
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
    }

    protected class MouseHandler extends MouseAdapterEx {
        private SelectScenario scenario;

        public MouseHandler(SelectScenario s) {
            scenario = s;
        }

        @Override
        public void mouseLeftClicked() {

        }

        @Override
        public void mouseRightClicked() {
            //scenario.closeScenario();
        }

        @Override
        public void mouseLeftLongPressed() {
            scenario.setFlagStart(true);
        }
    }
}
