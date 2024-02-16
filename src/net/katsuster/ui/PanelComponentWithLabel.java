package net.katsuster.ui;

import java.awt.*;
import javax.swing.*;

public class PanelComponentWithLabel extends JPanel {
    public PanelComponentWithLabel() {

    }

    public void addComponentWithLabel(String label, JTextField txt) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(txt, BorderLayout.CENTER);

        add(panel);
    }
}
