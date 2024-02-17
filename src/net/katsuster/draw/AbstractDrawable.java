package net.katsuster.draw;

import java.awt.*;

public class AbstractDrawable implements Drawable {
    private ContentBox contentBox = new ContentBox();
    private boolean visible = true;
    private Color foreground = Color.BLACK;
    private Color background = new Color(0, 0, 0, 0);
    private Font font;

    @Override
    public ContentBox getContentBox() {
        return contentBox;
    }

    @Override
    public boolean getVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean v) {
        visible = v;
    }

    @Override
    public Color getForeground() {
        return foreground;
    }

    @Override
    public void setForeground(Color c) {
        foreground = c;
    }

    @Override
    public Color getBackground() {
        return background;
    }

    @Override
    public void setBackground(Color c) {
        background = c;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font f) {
        font = f;
    }

    @Override
    public void draw(Graphics2D g2) {
        //do nothing
    }
}
