package net.katsuster.ble;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import org.freedesktop.dbus.exceptions.DBusException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainWindow extends JFrame {
    private int fontSize = 16;

    public MainWindow() {
        //Button listener
        ActionButton actButton = new ActionButton(this);

        //Panels
        JPanel panelScan = new BTScanPanel();

        //Buttons
        JButton buttonClose = new JButton("close");
        buttonClose.addActionListener(actButton);


        JPanel panelMain = new JPanel();
        panelMain.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelMain.add(panelScan);
        panelMain.add(buttonClose);

        setTitle("Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setUndecorated(true);
        setSize(1024, 768);
        add(panelMain);
    }

    public class ActionButton implements ActionListener {
        MainWindow wndMain;

        public ActionButton(MainWindow wm) {
            wndMain = wm;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String cmd = ev.getActionCommand();

            if (cmd.equalsIgnoreCase("close")) {
                wndMain.dispatchEvent(new WindowEvent(wndMain, WindowEvent.WINDOW_CLOSING));
            }
            if (cmd.equalsIgnoreCase("start")) {
                actionStart();
            }
        }

        public void actionStart() {

        }
    }
}
