package main.java;

import java.awt.event.WindowEvent;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;

import org.freedesktop.dbus.exceptions.DBusException;
import com.github.hypfvieh.bluetooth.DeviceManager;

import net.katsuster.scenario.*;
import net.katsuster.ui.BTScanWindow;
import net.katsuster.ui.LogWindow;
import net.katsuster.ui.MainWindow;

public class Main {
    public static String OPT_HELP = "-h";
    public static String OPT_DEBUG = "--debug";
    public static String OPT_SETTING_BT = "--sb";

    public static void main(String[] args) {
        Font fontUI, fontBase, fontMono;
        boolean runSettingBT = false, runDebug = false, runNormal = false;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(OPT_HELP)) {
                printUsage();
                return;
            } else if (args[0].equalsIgnoreCase(OPT_DEBUG)) {
                runDebug = true;
            } else if (args[0].equalsIgnoreCase(OPT_SETTING_BT)) {
                runSettingBT = true;
            } else {
                runNormal = true;
            }
        } else {
            runNormal = true;
        }

        try {
            InputStream is = Main.class.getResourceAsStream("/openfont/ZenMaruGothic-Medium.ttf");
            if (is == null) {
                System.err.println("Error: Cannot found UI Font resource.");
                return;
            }
            fontUI = Font.createFont(Font.TRUETYPE_FONT, is);
            is.close();
        } catch (IOException ex) {
            printException("Error: Cannot load UI Font.", ex);
            return;
        } catch (FontFormatException ex) {
            printException("Error: UI Font data is invalid or broken.", ex);
            return;
        }

        try {
            InputStream is = Main.class.getResourceAsStream("/openfont/KosugiMaru-Regular.ttf");
            if (is == null) {
                System.err.println("Error: Cannot found Monospace Font resource.");
                return;
            }
            fontBase = Font.createFont(Font.TRUETYPE_FONT, is);
            is.close();
        } catch (IOException ex) {
            printException("Error: Cannot load Monospace Font.", ex);
            return;
        } catch (FontFormatException ex) {
            printException("Error: Monospace Font data is invalid or broken.", ex);
            return;
        }

        fontMono = fontBase.deriveFont(Font.PLAIN, 12);
        setUIDefaultFont(fontMono);

        try {
            DeviceManager.createInstance(false);
        } catch (DBusException ex) {
            printException("Error: Cannot create Bluetooth device manager instance.", ex);
            return;
        }

        try {
            if (runSettingBT) {
                BTScanWindow w = new BTScanWindow();
                w.setVisible(true);
            }
            if (runNormal || runDebug) {
                MainWindow mw = new MainWindow(runDebug);
                mw.setVisible(true);
                mw.createBufferStrategy(2);
                LogWindow lw = new LogWindow();
                if (runDebug) {
                    lw.setVisible(true);
                }

                ScenarioSetting settingScenario = new ScenarioSetting();
                settingScenario.setFontUI(fontUI);
                settingScenario.setFontMono(fontMono);

                ScenarioSwitcher sw = new ScenarioSwitcher(settingScenario, mw, lw);
                sw.setNextScenario(new OpeningScenario(sw));

                Thread scenarioThread = new Thread(sw);
                scenarioThread.start();
                scenarioThread.join();
                mw.dispatchEvent(new WindowEvent(mw, WindowEvent.WINDOW_CLOSING));
            }
        } catch (HeadlessException ex) {
            printException("Error: Cannot show window.", ex);
            return;
        } catch (InterruptedException ex) {
            printException("Error: Scenario is aborted.", ex);
            return;
        }
    }

    public static void printUsage() {
        System.out.print(String.format("Usage:\n" +
                "  application [%s|%s]\n\n" +
                "Options:\n" +
                "  %4s: Show this help.\n" +
                "  %4s: Open Bluetooth device setting.\n",
                OPT_HELP, OPT_SETTING_BT,
                OPT_HELP, OPT_SETTING_BT));
    }

    public static void printException(String msg, Exception ex) {
        System.err.println(msg);
        System.err.println("  msg:" + ex.getMessage());
        ex.printStackTrace(System.err);
    }

    public static void setUIDefaultFont(Font f) {
        UIManager.put("Button.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("CheckBoxMenuItem.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("EditorPane.font", f);
        UIManager.put("FormattedTextField.font", f);
        UIManager.put("IconButton.font", f);
        UIManager.put("Label.font", f);
        UIManager.put("List.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("OptionPane.font", f);
        UIManager.put("Panel.font", f);
        UIManager.put("PasswordField.font", f);
        UIManager.put("PopupMenu.font", f);
        UIManager.put("ProgressBar.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("RadioButtonMenuItem.font", f);
        UIManager.put("ScrollPane.font", f);
        UIManager.put("Slider.font", f);
        UIManager.put("Spinner.font", f);
        UIManager.put("TabbedPane.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TextPane.font", f);
        UIManager.put("TitledBorder.font", f);
        UIManager.put("ToggleButton.font", f);
        UIManager.put("ToolBar.font", f);
        UIManager.put("ToolTip.font", f);
        UIManager.put("Tree.font", f);
        UIManager.put("Viewport.font", f);
    }
}
