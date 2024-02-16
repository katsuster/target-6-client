package net.katsuster.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class BTScanWindow extends JFrame {
    public static final String ACT_CLOSE = "Close";

    private int fontSize = 16;

    public BTScanWindow() {
        //Panels
        JPanel panelScan = new BTScanPanel();

        //Buttons
        ActionButton actButton = new ActionButton(this);
        JButton buttonClose = new JButton(ACT_CLOSE);
        buttonClose.addActionListener(actButton);

        JPanel panelButton = new JPanel();
        panelButton.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelButton.add(buttonClose);

        //Layout
        JPanel panelMain = new JPanel();
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.PAGE_AXIS));
        panelMain.add(panelScan);
        panelMain.add(panelButton);

        setTitle("Settings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        add(panelMain);
    }

    private class ActionButton implements ActionListener {
        BTScanWindow wnd;

        public ActionButton(BTScanWindow w) {
            wnd = w;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String cmd = ev.getActionCommand();

            if (cmd.equalsIgnoreCase(ACT_CLOSE)) {
                actionClose();
            }
        }

        public void actionClose() {
            wnd.dispatchEvent(new WindowEvent(wnd, WindowEvent.WINDOW_CLOSING));
        }
    }
}
