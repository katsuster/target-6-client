package net.katsuster.scenario;

import java.awt.*;

public interface Scenario {
    Color COLOR_BG_GRAY = new Color(240, 240, 240);
    Color COLOR_LIGHT_BLUE = new Color(192, 192, 255);
    Color COLOR_DARK_BLUE = new Color(40, 85, 160);
    Color COLOR_DARK_ORANGE = new Color(247, 119, 15);

    int FONT_SIZE_TIMER = 272;
    int FONT_SIZE_LARGEST = 96;
    int FONT_SIZE_LARGE = 64;
    int FONT_SIZE_MEDIUM = 48;
    int FONT_SIZE_SMALL = 32;
    int FONT_SIZE_SMALLEST = 16;

    String CMD_INIT = "init";
    String CMD_SINGLE = "single";
    String CMD_BEEP = "beep";
    String CMD_TATK = "tatk";
    String RES_BUTTON = "button";
    String RES_HIT = "hit";

    String getName();
    ScenarioSwitcher getSwitcher();
    boolean getActivated();
    void setActivated(boolean a);

    void activate();
    void deactivate();
    void drawFrame(Graphics2D g2);

    void printError(String str, Exception ex);
    void printWarn(String str, Exception ex);
    void printInfo(String str, Exception ex);
}
