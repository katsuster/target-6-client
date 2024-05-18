package net.katsuster.draw;

import java.awt.*;

/**
 * This class represents a drawing area of contents.
 *
 * Drawing area is consisted some areas as follows:
 *
 * <ul>
 * <li>Bounds: The whole area.</li>
 * <li>Margin: The gap between borders and bounds.</li>
 * <li>Borders: The area for drawing borders.</li>
 * <li>Padding: The gap between contents and borders.</li>
 * <li>Contents: The area for drawing contents (texts, pictures, etc.)</li>
 * </ul>
 *
 * <pre>
 *  (X, Y)          width
 *        +---------------------------+ ___ Bounds
 *        |                           |/
 *        |  +--------------------+  ______ Margin
 *        |  |                    | / |
 *        |  |  +-------------+   | _______ Borders
 *        |  |  |aaaaaaaaaaaaa|   |/  |
 *        |  |  |bbbbbbbbbbbbb|  __________ Padding
 * height |  |  |ccccccccccccc| / |   |
 *        |  |  |ddddddddddddd| ___________ Contents
 *        |  |  |eeeeeeeeeeeee|/  |   |
 *        |  |  |fffffffffffff|   |   |
 *        |  |  +-------------+   |   |
 *        |  |                    |   |
 *        |  +--------------------+   |
 *        +---------------------------+
 * </pre>
 *
 * The programmers can specify size of area of bounds, margin and padding only.
 * Other size of areas will be calculated automatically.
 * The area of borders is defined by bounds and margin, contents is defined by borders and paddings.
 */
public class ContentBox {
    //The whole area (Bounds)
    private Rectangle bounds;
    //Size of margin
    private Space margin;
    //Size of padding
    private Space padding;

    /**
     * Create a new object with position (0, 0), size of area is zero, no margin, no padding.
     */
    public ContentBox() {
        this(0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0);
    }

    /**
     * Create a new object that has specified position (x, y), size of area (w, h) with no margin, no padding.
     *
     * @param x  Position of X
     * @param y  Position of Y
     * @param w  Width of bounds
     * @param h  Height of bounds
     */
    public ContentBox(int x, int y, int w, int h) {
        this(x, y, w, h,
                0, 0, 0, 0,
                0, 0, 0, 0);
    }

    /**
     * Create a new object that has specified position (x, y), size of area (w, h) and margin (ml, mt, mr, mb) with no padding.
     *
     * @param x  Position of X
     * @param y  Position of Y
     * @param w  Width of bounds
     * @param h  Height of bounds
     * @param ml Size of left margin
     * @param mt Size of top margin
     * @param mr Size of right margin
     * @param mb Size of bottom margin
     */
    public ContentBox(int x, int y, int w, int h,
                      int ml, int mt, int mr, int mb) {
        this(x, y, w, h,
                ml, mt, mr, mb,
                0, 0, 0, 0);
    }

    /**
     * Create a new object with specified position (x, y), size of area (w, h), margin (ml, mt, mr, mb) and padding (pl, pt, pr, pb).
     *
     * @param x  Position of X
     * @param y  Position of Y
     * @param w  Width of bounds
     * @param h  Height of bounds
     * @param ml Size of left margin
     * @param mt Size of top margin
     * @param mr Size of right margin
     * @param mb Size of bottom margin
     * @param pl Size of left padding
     * @param pt Size of top padding
     * @param pr Size of right padding
     * @param pb Size of bottom padding
     */
    public ContentBox(int x, int y, int w, int h,
                      int ml, int mt, int mr, int mb,
                      int pl, int pt, int pr, int pb) {
        bounds = new Rectangle(x, y, w, h);
        margin = new Space(ml, mt, mr, mb);
        padding = new Space(pl, pt, pr, pb);
    }

    /**
     * Get X position of bounds.
     *
     * @return X position
     */
    public int getX() {
        return bounds.x;
    }

    /**
     * Set X position of bounds.
     *
     * @param x X position
     */
    public void setX(int x) {
        bounds.x = x;
    }

    /**
     * Get Y position of bounds.
     *
     * @return Y position
     */
    public int getY() {
        return bounds.y;
    }

    /**
     * Set X position of bounds.
     *
     * @param y Y position
     */
    public void setY(int y) {
        bounds.y = y;
    }

    /**
     * Get width of bounds.
     *
     * @return Width
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Set width of bounds.
     *
     * @param w Width
     */
    public void setWidth(int w) {
        bounds.width = w;
    }

    /**
     * Get height of bounds.
     *
     * @return Height
     */
    public int getHeight() {
        return bounds.height;
    }

    /**
     * Set height of bounds.
     *
     * @param h Height
     */
    public void setHeight(int h) {
        bounds.height = h;
    }

    /**
     * Get the area of bounds.
     *
     * @return Area of bounds
     */
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    /**
     * Set the area of bounds.
     *
     * @param x  X position
     * @param y  Y position
     * @param w  Width
     * @param h  Height
     */
    public void setBounds(int x, int y, int w, int h) {
        bounds = new Rectangle(x, y, w, h);
    }

    /**
     * Set the area of bounds.
     *
     * @param r Area of bounds
     */
    public void setBounds(Rectangle r) {
        bounds = new Rectangle(r);
    }

    /**
     * Get the size of margin.
     *
     * @return Size of margin
     */
    public Space getMargin() {
        return new Space(margin);
    }

    /**
     * Set the size of margin.
     *
     * @param l Size of left
     * @param t Size of top
     * @param r Size of right
     * @param b Size of bottom
     */
    public void setMargin(int l, int t, int r, int b) {
        margin = new Space(l, t, r, b);
    }

    /**
     * Set the size of margin.
     *
     * @param s Size of margin
     */
    public void setMargin(Space s) {
        margin = new Space(s);
    }

    /**
     * Get the area of borders.
     *
     * @return Area of borders
     */
    public Rectangle getBorder() {
        return new Rectangle(
                bounds.x + margin.left,
                bounds.y + margin.top,
                bounds.width - margin.left - margin.right,
                bounds.height - margin.top - margin.bottom);
    }

    /**
     * Get the size of padding.
     *
     * @return Size of padding
     */
    public Space getPadding() {
        return new Space(padding);
    }

    /**
     * Set the size of padding.
     *
     * @param l Size of left
     * @param t Size of top
     * @param r Size of right
     * @param b Size of bottom
     */
    public void setPadding(int l, int t, int r, int b) {
        padding = new Space(l, t, r, b);
    }

    /**
     * Set the size of padding.
     *
     * @param s Size of padding
     */
    public void setPadding(Space s) {
        padding = new Space(s);
    }

    /**
     * Get the area of contents.
     *
     * @return Area of contents
     */
    public Rectangle getContents() {
        Rectangle border = getBorder();

        return new Rectangle(
                border.x + padding.left,
                border.y + padding.top,
                border.width - padding.left - padding.right,
                border.height - padding.top - padding.bottom);
    }
}
