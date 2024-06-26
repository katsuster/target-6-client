package net.katsuster.ui;

import java.awt.*;
import javax.swing.*;

public class LogWindow extends JFrame {
    private JTextArea logGame;

    public LogWindow() {
        //Log
        logGame = new JTextArea();
        logGame.setEditable(false);
        JScrollPane scrLogGame = new JScrollPane(logGame);
        scrLogGame.setBorder(BorderFactory.createLoweredBevelBorder());

        //Layout
        JPanel panelGame = new JPanel();
        panelGame.setLayout(new BorderLayout());
        panelGame.add(scrLogGame, BorderLayout.CENTER);

        setTitle("Log");
        setSize(400, 400);
        add(panelGame);
    }

    public JTextArea getLogGame() {
        return logGame;
    }
}
