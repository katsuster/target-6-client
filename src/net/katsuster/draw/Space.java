package net.katsuster.draw;

/**
 * This class represents size of space of left/top/right/bottom.
 */
public class Space {
    //Size of left space
    public int left;
    //Size of top space
    public int top;
    //Size of right space
    public int right;
    //Size of bottom space
    public int bottom;

    /**
     * Create a new object with zero-sized space.
     */
    public Space() {
        this(0);
    }

    /**
     * Create a new object with same size of space (left = top = right = bottom = n).
     *
     * @param n Size of space
     */
    public Space(int n) {
        this(n, n, n, n);
    }

    /**
     * Create a new object with same size as other object.
     *
     * @param s Space object
     */
    public Space(Space s) {
        this(s.left, s.top, s.right, s.bottom);
    }

    /**
     * Create a new object with specified size.
     *
     * @param l Size of left
     * @param t Size of top
     * @param r Size of right
     * @param b Size of bottom
     */
    public Space(int l, int t, int r, int b) {
        left = l;
        top = t;
        right = r;
        bottom = b;
    }
}
