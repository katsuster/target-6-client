package net.katsuster.scenario;

import java.awt.*;

public interface Scenario {
    String CODE_NAME = "target-6-client";
    String CODE_VERSION = "v1.0-rc1";
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

    String SCENARIO_COUNT_UP = "Count Up";
    String SCENARIO_COUNT_UP_30SEC = SCENARIO_COUNT_UP + "/30 sec";
    String SCENARIO_COUNT_UP_20SEC = SCENARIO_COUNT_UP + "/20 sec";
    String SCENARIO_COUNT_UP_15SEC = SCENARIO_COUNT_UP + "/15 sec";
    String SCENARIO_TIME_ATTACK = "Time Attack";
    String SCENARIO_TIME_ATTACK_6 = SCENARIO_TIME_ATTACK + "/6 Targets";
    String SCENARIO_TIME_ATTACK_5 = SCENARIO_TIME_ATTACK + "/5 Targets";
    String SCENARIO_TIME_ATTACK_4 = SCENARIO_TIME_ATTACK + "/4 Targets";
    String SCENARIO_RANKING = "Ranking";
    String SCENARIO_SEPARATOR = "・";

    enum SCORE_TYPE {
        SCORE_EMPTY {
            public String toString() {
                return "Empty";
            }
        },
        SCORE_COUNT_UP_30 {
            public String toString() {
                return "CountUp 30";
            }
        },
        SCORE_COUNT_UP_20 {
            public String toString() {
                return "CountUp 20";
            }
        },
        SCORE_COUNT_UP_15 {
            public String toString() {
                return "CountUp 15";
            }
        },
        SCORE_TIME_ATTACK_6 {
            public String toString() {
                return "TimeAttack 6";
            }
        },
        SCORE_TIME_ATTACK_5 {
            public String toString() {
                return "TimeAttack 5";
            }
        },
        SCORE_TIME_ATTACK_4 {
            public String toString() {
                return "TimeAttack 4";
            }
        },
        SCORE_TIME_ATTACK_TEST {
            public String toString() {
                return "TimeAttackTest";
            }
        },
    }

    static SCORE_TYPE toScoreType(String scr) {
        switch (scr) {
        case SCENARIO_COUNT_UP_30SEC:
            return SCORE_TYPE.SCORE_COUNT_UP_30;
        case SCENARIO_COUNT_UP_20SEC:
            return SCORE_TYPE.SCORE_COUNT_UP_20;
        case SCENARIO_COUNT_UP_15SEC:
            return SCORE_TYPE.SCORE_COUNT_UP_15;
        case SCENARIO_TIME_ATTACK_6:
            return SCORE_TYPE.SCORE_TIME_ATTACK_6;
        case SCENARIO_TIME_ATTACK_5:
            return SCORE_TYPE.SCORE_TIME_ATTACK_5;
        case SCENARIO_TIME_ATTACK_4:
            return SCORE_TYPE.SCORE_TIME_ATTACK_4;
        case SCENARIO_RANKING:
        case SCENARIO_SEPARATOR:
        default:
            return SCORE_TYPE.SCORE_EMPTY;
        }
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
