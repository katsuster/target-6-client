package net.katsuster.draw;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class ShapeBox extends AbstractDrawable {
    private Shape shape = new Rectangle();

    public ShapeBox() {
        //do nothing
    }

    @Override
    protected void drawInner(Graphics2D g2) {
        Rectangle ctx = getContentBox().getContents();
        Rectangle shp = shape.getBounds();
        boolean shrinkX = ctx.width <= shp.width;
        boolean shrinkY = ctx.height <= shp.height;
        int minw = Integer.min(ctx.width, shp.width);
        int minh = Integer.min(ctx.height, shp.height);
        int maxw = Integer.max(ctx.width, shp.width);
        int maxh = Integer.max(ctx.height, shp.height);
        double scaleShrinkX = (double)minw / shp.width;
        double scaleShrinkY = (double)minh / shp.height;
        double scaleExpandX = (double)maxw / shp.width;
        double scaleExpandY = (double)maxh / shp.height;
        double scaleX = 1, scaleY = 1, scaleMin;
        double offX = 0, offY = 0;

        switch (getScale()) {
        case JUST:
            if (shrinkX) {
                scaleX = scaleShrinkX;
            } else {
                scaleX = scaleExpandX;
            }
            if (shrinkY) {
                scaleY = scaleShrinkY;
            } else {
                scaleY = scaleExpandY;
            }
            break;
        case JUST_AND_KEEP_ASPECT:
            if (shrinkX) {
                scaleX = scaleShrinkX;
            } else {
                scaleX = scaleExpandX;
            }
            if (shrinkY) {
                scaleY = scaleShrinkY;
            } else {
                scaleY = scaleExpandY;
            }

            scaleMin = Double.min(scaleX, scaleY);
            scaleX = scaleMin;
            scaleY = scaleMin;
            break;
        case SHRINK:
            scaleX = scaleShrinkX;
            scaleY = scaleShrinkY;
            break;
        case SHRINK_AND_KEEP_ASPECT:
            scaleMin = Double.min(scaleShrinkX, scaleShrinkY);
            scaleX = scaleMin;
            scaleY = scaleMin;
            break;
        }

        if (!shrinkX) {
            switch (getHAlign()) {
            case LEFT:
                offX = 0;
                break;
            case CENTER:
                offX = (ctx.width - shp.width * scaleX) / 2;
                break;
            case RIGHT:
                offX = ctx.width - shp.width * scaleX;
                break;
            }
        }

        if (!shrinkY) {
            switch (getVAlign()) {
            case TOP:
                offY = 0;
                break;
            case CENTER:
                offY = (ctx.height - shp.height * scaleY) / 2;
                break;
            case BOTTOM:
                offY = ctx.height - shp.height * scaleY;
                break;
            }
        }

        AffineTransform af = new AffineTransform();
        af.translate(ctx.x - shp.x * scaleX + offX, ctx.y - shp.y * scaleY + offY);
        af.scale(scaleX, scaleY);

        g2.setTransform(af);

        g2.setColor(getBackground());
        g2.fill(shape);

        g2.setColor(getForeground());
        g2.setStroke(getStroke());
        g2.draw(shape);
        if (getFilled()) {
            g2.fill(shape);
        }
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape s) {
        shape = s;
    }
}
