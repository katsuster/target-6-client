package net.katsuster.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;

public class BTServiceSubPanel extends JPanel {
    public static final String ACT_USE_SERVICE = "Use Service";
    public static final String ACT_CHARACTERISTIC = "Characteristic";

    private BTScanPanel parent;
    private JButton buttonUseService;
    private JButton buttonCharacteristic;
    private JTextField statusService;

    public BTServiceSubPanel(BTScanPanel p) {
        parent = p;

        Dimension preferredListSize = new Dimension(250, (int)(parent.getFontSize() * 1.3 * 8));

        //Status
        statusService = new JTextField();
        statusService.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        statusService.setEditable(false);

        //Buttons
        ActionButton actButton = new ActionButton(this);
        buttonUseService = new JButton(ACT_USE_SERVICE);
        buttonUseService.addActionListener(actButton);
        buttonUseService.setEnabled(false);

        buttonCharacteristic = new JButton(ACT_CHARACTERISTIC);
        buttonCharacteristic.addActionListener(actButton);
        buttonCharacteristic.setEnabled(false);

        JPanel panelBtnCharacteristic = new JPanel();
        panelBtnCharacteristic.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnCharacteristic.add(buttonUseService);
        panelBtnCharacteristic.add(buttonCharacteristic);


        //Lists
        JList<GattServiceItem> listGattService = new JList<>(parent.getModelGattService());
        listGattService.addListSelectionListener(new ServiceSelection(this, listGattService));
        JScrollPane scrListGattService = new JScrollPane(listGattService);
        scrListGattService.setPreferredSize(preferredListSize);

        //Layout
        JPanel pSRVContent = new JPanel();
        pSRVContent.setLayout(new BoxLayout(pSRVContent, BoxLayout.PAGE_AXIS));
        pSRVContent.add(scrListGattService);
        pSRVContent.add(panelBtnCharacteristic);
        pSRVContent.add(statusService);
        LayoutPanel pSRV = new LayoutPanel();
        pSRV.setPadding(2, 5, 2, 5);
        pSRV.setContentBorder(BorderFactory.createTitledBorder("GATT Service"));
        pSRV.setContent(pSRVContent);

        add(pSRV);
    }

    private DefaultListModel<GattCharacteristicItem> getModelGattCharacteristic() {
        return parent.getModelGattCharacteristic();
    }

    private BluetoothGattService getBluetoothGattService() {
        return parent.getBluetoothGattService();
    }

    private void setBluetoothGattService(BluetoothGattService srv) {
        parent.setBluetoothGattService(srv);
    }

    private String getTextBTGattServiceUuid() {
        return parent.getTextBTGattServiceUuid();
    }

    private void setTextBTGattServiceUuid(String t) {
        parent.setTextBTGattServiceUuid(t);
    }

    public boolean getServiceEnabled() {
        return buttonCharacteristic.isEnabled();
    }

    public void setServiceEnabled(boolean b) {
        buttonUseService.setEnabled(b);
        buttonCharacteristic.setEnabled(b);
    }

    public void setStatusTextService(String txt) {
        statusService.setText(txt);
        statusService.setCaretPosition(0);
    }

    private class ServiceSelection implements ListSelectionListener {
        BTServiceSubPanel panel;
        JList<GattServiceItem> list;

        public ServiceSelection(BTServiceSubPanel p, JList<GattServiceItem> l) {
            panel = p;
            list = l;
        }

        @Override
        public void valueChanged(ListSelectionEvent ev) {
            panel.getModelGattCharacteristic().clear();
            panel.setServiceEnabled(false);
            panel.setStatusTextService("");

            if (list.isSelectionEmpty()) {
                return;
            }

            BluetoothGattService srv = list.getModel().getElementAt(list.getSelectedIndex()).getBluetoothGattService();
            panel.setBluetoothGattService(srv);
            panel.setServiceEnabled(true);
        }
    }

    private class ActionButton implements ActionListener {
        BTServiceSubPanel panel;

        public ActionButton(BTServiceSubPanel p) {
            panel = p;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String cmd = ev.getActionCommand();

            if (cmd.equalsIgnoreCase(ACT_USE_SERVICE)) {
                actionUseService();
            }
            if (cmd.equalsIgnoreCase(ACT_CHARACTERISTIC)) {
                actionScanCharacteristic();
            }
        }

        public void actionUseService() {
            BluetoothGattService service = panel.getBluetoothGattService();

            panel.setTextBTGattServiceUuid(service.getUuid());
        }

        public void actionScanCharacteristic() {
            DefaultListModel<GattCharacteristicItem> listGattCharacteristic = panel.getModelGattCharacteristic();
            BluetoothGattService srv = panel.getBluetoothGattService();

            listGattCharacteristic.clear();

            panel.setStatusTextService("Searching characteristics...");
            srv.refreshGattCharacteristics();
            List<BluetoothGattCharacteristic> gattCharacteristics = srv.getGattCharacteristics();
            if (gattCharacteristics.isEmpty()) {
                System.err.println("Error: There is no bluetooth GATT characteristic.");
                panel.setStatusTextService("No bluetooth GATT characteristic.");
                return;
            }
            for (BluetoothGattCharacteristic chr : gattCharacteristics) {
                listGattCharacteristic.addElement(new GattCharacteristicItem(chr));
            }

            panel.setStatusTextService(gattCharacteristics.size() + " characteristic(s) found.");
        }
    }
}
