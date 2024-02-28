package net.katsuster.draw;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GridBG extends AbstractDrawable {
    private List<Shape> gridX = new ArrayList<>();
    private List<Shape> gridY = new ArrayList<>();
    private Rectangle cache = new Rectangle();
    private int gridWidth = 5;
    private int gridHeight = 5;

    public GridBG() {
        //do nothing
    }

    @Override
    protected void drawInner(Graphics2D g2) {
        if (!cache.equals(getContentBox().getContents())) {
            createGrid();
        }

        g2.setColor(getForeground());

        for (Shape s : gridX) {
            g2.draw(s);
        }
        for (Shape s : gridY) {
            g2.draw(s);
        }
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int w) {
        if (w <= 0) {
            throw new IllegalArgumentException("Grid width cannot be set 0 nor negative values.");
        }
        gridWidth = w;
        createGrid();
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int h) {
        if (h <= 0) {
            throw new IllegalArgumentException("Grid height cannot be set 0 nor negative values.");
        }
        gridHeight = h;
        createGrid();
    }

    public void setGridSize(int w, int h) {
        gridWidth = w;
        gridHeight = h;
        createGrid();
    }

    protected void createGrid() {
        Rectangle cnt = getContentBox().getContents();
        gridX = new ArrayList<>();
        gridY = new ArrayList<>();

        for (int x = cnt.x; x < cnt.width; x += getGridWidth()) {
            gridX.add(new Rectangle(x, cnt.y, getGridWidth(), cnt.height));
        }
        for (int y = cnt.y; y < cnt.height; y += getGridHeight()) {
            gridY.add(new Rectangle(cnt.x, y, cnt.width, getGridHeight()));
        }

        cache = cnt;
    }
}
