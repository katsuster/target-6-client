package net.katsuster.scenario;

import java.awt.*;

public interface Scenario {
    String CODE_NAME = "target-6-client";
    String CODE_VERSION = "v1.0-rc3";
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
    int FONT_SIZE_TIMER_RESULT = 192;
    int FONT_SIZE_DETAIL = 36;
    int FONT_SIZE_LARGEST = 96;
    int FONT_SIZE_LARGE = 64;
    int FONT_SIZE_MEDIUM = 48;
    int FONT_SIZE_SMALL = 32;
    int FONT_SIZE_SMALLEST = 16;

    int FPS_TIMER = 30;
    int FPS_RESULT = 3;

    String CMD_INIT = "init";
    String CMD_SINGLE = "single";
    String CMD_BEEP = "beep";
    String CMD_BLINK = "blink";
    String CMD_LED_ON = "ledon";
    String CMD_LED_OFF = "ledoff";
    String CMD_CNTUP = "cntup";
    String CMD_SSHOT = "sshot";
    String CMD_TATK = "tatk";
    String RES_BUTTON = "button";
    String RES_HIT = "hit";

    int SENSORS_DEFAULT = 6;

    String SCENARIO_PARENT = "<< Back";
    String SCENARIO_SEPARATOR = "・";

    String SCENARIO_COUNT_UP = "Count Up";
    String SCENARIO_COUNT_UP_PRE = "CountUp";
    String SCENARIO_COUNT_UP_PARENT = SCENARIO_COUNT_UP + "/" + SCENARIO_PARENT;
    String SCENARIO_COUNT_UP_30SEC = SCENARIO_COUNT_UP + "/30 sec";
    String SCENARIO_COUNT_UP_20SEC = SCENARIO_COUNT_UP + "/20 sec";
    String SCENARIO_COUNT_UP_15SEC = SCENARIO_COUNT_UP + "/15 sec";

    String SCENARIO_SPEED_SHOOT = "Speed Shoot";
    String SCENARIO_SPEED_SHOOT_PRE = "SpeedShoot";
    String SCENARIO_SPEED_SHOOT_PARENT = SCENARIO_SPEED_SHOOT + "/" + SCENARIO_PARENT;
    String SCENARIO_SPEED_SHOOT_6 = SCENARIO_SPEED_SHOOT + "/6 Targets";
    String SCENARIO_SPEED_SHOOT_5 = SCENARIO_SPEED_SHOOT + "/5 Targets";
    String SCENARIO_SPEED_SHOOT_4 = SCENARIO_SPEED_SHOOT + "/4 Targets";

    String SCENARIO_TIME_ATTACK = "Time Attack";
    String SCENARIO_TIME_ATTACK_PRE = "Time Attack";
    String SCENARIO_TIME_ATTACK_PARENT = SCENARIO_TIME_ATTACK + "/" + SCENARIO_PARENT;
    String SCENARIO_TIME_ATTACK_6 = SCENARIO_TIME_ATTACK + "/6 Targets";
    String SCENARIO_TIME_ATTACK_5 = SCENARIO_TIME_ATTACK + "/5 Targets";
    String SCENARIO_TIME_ATTACK_4 = SCENARIO_TIME_ATTACK + "/4 Targets";

    String SCENARIO_RANKING = "Ranking";
    String SCENARIO_RANKING_PARENT = SCENARIO_RANKING + "/" + SCENARIO_PARENT;
    String SCENARIO_RANKING_COUNT_UP = SCENARIO_RANKING + "/Count Up";
    String SCENARIO_RANKING_SPEED_SHOOT = SCENARIO_RANKING + "/Speed Shoot";
    String SCENARIO_RANKING_TIME_ATTACK = SCENARIO_RANKING + "/Time Attack";

    enum SCORE_TYPE {
        SCORE_COUNT_UP_30 {
            @Override
            public String toDisplay() {
                return SCENARIO_COUNT_UP_30SEC;
            }
            public String toString() {
                return SCENARIO_COUNT_UP_PRE + "30";
            }
        },
        SCORE_COUNT_UP_20 {
            @Override
            public String toDisplay() {
                return SCENARIO_COUNT_UP_20SEC;
            }
            public String toString() {
                return SCENARIO_COUNT_UP_PRE + "20";
            }
        },
        SCORE_COUNT_UP_15 {
            @Override
            public String toDisplay() {
                return SCENARIO_COUNT_UP_15SEC;
            }
            public String toString() {
                return SCENARIO_COUNT_UP_PRE + "15";
            }
        },
        SCORE_SPEED_SHOOT_6 {
            @Override
            public String toDisplay() {
                return SCENARIO_SPEED_SHOOT_6;
            }
            public String toString() {
                return SCENARIO_SPEED_SHOOT_PRE + "6";
            }
        },
        SCORE_SPEED_SHOOT_5 {
            @Override
            public String toDisplay() {
                return SCENARIO_SPEED_SHOOT_5;
            }
            public String toString() {
                return SCENARIO_SPEED_SHOOT_PRE + "5";
            }
        },
        SCORE_SPEED_SHOOT_4 {
            @Override
            public String toDisplay() {
                return SCENARIO_SPEED_SHOOT_4;
            }
            public String toString() {
                return SCENARIO_SPEED_SHOOT_PRE + "4";
            }
        },
        SCORE_TIME_ATTACK_6 {
            @Override
            public String toDisplay() {
                return SCENARIO_TIME_ATTACK_6;
            }
            public String toString() {
                return SCENARIO_TIME_ATTACK_PRE + "6";
            }
        },
        SCORE_TIME_ATTACK_5 {
            @Override
            public String toDisplay() {
                return SCENARIO_TIME_ATTACK_5;
            }
            public String toString() {
                return SCENARIO_TIME_ATTACK_PRE + "5";
            }
        },
        SCORE_TIME_ATTACK_4 {
            @Override
            public String toDisplay() {
                return SCENARIO_TIME_ATTACK_4;
            }
            public String toString() {
                return SCENARIO_TIME_ATTACK_PRE + "4";
            }
        },
        SCORE_TIME_ATTACK_TEST {
            @Override
            public String toDisplay() {
                return SCENARIO_TIME_ATTACK + "/test";
            }
            public String toString() {
                return SCENARIO_TIME_ATTACK_PRE + "Test";
            }
        },
        SCORE_EMPTY {
            @Override
            public String toDisplay() {
                return "Empty";
            }
            public String toString() {
                return "Empty";
            }
        };

        abstract String toDisplay();
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
