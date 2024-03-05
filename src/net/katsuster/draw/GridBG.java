package net.katsuster.draw;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GridBG extends AbstractDrawable {
    private List<Integer> gridX = new ArrayList<>();
    private List<Integer> gridY = new ArrayList<>();
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

        for (Integer x : gridX) {
            g2.drawLine(x, 0, x, cache.height);
        }
        for (Integer y : gridY) {
            g2.drawLine(0, y, cache.width, y);
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
            gridX.add(x);
        }
        for (int y = cnt.y; y < cnt.height; y += getGridHeight()) {
            gridY.add(y);
        }

        cache = cnt;
    }
}
