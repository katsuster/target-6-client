package net.katsuster.draw;

import java.awt.*;

public class TextLine extends AbstractDrawable {
    private String text = "";
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
