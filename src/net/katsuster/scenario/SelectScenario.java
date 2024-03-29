package net.katsuster.scenario;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;
import net.katsuster.ui.MouseAdapterEx;

public class SelectScenario extends AbstractScenario {
    public static final String SCENARIO_COUNT_UP = "Count Up";
    public static final String SCENARIO_TIME_ATTACK = "Time Attack";

    private BTDeviceHandler handlerBT;
    private MouseHandler handlerMouse;
    private Font fontLarge;
    private Font fontSmall;
    private boolean flagStart = false;
    private boolean flagClose = false;
    private TextLine tlSelector;
    private TextLine tlClock;
    private List<String> scenarios = new ArrayList<>();
    private int indexSelected = 0;

    public SelectScenario(ScenarioSwitcher sw) {
        super(sw);

        scenarios.add(SCENARIO_COUNT_UP);
        scenarios.add(SCENARIO_TIME_ATTACK);
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

        tlSelector = new TextLine();
        tlSelector.setText(">>");
        tlSelector.setAlign(Drawable.H_ALIGN.RIGHT, Drawable.V_ALIGN.TOP);
        tlSelector.setForeground(COLOR_DARK_ORANGE);
        tlSelector.setFont(fontLarge);
        tlSelector.getContentBox().setBounds(0, mainWnd.getHeight() / 2,
                mainWnd.getWidth() / 4 - FONT_SIZE_LARGE / 2, (int)(FONT_SIZE_LARGE * 1.3));

        List<TextLine> tlScenarios = new ArrayList<>();
        for (int i = 0; i < scenarios.size(); i++) {
            TextLine tl = new TextLine();
            tl.setText(scenarios.get(i));
            tl.setAlign(Drawable.H_ALIGN.LEFT, Drawable.V_ALIGN.TOP);
            tl.setForeground(Color.DARK_GRAY);
            tl.setFont(fontLarge);
            tl.getContentBox().setBounds(mainWnd.getWidth() / 4,
                    mainWnd.getHeight() / 2 + (int)(FONT_SIZE_LARGE * 1.3) * i,
                    mainWnd.getWidth() / 4 * 2, (int)(FONT_SIZE_LARGE * 1.3));

            tlScenarios.add(tl);
        }

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
        addDrawable(tlSelector);
        addDrawable(tlClock);
        addDrawable(tlVersion);
        for (TextLine tl: tlScenarios) {
            addDrawable(tl);
        }
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
        MainWindow mainWnd = getSwitcher().getMainWindow();

        tlSelector.getContentBox().setBounds(0,
                mainWnd.getHeight() / 2 + (int)(FONT_SIZE_LARGE * 1.3) * indexSelected,
                mainWnd.getWidth() / 4 - FONT_SIZE_LARGE / 2, (int)(FONT_SIZE_LARGE * 1.3));

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        tlClock.setText(df.format(new Date()));

        if (flagStart) {
            Scenario s = getSelectedScenario();
            if (s == null) {
                printError("Cannot select scenario, go to title.", null);
                s = new SelectScenario(getSwitcher());
            }

            getSwitcher().setNextScenario(s);
        }
        if (flagClose) {
            getSwitcher().setNextScenario(new ClosingScenario(getSwitcher()));
        }

        drawAllDrawable(g2);
    }

    private Scenario getSelectedScenario() {
        switch (scenarios.get(indexSelected)) {
        case SCENARIO_TIME_ATTACK:
            return new SingleScenario(getSwitcher());
        case SCENARIO_COUNT_UP:
        default:
            printError("Invalid scenario is selected.", null);
            break;
        }

        return null;
    }

    public synchronized void nextSelection() {
        indexSelected++;
        if (indexSelected >= scenarios.size()) {
            indexSelected = 0;
        }
    }

    public synchronized void startScenario() {
        flagStart = true;
    }

    public synchronized void closeScenario() {
        flagClose = true;
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
            scenario.nextSelection();
        }

        @Override
        public void mouseRightClicked() {
            scenario.closeScenario();
        }

        @Override
        public void mouseLeftLongPressed() {
            scenario.startScenario();
        }
    }
}
