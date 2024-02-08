package main.java;

import com.github.hypfvieh.bluetooth.DeviceManager;
import net.katsuster.ble.MainWindow;
import org.freedesktop.dbus.exceptions.DBusException;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            DeviceManager.createInstance(false);
        } catch (DBusException e) {
            System.err.println("Error: Cannot create Bluetooth device manager instance.");
            e.printStackTrace();
            return;
        }

        try {
            MainWindow w = new MainWindow();

            w.setVisible(true);
        } catch (HeadlessException ex) {
            System.err.println("Error: Cannot show window.");
        }
    }
}
