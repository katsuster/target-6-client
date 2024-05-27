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
    public static final int TITLE_OFFSET_LEFT = (int)(FONT_SIZE_TITLE * 1.5);
    public static final int TITLE_MARGIN_TOP = (int)(FONT_SIZE_TITLE * 0.1);
    public static final int TITLE_HIGHT = (int)(FONT_SIZE_TITLE * 1.0);

    private BTButtonHandler handlerBT;
    private MouseHandler handlerMouse;
    private Font fontTitle;
    private Font fontLarge;
    private Font fontSmall;
    private TreeNode<String, ScenarioData> scenarioRootNode = new TreeNode<>("root", null);
    private TreeNode<String, ScenarioData> curNode;
    private int indexSelected = 0;
    private boolean flagStart = false;
    private boolean flagClose = false;
    private TextLine tlClock;
    private List<ShapeBox> shScenarios = new ArrayList<>();
    private List<TextLine> tlScenarios = new ArrayList<>();

    public class ScenarioData {
        private String scenarioType;

        public ScenarioData(String str) {
            scenarioType = str;
        }

        public Scenario createScenario(ScenarioSwitcher sc) {
            CountUpScenario cs;
            TimeAttackScenario ts;
            RankingScenario rs;
            ArrayList<String> lst;
            Scenario s = null;

            switch (scenarioType) {
            case SCENARIO_COUNT_UP_30SEC:
                s = cs = new CountUpScenario(sc);
                cs.setTimeoutInMills(30000);
                break;
            case SCENARIO_COUNT_UP_20SEC:
                s = cs = new CountUpScenario(sc);
                cs.setTimeoutInMills(20000);
                break;
            case SCENARIO_COUNT_UP_15SEC:
                s = cs = new CountUpScenario(sc);
                cs.setTimeoutInMills(15000);
                break;
            case SCENARIO_TIME_ATTACK_6:
                s = ts = new TimeAttackScenario(sc);
                ts.setNumberOfTargets(6);
                break;
            case SCENARIO_TIME_ATTACK_5:
                s = ts = new TimeAttackScenario(sc);
                ts.setNumberOfTargets(5);
                break;
            case SCENARIO_TIME_ATTACK_4:
                s = ts = new TimeAttackScenario(sc);
                ts.setNumberOfTargets(4);
                break;
            case SCENARIO_RANKING_COUNT_UP:
                s = rs = new RankingScenario(sc);
                lst = new ArrayList<>();
                lst.add(SCENARIO_COUNT_UP_15SEC);
                lst.add(SCENARIO_COUNT_UP_20SEC);
                lst.add(SCENARIO_COUNT_UP_30SEC);
                rs.setScenarios(lst);
                break;
            case SCENARIO_RANKING_TIME_ATTACK:
                s = rs = new RankingScenario(sc);
                lst = new ArrayList<>();
                lst.add(SCENARIO_TIME_ATTACK_6);
                lst.add(SCENARIO_TIME_ATTACK_5);
                lst.add(SCENARIO_TIME_ATTACK_4);
                rs.setScenarios(lst);
                break;
            default:
                printError("Cannot find suitable scenario for " + scenarioType + ".", null);
                return null;
            }

            return s;
        }
    }

    public SelectScenario(ScenarioSwitcher sw) {
        super(sw);

        scenarioRootNode.addPath(SCENARIO_COUNT_UP_15SEC.split("/"), new ScenarioData(SCENARIO_COUNT_UP_15SEC));
        scenarioRootNode.addPath(SCENARIO_COUNT_UP_20SEC.split("/"), new ScenarioData(SCENARIO_COUNT_UP_20SEC));
        scenarioRootNode.addPath(SCENARIO_COUNT_UP_30SEC.split("/"), new ScenarioData(SCENARIO_COUNT_UP_30SEC));
        scenarioRootNode.addPath(SCENARIO_TIME_ATTACK_6.split("/"), new ScenarioData(SCENARIO_TIME_ATTACK_6));
        scenarioRootNode.addPath(SCENARIO_TIME_ATTACK_5.split("/"), new ScenarioData(SCENARIO_TIME_ATTACK_5));
        scenarioRootNode.addPath(SCENARIO_TIME_ATTACK_4.split("/"), new ScenarioData(SCENARIO_TIME_ATTACK_4));
        scenarioRootNode.addPath(SCENARIO_SEPARATOR.split("/"), null);
        scenarioRootNode.addPath(SCENARIO_RANKING_COUNT_UP.split("/"), new ScenarioData(SCENARIO_RANKING_COUNT_UP));
        scenarioRootNode.addPath(SCENARIO_RANKING_TIME_ATTACK.split("/"), new ScenarioData(SCENARIO_RANKING_TIME_ATTACK));
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

        expandChildItem(scenarioRootNode, false);

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
            ScenarioData sd = getSelectedItem().getValue();
            Scenario s = sd.createScenario(getSwitcher());
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

    public synchronized void nextItem() {
        TreeNode<String, ScenarioData>[] scenarios = curNode.getChildren();

        indexSelected++;
        if (indexSelected >= scenarios.length) {
            indexSelected = 0;
        }

        //Skip separator
        TreeNode<String, ScenarioData> selected = scenarios[indexSelected];
        if (!selected.hasChild() && selected.getValue() == null) {
            nextItem();
        }
    }

    private TreeNode<String, ScenarioData> getSelectedItem() {
        TreeNode<String, ScenarioData>[] scenarios = curNode.getChildren();
        return scenarios[indexSelected];
    }

    public synchronized void descentToChildItem() {
        descentToChildItem(getSelectedItem());
    }

    public synchronized void descentToChildItem(TreeNode<String, ScenarioData> node) {
        if (node.hasChild()) {
            //Non-leaf node
            expandChildItem(node, true);
        } else {
            //Leaf node
            if (node.getValue() != null) {
                flagStart = true;
            }
        }
    }

    public synchronized void expandChildItem(TreeNode<String, ScenarioData> node, boolean replace) {
        MainWindow mainWnd = getSwitcher().getMainWindow();
        TreeNode<String, ScenarioData>[] scenarios = node.getChildren();

        if (scenarios.length == 0) {
            return;
        }

        if (replace) {
            for (ShapeBox sh: shScenarios) {
                removeDrawable(sh);
            }
            for (TextLine tl: tlScenarios) {
                removeDrawable(tl);
            }
        }
        tlScenarios.clear();
        shScenarios.clear();

        for (int i = 0; i < scenarios.length; i++) {
            TextLine tl = new TextLine();
            tl.setText(scenarios[i].getKey());
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

        for (ShapeBox sh: shScenarios) {
            addDrawable(sh);
        }
        for (TextLine tl: tlScenarios) {
            addDrawable(tl);
        }

        indexSelected = 0;
        curNode = node;
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
            scenario.nextItem();
        }

        @Override
        public void mouseRightClicked() {
            scenario.closeScenario();
        }

        @Override
        public void mouseLeftLongPressed() {
            scenario.descentToChildItem();
        }
    }
}
