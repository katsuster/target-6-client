package net.katsuster.scenario;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.katsuster.ble.BTInOut;
import net.katsuster.draw.Drawable;
import net.katsuster.draw.GridBG;
import net.katsuster.draw.ShapeBox;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;
import net.katsuster.ui.MouseAdapterEx;

public class SelectScenario extends AbstractScenario {
    public static final String SCENARIO_COUNT_UP = "Count Up";
    public static final String SCENARIO_TIME_ATTACK = "Time Attack";
    public static final String SCENARIO_RANKING = "Ranking";
    public static final String SCENARIO_SEPARATOR = "-----";
    public static final int TITLE_SPACE_H = (int)(FONT_SIZE_TITLE * 1.5);
    public static final int TITLE_SPACE_V = (int)(FONT_SIZE_TITLE * 1.0);

    private BTButtonHandler handlerBT;
    private MouseHandler handlerMouse;
    private Font fontTitle;
    private Font fontLarge;
    private Font fontSmall;
    private boolean flagStart = false;
    private boolean flagClose = false;
    private TextLine tlClock;
    List<ShapeBox> shScenarios = new ArrayList<>();
    List<TextLine> tlScenarios = new ArrayList<>();
    private List<String> scenarios = new ArrayList<>();
    private int indexSelected = 0;

    public SelectScenario(ScenarioSwitcher sw) {
        super(sw);

        scenarios.add(SCENARIO_COUNT_UP);
        scenarios.add(SCENARIO_TIME_ATTACK);
        scenarios.add(SCENARIO_SEPARATOR);
        scenarios.add(SCENARIO_RANKING);
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        BTInOut btIO = getSwitcher().getBTInOut();

        getSwitcher().setTargetFPS(3);

        handlerMouse = new MouseHandler(this);
        mainWnd.addMouseListener(handlerMouse);
        handlerBT = new BTButtonHandler(this, handlerMouse);
        btIO.addBTDeviceListener(handlerBT);

        Font fUI = getSwitcher().getSetting().getFontUI();
        fontTitle = fUI.deriveFont(Font.PLAIN, FONT_SIZE_TITLE);
        fontLarge = fUI.deriveFont(Font.PLAIN, FONT_SIZE_LARGE);
        fontSmall = fUI.deriveFont(Font.PLAIN, FONT_SIZE_SMALLEST);

        GridBG bg = new GridBG();
        bg.setForeground(COLOR_BG_GRAY);
        bg.setGridSize(48, 48);
        bg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        TextLine[] tlTitle = new TextLine[3];
        for (int i = 0; i < tlTitle.length; i++) {
            tlTitle[i] = new TextLine();
            tlTitle[i].setVAlign(Drawable.V_ALIGN.TOP);
            tlTitle[i].setForeground(COLOR_DARK_BLUE);
            tlTitle[i].setShadow(Color.LIGHT_GRAY);
            tlTitle[i].setShadowPosition(5, 5);
            tlTitle[i].setFont(fontTitle);
            tlTitle[i].getContentBox().setBounds(
                    TITLE_SPACE_H, i * TITLE_SPACE_V,
                    mainWnd.getWidth() - TITLE_SPACE_H * 2, TITLE_SPACE_V);
        }

        tlTitle[0].setText(CODE_TITLE_WORD1);
        tlTitle[0].setHAlign(Drawable.H_ALIGN.LEFT);
        tlTitle[1].setText(CODE_TITLE_WORD2);
        tlTitle[1].setHAlign(Drawable.H_ALIGN.CENTER);
        tlTitle[2].setText(CODE_TITLE_WORD3);
        tlTitle[2].setHAlign(Drawable.H_ALIGN.RIGHT);

        for (int i = 0; i < scenarios.size(); i++) {
            ShapeBox sh = new ShapeBox();

            shScenarios.add(sh);

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
        tlVersion.setText(CODE_NAME + " " + CODE_VERSION);
        tlVersion.setAlign(Drawable.H_ALIGN.RIGHT, Drawable.V_ALIGN.BOTTOM);
        tlVersion.setForeground(Color.DARK_GRAY);
        tlVersion.setFont(fontSmall);
        tlVersion.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlVersion.getContentBox().setMargin(5, 0, FONT_SIZE_SMALLEST, 5);

        clearDrawable();
        addDrawable(bg);
        for (TextLine tl : tlTitle) {
            addDrawable(tl);
        }
        addDrawable(tlClock);
        addDrawable(tlVersion);
        for (ShapeBox sh: shScenarios) {
            addDrawable(sh);
        }
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

        for (TextLine tl: tlScenarios) {
            if (tl == tlScenarios.get(indexSelected)) {
                tl.setForeground(COLOR_DARK_ORANGE);
            } else {
                tl.setForeground(Color.DARK_GRAY);
            }
        }

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

    public static SCORE_TYPE getScoreType(String scr) {
        switch (scr) {
        case SCENARIO_COUNT_UP:
            return SCORE_TYPE.SCORE_COUNT_UP;
        case SCENARIO_TIME_ATTACK:
            return SCORE_TYPE.SCORE_TIME_ATTACK;
        case SCENARIO_RANKING:
        case SCENARIO_SEPARATOR:
        default:
            return SCORE_TYPE.SCORE_EMPTY;
        }
    }

    private Scenario getSelectedScenario() {
        switch (scenarios.get(indexSelected)) {
        case SCENARIO_COUNT_UP:
            return new CountUpScenario(getSwitcher());
        case SCENARIO_TIME_ATTACK:
            return new TimeAttackScenario(getSwitcher());
        case SCENARIO_RANKING:
            RankingScenario ns = new RankingScenario(getSwitcher());
            ns.setScenarios(scenarios.stream()
                    .filter(w -> !w.equals(SCENARIO_SEPARATOR))
                    .filter(w -> !w.equals(SCENARIO_RANKING))
                    .toList());
            return ns;
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

        //Skip separator
        if (scenarios.get(indexSelected).equalsIgnoreCase(SCENARIO_SEPARATOR)) {
            nextSelection();
        }
    }

    public synchronized void startScenario() {
        flagStart = true;
    }

    public synchronized void closeScenario() {
        flagClose = true;
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
