package net.katsuster.ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;

public class BTDeviceSubPanel extends JPanel {
    public static final String ACT_USE_DEVICE = "Use Device";
    public static final String ACT_SERVICE = "Service";

    private BTScanPanel parent;
    private JButton buttonUseDevice;
    private JButton buttonService;
    private SpinnerNumberModel modelDeviceTimeout;
    private JTextField statusDevice;

    public BTDeviceSubPanel(BTScanPanel p) {
        parent = p;

        Dimension preferredListSize = new Dimension(250, (int)(parent.getFontSize() * 1.3 * 8));

        //Status
        statusDevice = new JTextField();
        statusDevice.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        statusDevice.setEditable(false);

        //Buttons
        ActionButton actButton = new ActionButton(this);
        buttonUseDevice = new JButton(ACT_USE_DEVICE);
        buttonUseDevice.addActionListener(actButton);
        buttonUseDevice.setEnabled(false);

        buttonService = new JButton(ACT_SERVICE);
        buttonService.addActionListener(actButton);
        buttonService.setEnabled(false);

        JPanel panelBtnService = new JPanel();
        panelBtnService.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnService.add(buttonUseDevice);
        panelBtnService.add(buttonService);

        //Lists
        JList<BTDeviceItem> listBTDevice = new JList<>(parent.getModelBTDevice());
        listBTDevice.addListSelectionListener(new DeviceSelection(this, listBTDevice));
        JScrollPane scrListBTDevice = new JScrollPane(listBTDevice);
        scrListBTDevice.setPreferredSize(preferredListSize);

        //Timeout
        JPanel panelDeviceTimeout = new JPanel();
        modelDeviceTimeout = new SpinnerNumberModel(3, 1, 10, 1);
        JSpinner spinnerDeviceTimeout = new JSpinner(modelDeviceTimeout);
        panelDeviceTimeout.setLayout(new BorderLayout());
        panelDeviceTimeout.add(new JLabel("Timeout[sec]: "), BorderLayout.WEST);
        panelDeviceTimeout.add(spinnerDeviceTimeout, BorderLayout.CENTER);

        //Layout
        JPanel pBTDContent = new JPanel();
        pBTDContent.setLayout(new BoxLayout(pBTDContent, BoxLayout.PAGE_AXIS));
        pBTDContent.add(scrListBTDevice);
        pBTDContent.add(panelDeviceTimeout);
        pBTDContent.add(panelBtnService);
        pBTDContent.add(statusDevice);
        LayoutPanel pBTD = new LayoutPanel();
        pBTD.setPadding(2, 5, 2, 5);
        pBTD.setContentBorder(BorderFactory.createTitledBorder("Device"));
        pBTD.setContent(pBTDContent);

        add(pBTD);
    }

    private DefaultListModel<GattServiceItem> getModelGattService() {
        return parent.getModelGattService();
    }

    private BluetoothDevice getBluetoothDevice() {
        return parent.getBluetoothDevice();
    }

    private void setBluetoothDevice(BluetoothDevice dev) {
        parent.setBluetoothDevice(dev);
    }

    private String getTextBTDeviceAddress() {
        return parent.getTextBTDeviceAddress();
    }

    private void setTextBTDeviceAddress(String t) {
        parent.setTextBTDeviceAddress(t);
    }

    public boolean getDeviceEnabled() {
        return buttonService.isEnabled();
    }

    public void setDeviceEnabled(boolean b) {
        buttonUseDevice.setEnabled(b);
        buttonService.setEnabled(b);
    }

    public int getDeviceTimeout() {
        return modelDeviceTimeout.getNumber().intValue();
    }

    public void setStatusTextDevice(String txt) {
        statusDevice.setText(txt);
        statusDevice.setCaretPosition(0);
    }

    private class DeviceSelection implements ListSelectionListener {
        BTDeviceSubPanel panel;
        JList<BTDeviceItem> list;

        public DeviceSelection(BTDeviceSubPanel p, JList<BTDeviceItem> l) {
            panel = p;
            list = l;
        }

        @Override
        public void valueChanged(ListSelectionEvent ev) {
            panel.getModelGattService().clear();
            panel.setDeviceEnabled(false);
            panel.setStatusTextDevice("");

            if (list.isSelectionEmpty()) {
                return;
            }

            BluetoothDevice dev = list.getModel().getElementAt(list.getSelectedIndex()).getBluetoothDevice();
            panel.setBluetoothDevice(dev);
            panel.setDeviceEnabled(true);
        }
    }

    private class ActionButton implements ActionListener {
        BTDeviceSubPanel panel;

        public ActionButton(BTDeviceSubPanel p) {
            panel = p;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String cmd = ev.getActionCommand();

            if (cmd.equalsIgnoreCase(ACT_USE_DEVICE)) {
                actionUseDevice();
            }
            if (cmd.equalsIgnoreCase(ACT_SERVICE)) {
                actionScanService();
            }
        }

        public void actionUseDevice() {
            BluetoothDevice device = panel.getBluetoothDevice();

            panel.setTextBTDeviceAddress(device.getAddress());
        }

        public void actionScanService() {
            DefaultListModel<GattServiceItem> listGattService = panel.getModelGattService();
            BluetoothDevice device = panel.getBluetoothDevice();
            AtomicBoolean finish = new AtomicBoolean(false);
            int retry;

            listGattService.clear();

            panel.setStatusTextDevice("Searching service...");
            Thread t = new Thread(() -> {
                device.connect();
                finish.set(true);
            });
            t.start();

            retry = 0;
            while (!finish.get()) {
                System.out.println("connect...");
                if (retry >= panel.getDeviceTimeout() * 5) {
                    t.interrupt();
                    break;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    //ignore
                }
                retry += 1;
            }
            if (!finish.get()) {
                System.err.println("Error: Cannot connect to bluetooth device.");
                panel.setStatusTextDevice("Cannot connect to BT device.");
                return;
            }

            device.refreshGattServices();
            List<BluetoothGattService> gattServices = device.getGattServices();
            retry = 0;
            while (gattServices.isEmpty()) {
                System.out.println("search GATT...");
                if (retry >= panel.getDeviceTimeout() * 5) {
                    break;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    //ignore
                }
                retry += 1;

                device.refreshGattServices();
                gattServices = device.getGattServices();
            }

            device.disconnect();

            if (gattServices.isEmpty()) {
                System.err.println("Error: There is no bluetooth GATT service.");
                panel.setStatusTextDevice("No bluetooth GATT service.");
                return;
            }
            for (BluetoothGattService srv : gattServices) {
                listGattService.addElement(new GattServiceItem(srv));
            }

            panel.setStatusTextDevice(gattServices.size() + " service(s) found.");
        }
    }
}
