package net.katsuster.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;

public class MainWindow extends JFrame {
    private boolean debugMode = false;

    public MainWindow(boolean dbg) {
        setTitle("Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setDebugMode(dbg);
    }

    public boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean d) {
        if (!d) {
            setUndecorated(true);
            setAlwaysOnTop(true);
        }
        debugMode = d;
    }
}
