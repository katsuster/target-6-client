package net.katsuster.scenario;

import java.awt.*;

public interface Scenario {
    public static final Color COLOR_BG_GRAY = new Color(240, 240, 240);
    public static final Color COLOR_LIGHT_BLUE = new Color(192, 192, 255);
    public static final Color COLOR_DARK_BLUE = new Color(40, 85, 160);
    public static final Color COLOR_DARK_ORANGE = new Color(247, 119, 15);

    public static final int FONT_SIZE_LARGEST = 120;
    public static final int FONT_SIZE_LARGE = 80;
    public static final int FONT_SIZE_MEDIUM = 32;
    public static final int FONT_SIZE_SMALL = 24;
    public static final int FONT_SIZE_SMALLEST = 16;

    String getName();
    ScenarioSwitcher getSwitcher();
    boolean getActivated();
    void setActivated(boolean a);

    void activate();
    void deactivate();
    void drawFrame(Graphics2D g2);
}
