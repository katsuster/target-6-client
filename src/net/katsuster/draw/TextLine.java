package net.katsuster.draw;

import java.awt.*;

public class TextLine extends AbstractDrawable {
    private String text = "";
    private Color shadow;
    private Point shadowPosition = new Point();

    public TextLine() {
        //do nothing
    }

    @Override
    protected void drawInner(Graphics2D g2) {
        Rectangle bdContent = getContentBox().getContents();
        FontMetrics fm = g2.getFontMetrics(getFont());
        int wStr = fm.stringWidth(getText());
        int ascentStr = fm.getMaxAscent();
        int descentStr = fm.getMaxDescent();
        int hStr = ascentStr + descentStr;
        int x = 0, y = 0;

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

        g2.setColor(getBackground());
        g2.fillRect(bdContent.x, bdContent.y, bdContent.width, bdContent.height);

        g2.setFont(getFont());
        g2.setColor(getShadow());
        g2.drawString(getText(), x + getShadowPositionX(), y + ascentStr + getShadowPositionY());

        g2.setColor(getForeground());
        g2.drawString(getText(), x, y + ascentStr);
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
    text = t;
}

    public Color getShadow() {
        return shadow;
    }

    public void setShadow(Color c) {
        shadow = c;
    }

    public Point getShadowPosition() {
        return shadowPosition;
    }

    public int getShadowPositionX() {
        return (int)shadowPosition.getX();
    }

    public int getShadowPositionY() {
        return (int)shadowPosition.getY();
    }

    public void setShadowPosition(int x, int y) {
        shadowPosition.setLocation(x, y);
    }

    public void setShadowPosition(Point p) {
        shadowPosition = p;
    }
}
