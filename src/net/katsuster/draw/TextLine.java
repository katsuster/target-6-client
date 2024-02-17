package net.katsuster.draw;

import java.awt.*;

public class TextLine extends AbstractDrawable {
    public enum TEXT_HALIGN {
        LEFT, CENTER, RIGHT,
    }

    public enum TEXT_VALIGN {
        TOP, CENTER, BOTTOM,
    }

    private String text = "";
    private TEXT_HALIGN hAlign = TEXT_HALIGN.LEFT;
    private TEXT_VALIGN vAlign = TEXT_VALIGN.TOP;
    private FontMetrics fm;

    public TextLine() {
        //do nothing
    }

    @Override
    public void draw(Graphics2D g2) {
        Color cBefore = g2.getColor();
        Font fBefore = g2.getFont();
        fm = g2.getFontMetrics(getFont());
        drawInner(g2);
        g2.setColor(cBefore);
        g2.setFont(fBefore);
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        text = t;
    }

    public TEXT_HALIGN getHAlign() {
        return hAlign;
    }

    public void setHAlign(TEXT_HALIGN h) {
        hAlign = h;
    }

    public TEXT_VALIGN getVAlign() {
        return vAlign;
    }

    public void setVAlign(TEXT_VALIGN v) {
        vAlign = v;
    }

    public void setAlign(TEXT_HALIGN h, TEXT_VALIGN v) {
        setHAlign(h);
        setVAlign(v);
    }

    protected void drawInner(Graphics2D g2) {
        Rectangle bdContent = getContentBox().getContents();
        int x = 0, y = 0;
        int wStr = fm.stringWidth(getText());
        int ascentStr = fm.getMaxAscent();
        int descentStr = fm.getMaxDescent();
        int hStr = ascentStr + descentStr;

        switch (getHAlign()) {
        case LEFT:
            x = bdContent.x;
            break;
        case CENTER:
            x = bdContent.x + (bdContent.width - wStr) / 2;
            break;
        case RIGHT:
            x = bdContent.x + bdContent.width - wStr;
            break;
        }

        switch (getVAlign()) {
        case TOP:
            y = bdContent.y;
            break;
        case CENTER:
            y = bdContent.y + (bdContent.height - hStr) / 2;
            break;
        case BOTTOM:
            y = bdContent.y + bdContent.height - hStr;
            break;
        }

        g2.setColor(getForeground());
        g2.setFont(getFont());
        g2.drawString(getText(), x, y + ascentStr);

        g2.setColor(getBackground());
        //g2.fillRect(x, y, wStr, hStr);
        g2.fillRect(bdContent.x, bdContent.y, bdContent.width, bdContent.height);
    }

}
