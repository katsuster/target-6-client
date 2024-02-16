package net.katsuster.ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;

public class BTCharacteristicSubPanel extends JPanel {
    public static final String ACT_USE_TX = "Use Tx";
    public static final String ACT_USE_RX = "Use Rx";

    private BTScanPanel parent;
    private JButton buttonUseTx;
    private JButton buttonUseRx;

    public BTCharacteristicSubPanel(BTScanPanel p) {
        parent = p;

        Dimension preferredListSize = new Dimension(250, (int)(parent.getFontSize() * 1.3 * 8));

        //Buttons
        ActionButton actButton = new ActionButton(this);
        buttonUseTx = new JButton(ACT_USE_TX);
        buttonUseTx.addActionListener(actButton);
        buttonUseTx.setEnabled(false);

        buttonUseRx = new JButton(ACT_USE_RX);
        buttonUseRx.addActionListener(actButton);
        buttonUseRx.setEnabled(false);

        JPanel panelBtnTxRx = new JPanel();
        panelBtnTxRx.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnTxRx.add(buttonUseTx);
        panelBtnTxRx.add(buttonUseRx);

        //Lists
        JList<GattCharacteristicItem> listGattCharacteristic = new JList<>(parent.getModelGattCharacteristic());
        listGattCharacteristic.addListSelectionListener(new CharacteristicSelection(this, listGattCharacteristic));
        JScrollPane scrListGattCharacteristic = new JScrollPane(listGattCharacteristic);
        scrListGattCharacteristic.setPreferredSize(preferredListSize);

        //Layout
        JPanel pCHRContent = new JPanel();
        pCHRContent.setLayout(new BoxLayout(pCHRContent, BoxLayout.PAGE_AXIS));
        pCHRContent.add(scrListGattCharacteristic);
        pCHRContent.add(panelBtnTxRx);
        LayoutPanel pCHR = new LayoutPanel();
        pCHR.setPadding(2, 5, 2, 5);
        pCHR.setContentBorder(BorderFactory.createTitledBorder("GATT Characteristic"));
        pCHR.setContent(pCHRContent);

        add(pCHR);
    }

    private BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        return parent.getBluetoothGattCharacteristic();
    }

    private void setBluetoothGattCharacteristic(BluetoothGattCharacteristic chr) {
        parent.setBluetoothGattCharacteristic(chr);
    }

    private String getTextBTGattTxUuid() {
        return parent.getTextBTGattTxUuid();
    }

    private void setTextBTGattTxUuid(String t) {
        parent.setTextBTGattTxUuid(t);
    }

    private String getTextBTGattRxUuid() {
        return parent.getTextBTGattRxUuid();
    }

    private void setTextBTGattRxUuid(String t) {
        parent.setTextBTGattRxUuid(t);
    }

    public boolean getCharacteristicEnabled() {
        return buttonUseRx.isEnabled();
    }

    public void setCharacteristicEnabled(boolean b) {
        buttonUseTx.setEnabled(b);
        buttonUseRx.setEnabled(b);
    }

    private class CharacteristicSelection implements ListSelectionListener {
        BTCharacteristicSubPanel panel;
        JList<GattCharacteristicItem> list;

        public CharacteristicSelection(BTCharacteristicSubPanel p, JList<GattCharacteristicItem> l) {
            panel = p;
            list = l;
        }

        @Override
        public void valueChanged(ListSelectionEvent ev) {
            panel.setCharacteristicEnabled(false);

            if (list.isSelectionEmpty()) {
                return;
            }

            BluetoothGattCharacteristic chr = list.getModel().getElementAt(list.getSelectedIndex()).getBluetoothGattCharacteristic();
            panel.setBluetoothGattCharacteristic(chr);
            panel.setCharacteristicEnabled(true);
        }
    }

    private class ActionButton implements ActionListener {
        BTCharacteristicSubPanel panel;

        public ActionButton(BTCharacteristicSubPanel p) {
            panel = p;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String cmd = ev.getActionCommand();

            if (cmd.equalsIgnoreCase(ACT_USE_TX)) {
                actionUseGattTx();
            }
            if (cmd.equalsIgnoreCase(ACT_USE_RX)) {
                actionUseGattRx();
            }
        }

        public void actionUseGattTx() {
            BluetoothGattCharacteristic chr = panel.getBluetoothGattCharacteristic();

            panel.setTextBTGattTxUuid(chr.getUuid());
        }

        public void actionUseGattRx() {
            BluetoothGattCharacteristic chr = panel.getBluetoothGattCharacteristic();

            panel.setTextBTGattRxUuid(chr.getUuid());
        }
    }
}
