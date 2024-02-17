package net.katsuster.scenario;

import java.awt.*;
import java.util.prefs.Preferences;

import net.katsuster.ui.MainWindow;

public class ScenarioSetting {
    public static String SETTING_HOST_NAME = "HostName";

    private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

    public ScenarioSetting() {
        //do nothing
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font f) {
        font = f;
    }

    public static String getSetting(String key) {
        Preferences prefs = Preferences.userNodeForPackage(MainWindow.class);

        return prefs.get(key, "");
    }

    public static void putSetting(String key, String value) {
        Preferences prefs = Preferences.userNodeForPackage(MainWindow.class);

        prefs.put(key, value);
    }
}
