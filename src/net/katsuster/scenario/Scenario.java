package net.katsuster.scenario;

import java.awt.*;

public interface Scenario {
    String CODE_NAME = "Target-6";
    String CODE_VERSION = "v0.1";
    String CODE_TITLE_WORD1 = "Speed Shooting";
    String CODE_TITLE_WORD2 = "Scoreboard";

    Color COLOR_BG_GRAY = new Color(240, 240, 240);
    Color COLOR_LIGHT_BLUE = new Color(192, 192, 255);
    Color COLOR_DARK_BLUE = new Color(40, 85, 160);
    Color COLOR_DARK_ORANGE = new Color(247, 119, 15);

    int DEV_CONTROLLER = 0;
    int DEV_SINGLE = 1;

    int FONT_SIZE_TITLE = 96;
    int FONT_SIZE_TIMER = 272;
    int FONT_SIZE_DETAIL = 36;
    int FONT_SIZE_LARGEST = 96;
    int FONT_SIZE_LARGE = 64;
    int FONT_SIZE_MEDIUM = 48;
    int FONT_SIZE_SMALL = 32;
    int FONT_SIZE_SMALLEST = 16;

    String CMD_INIT = "init";
    String CMD_SINGLE = "single";
    String CMD_BEEP = "beep";
    String CMD_CNTUP = "cntup";
    String CMD_TATK = "tatk";
    String RES_BUTTON = "button";
    String RES_HIT = "hit";

    int SENSORS_DEFAULT = 6;

    enum SCORE_TYPE {
        SCORE_EMPTY {
            public String toString() {
                return "Empty";
            }
        },
        SCORE_COUNT_UP {
            public String toString() {
                return "CountUp";
            }
        },
        SCORE_TIME_ATTACK {
            public String toString() {
                return "TimeAttack";
            }
        },
        SCORE_TIME_ATTACK_TEST {
            public String toString() {
                return "TimeAttackTest";
            }
        },
    }

    String getName();
    SCORE_TYPE getScoreType();
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
