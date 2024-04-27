package net.katsuster.scenario;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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
    public static final String SCENARIO_SEPARATOR = "・";
    public static final int TITLE_OFFSET_LEFT = (int)(FONT_SIZE_TITLE * 1.5);
    public static final int TITLE_MARGIN_TOP = (int)(FONT_SIZE_TITLE * 0.1);
    public static final int TITLE_HIGHT = (int)(FONT_SIZE_TITLE * 1.0);

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
        fontTitle = fUI.deriveFont(Font.BOLD, FONT_SIZE_TITLE);
        fontLarge = fUI.deriveFont(Font.PLAIN, FONT_SIZE_LARGE);
        fontSmall = fUI.deriveFont(Font.PLAIN, FONT_SIZE_SMALLEST);

        GridBG bg = new GridBG();
        bg.setForeground(COLOR_BG_GRAY);
        bg.setGridSize(48, 48);
        bg.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());

        TextLine[] tlTitle = new TextLine[2];
        for (int i = 0; i < tlTitle.length; i++) {
            tlTitle[i] = new TextLine();
            tlTitle[i].setVAlign(Drawable.V_ALIGN.TOP);
            tlTitle[i].setForeground(COLOR_DARK_BLUE);
            tlTitle[i].setShadow(Color.LIGHT_GRAY);
            tlTitle[i].setShadowPosition(5, 5);
            tlTitle[i].setFont(fontTitle);
            tlTitle[i].getContentBox().setBounds(
                    TITLE_OFFSET_LEFT, TITLE_MARGIN_TOP + i * TITLE_HIGHT,
                    mainWnd.getWidth() - TITLE_OFFSET_LEFT * 2, TITLE_HIGHT);
        }

        tlTitle[0].setText(CODE_TITLE_WORD1);
        tlTitle[0].setHAlign(Drawable.H_ALIGN.LEFT);
        tlTitle[1].setText(CODE_TITLE_WORD2);
        tlTitle[1].setHAlign(Drawable.H_ALIGN.CENTER);

        ShapeBox shTitle = new ShapeBox();
        shTitle.setShape(new RoundRectangle2D.Double(1, 1,
                mainWnd.getWidth() - TITLE_OFFSET_LEFT,
                TITLE_MARGIN_TOP + tlTitle.length * TITLE_HIGHT,
                10, 10));
        shTitle.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.CENTER);
        shTitle.setBackground(Color.WHITE);
        shTitle.setForeground(COLOR_DARK_BLUE);
        shTitle.setScale(Drawable.SCALE.SHRINK_AND_KEEP_ASPECT);
        shTitle.setStroke(new BasicStroke(2));
        shTitle.getContentBox().setBounds(
                TITLE_OFFSET_LEFT / 2, FONT_SIZE_TITLE / 2 - TITLE_MARGIN_TOP,
                mainWnd.getWidth() - TITLE_OFFSET_LEFT,
                TITLE_MARGIN_TOP * 2 + tlTitle.length * TITLE_HIGHT);

        for (int i = 0; i < scenarios.size(); i++) {
            TextLine tl = new TextLine();
            tl.setText(scenarios.get(i));
            tl.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.TOP);
            tl.setForeground(Color.DARK_GRAY);
            tl.setFont(fontLarge);
            tl.getContentBox().setBounds(mainWnd.getWidth() / 4,
                    (int)(mainWnd.getHeight() / 2.3) + (int)(FONT_SIZE_LARGE * 1.2) * i,
                    mainWnd.getWidth() / 4 * 2, (int)(FONT_SIZE_LARGE * 1.4));
            tlScenarios.add(tl);

            ShapeBox sh = new ShapeBox();
            sh.setShape(new RoundRectangle2D.Double(1, 1,
                    tl.getContentBox().getWidth(),
                    tl.getContentBox().getHeight() - FONT_SIZE_LARGE / 3,
                    FONT_SIZE_LARGE, FONT_SIZE_LARGE));
            sh.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.CENTER);
            sh.setBackground(Color.WHITE);
            sh.setForeground(Color.DARK_GRAY);
            sh.setScale(Drawable.SCALE.SHRINK_AND_KEEP_ASPECT);
            sh.setStroke(new BasicStroke(1));
            sh.getContentBox().setBounds(tl.getContentBox().getBounds());
            sh.getContentBox().setMargin(0, FONT_SIZE_LARGE / 3, 0, 0);
            sh.setVisible(false);
            shScenarios.add(sh);
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
        addDrawable(shTitle);
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
        for (ShapeBox sh: shScenarios) {
            sh.setVisible(sh == shScenarios.get(indexSelected));
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
