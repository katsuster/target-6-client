package net.katsuster.scenario;

import net.katsuster.draw.Drawable;
import net.katsuster.draw.TextLine;
import net.katsuster.ui.MainWindow;

import java.awt.*;

public class CancelSubScenario extends AbstractScenario {
    private Font fontTimerResult;
    private Font fontMedium;
    private Font fontSmall;
    private TextLine tlMsgCancel;
    private TextLine tlMsgNext;
    private TextLine tlMsgClose;
    private TextLine tlRestartFirst;
    private TextLine tlRestartSecond;

    public CancelSubScenario(ScenarioSwitcher sw) {
        super(sw);
    }

    @Override
    public SCORE_TYPE getScoreType() {
        return SCORE_TYPE.SCORE_EMPTY;
    }

    @Override
    public void activate() {
        MainWindow mainWnd = getSwitcher().getMainWindow();

        Font fUI = getSwitcher().getSetting().getFontUI();
        Font fMono = getSwitcher().getSetting().getFontMono();
        fontMedium = fUI.deriveFont(Font.PLAIN, FONT_SIZE_MEDIUM);
        fontSmall = fUI.deriveFont(Font.PLAIN, FONT_SIZE_SMALL);
        fontTimerResult = fMono.deriveFont(Font.PLAIN, FONT_SIZE_TIMER_RESULT);

        tlMsgCancel = new TextLine();
        tlMsgCancel.setText("Press(Long): Cancel Game");
        tlMsgCancel.setForeground(COLOR_DARK_ORANGE);
        tlMsgCancel.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlMsgCancel.setFont(fontMedium);
        tlMsgCancel.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight());
        tlMsgCancel.getContentBox().setMargin(20, 20, 20, 20);
        tlMsgCancel.setVisible(false);

        tlMsgNext = new TextLine();
        tlMsgNext.setText("Press(Short): Next Game");
        tlMsgNext.setForeground(COLOR_DARK_BLUE);
        tlMsgNext.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlMsgNext.setFont(fontSmall);
        tlMsgNext.getContentBox().setBounds(0, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlMsgNext.getContentBox().setMargin(20, 20, 20, 20);
        tlMsgNext.setVisible(false);

        tlMsgClose = new TextLine();
        tlMsgClose.setText("Press(Long): Back to Title");
        tlMsgClose.setForeground(COLOR_DARK_BLUE);
        tlMsgClose.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlMsgClose.setFont(fontSmall);
        tlMsgClose.getContentBox().setBounds(mainWnd.getWidth() / 2, 0,
                mainWnd.getWidth() / 2, mainWnd.getHeight());
        tlMsgClose.getContentBox().setMargin(20, 20, 20, 20);
        tlMsgClose.setVisible(false);

        tlRestartFirst = new TextLine();
        tlRestartFirst.setText("Press");
        tlRestartFirst.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.BOTTOM);
        tlRestartFirst.setForeground(Color.DARK_GRAY);
        tlRestartFirst.setFont(fontTimerResult);
        tlRestartFirst.getContentBox().setBounds(0, 0,
                mainWnd.getWidth(), mainWnd.getHeight() / 2);
        tlRestartFirst.getContentBox().setMargin(20, 20, 20, 20);
        tlRestartFirst.setVisible(false);

        tlRestartSecond = new TextLine();
        tlRestartSecond.setText("Button");
        tlRestartSecond.setAlign(Drawable.H_ALIGN.CENTER, Drawable.V_ALIGN.TOP);
        tlRestartSecond.setForeground(Color.DARK_GRAY);
        tlRestartSecond.setFont(fontTimerResult);
        tlRestartSecond.getContentBox().setBounds(0, mainWnd.getHeight() / 2,
                mainWnd.getWidth(), mainWnd.getHeight() / 2);
        tlRestartSecond.getContentBox().setMargin(20, 20, 20, 20);
        tlRestartSecond.setVisible(false);

        clearDrawable();
        addDrawable(tlMsgCancel);
        addDrawable(tlMsgNext);
        addDrawable(tlMsgClose);
        addDrawable(tlRestartFirst);
        addDrawable(tlRestartSecond);
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void drawFrame(Graphics2D g2) {
        drawAllDrawable(g2);
    }

    public synchronized void finishScenario() {
        tlMsgCancel.setVisible(false);
        tlMsgNext.setVisible(true);
        tlMsgClose.setVisible(true);
    }

    public synchronized void tryToCancelScenario() {
        tlMsgCancel.setVisible(true);
    }

    public synchronized void cancelScenario() {
        tlMsgCancel.setVisible(false);
        tlMsgNext.setVisible(true);
        tlMsgClose.setVisible(true);
    }

    public synchronized void restartScenario() {
        tlRestartFirst.setVisible(true);
        tlRestartSecond.setVisible(true);
    }
}
