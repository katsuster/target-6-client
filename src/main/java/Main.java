package main.java;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.awt.*;
import javax.swing.*;
import com.github.hypfvieh.bluetooth.DeviceManager;
import org.freedesktop.dbus.exceptions.DBusException;

import net.katsuster.ble.BTScanWindow;
import net.katsuster.ble.LogWindow;
import net.katsuster.ble.MainWindow;
import net.katsuster.scenario.OpeningScenario;
import net.katsuster.scenario.ScenarioSwitcher;

public class Main {
    public static void main(String[] args) {
        Font uiFontBase, uiFont;
        boolean setting = false, normal = false;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-h")) {
                printUsage();
                return;
            } else if (args[0].equalsIgnoreCase("-s")) {
                setting = true;
            } else {
                normal = true;
            }
        } else {
            normal = true;
        }

        try {
            InputStream is = Main.class.getResourceAsStream("/openfont/ZenMaruGothic-Medium.ttf");
            if (is == null) {
                System.err.println("Error: Cannot found Font resource.");
                return;
            }
            uiFontBase = Font.createFont(Font.TRUETYPE_FONT, is);
            is.close();
        } catch (IOException ex) {
            System.err.println("Error: Cannot load Font.");
            System.err.println("  msg:" + ex.getMessage());
            return;
        } catch (FontFormatException ex) {
            System.err.println("Error: Font data is invalid or broken.");
            System.err.println("  msg:" + ex.getMessage());
            return;
        }

        uiFont = uiFontBase.deriveFont(Font.PLAIN, 12);
        setUIDefaultFont(uiFont);

        try {
            DeviceManager.createInstance(false);
        } catch (DBusException ex) {
            System.err.println("Error: Cannot create Bluetooth device manager instance.");
            System.err.println("  msg:" + ex.getMessage());
            return;
        }

        try {
            if (setting) {
                BTScanWindow w = new BTScanWindow();
                w.setVisible(true);
            }
            if (normal) {
                MainWindow mw = new MainWindow();
                mw.setVisible(true);
                LogWindow lw = new LogWindow();
                lw.setVisible(true);

                ScenarioSwitcher sw = new ScenarioSwitcher(mw, lw);
                sw.setNextScenario(new OpeningScenario(sw));

                Thread scenarioThread = new Thread(sw);
                scenarioThread.start();
                scenarioThread.join();
                mw.dispatchEvent(new WindowEvent(mw, WindowEvent.WINDOW_CLOSING));
            }
        } catch (HeadlessException ex) {
            System.err.println("Error: Cannot show window.");
            System.err.println("  msg:" + ex.getMessage());
            return;
        } catch (InterruptedException ex) {
            System.err.println("Error: Scenario is aborted.");
            System.err.println("  msg:" + ex.getMessage());
            return;
        }
    }

    public static void printUsage() {
        System.out.print("Usage:\n" +
                "  application [-h|-s]\n\n" +
                "Options:\n" +
                "  -h: Show this help.\n" +
                "  -s: Open setting window.\n");
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
