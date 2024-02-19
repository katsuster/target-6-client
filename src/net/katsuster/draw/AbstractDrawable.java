package net.katsuster.draw;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class AbstractDrawable implements Drawable {
    private ContentBox contentBox = new ContentBox();
    private boolean visible = true;
    private boolean filled = false;
    private Color foreground = Color.BLACK;
    private Color background = new Color(0, 0, 0, 0);
    private Font font;
    private Stroke stroke = new BasicStroke();
    private H_ALIGN hAlign = H_ALIGN.LEFT;
    private V_ALIGN vAlign = V_ALIGN.TOP;
    private SCALE scale = SCALE.SHRINK_AND_KEEP_ASPECT;

    /* for debug */
    private boolean debugDrawBounds = false;

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
    public boolean getFilled() {
        return filled;
    }

    @Override
    public void setFilled(boolean f) {
        filled = f;
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
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void setStroke(Stroke s) {
        stroke = s;
    }

    @Override
    public H_ALIGN getHAlign() {
        return hAlign;
    }

    @Override
    public void setHAlign(H_ALIGN h) {
        hAlign = h;
    }

    @Override
    public V_ALIGN getVAlign() {
        return vAlign;
    }

    @Override
    public void setVAlign(V_ALIGN v) {
        vAlign = v;
    }

    @Override
    public void setAlign(H_ALIGN h, V_ALIGN v) {
        setHAlign(h);
        setVAlign(v);
    }

    @Override
    public SCALE getScale() {
        return scale;
    }

    @Override
    public void setScale(SCALE s) {
        scale = s;
    }

    public boolean getDrawBounds() {
        return debugDrawBounds;
    }

    public void setDrawBounds(boolean b) {
        debugDrawBounds = b;
    }

    @Override
    public void draw(Graphics2D g2) {
        //Backup
        Color cBefore = g2.getColor();
        Font fBefore = g2.getFont();
        Stroke stBefore = g2.getStroke();
        AffineTransform trBefore = g2.getTransform();

        //Draw
        drawInner(g2);
        if (getDrawBounds()) {
            drawBounds(g2);
        }

        //Restore
        g2.setTransform(trBefore);
        g2.setStroke(stBefore);
        g2.setColor(cBefore);
        g2.setFont(fBefore);
    }

    protected void drawInner(Graphics2D g2) {
        //do nothing
    }

    protected void drawBounds(Graphics2D g2) {
        g2.setTransform(new AffineTransform());
        g2.setColor(new Color(192, 192, 128));
        g2.draw(getContentBox().getBounds());
        g2.setColor(new Color(192, 128, 192));
        g2.draw(getContentBox().getBorder());
        g2.setColor(new Color(128, 128, 192));
        g2.draw(getContentBox().getContents());
    }

    protected void drawContentBox(Graphics2D g2) {
        g2.setTransform(new AffineTransform());
    }
}
