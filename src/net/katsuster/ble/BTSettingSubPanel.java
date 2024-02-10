package net.katsuster.ble;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BTSettingSubPanel extends JPanel {
    public static final String DEFAULT_UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String DEFAULT_UUID_RX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String DEFAULT_UUID_TX = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    public static final String ACT_SET_DEFAULT = "Default";

    private BTScanPanel parent;
    private JTextField textBTAdapterAddress;
    private JTextField textBTDeviceAddress;
    private JTextField textBTGattServiceUuid;
    private JTextField textBTGattTxUuid;
    private JTextField textBTGattRxUuid;

    public BTSettingSubPanel(BTScanPanel p) {
        parent = p;

        Dimension preferredListSize = new Dimension(200, (int)(parent.getFontSize() * 1.3 * 8));
        Dimension preferredTextSize = new Dimension(160, (int)(parent.getFontSize() * 1.3));

        //Buttons
        ActionButton actButton = new ActionButton(this);
        JButton buttonDefaultGATT = new JButton(ACT_SET_DEFAULT);
        buttonDefaultGATT.addActionListener(actButton);

        JPanel panelBtnSettings = new JPanel();
        panelBtnSettings.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnSettings.add(buttonDefaultGATT);

        //Settings
        textBTAdapterAddress = new JTextField();
        textBTAdapterAddress.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTAdapterAddress.setPreferredSize(preferredTextSize);

        textBTDeviceAddress = new JTextField();
        textBTDeviceAddress.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTDeviceAddress.setPreferredSize(preferredTextSize);

        textBTGattServiceUuid = new JTextField();
        textBTGattServiceUuid.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTGattServiceUuid.setPreferredSize(preferredTextSize);

        textBTGattTxUuid = new JTextField();
        textBTGattTxUuid.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTGattTxUuid.setPreferredSize(preferredTextSize);

        textBTGattRxUuid = new JTextField();
        textBTGattRxUuid.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTGattRxUuid.setPreferredSize(preferredTextSize);

        PanelComponentWithLabel panelTestSetting = new PanelComponentWithLabel();
        int gap = parent.getFontSize() / 3;
        panelTestSetting.setLayout(new GridLayout(5, 1, gap, gap));
        panelTestSetting.addComponentWithLabel("Adapter MAC: ", textBTAdapterAddress);
        panelTestSetting.addComponentWithLabel("Device MAC: ", textBTDeviceAddress);
        panelTestSetting.addComponentWithLabel("Service GATT: ", textBTGattServiceUuid);
        panelTestSetting.addComponentWithLabel("Tx GATT: ", textBTGattTxUuid);
        panelTestSetting.addComponentWithLabel("Rx GATT: ", textBTGattRxUuid);

        //Layout
        JPanel pSETContent = new JPanel();
        pSETContent.setLayout(new BoxLayout(pSETContent, BoxLayout.PAGE_AXIS));
        pSETContent.add(panelTestSetting);
        pSETContent.add(panelBtnSettings);
        LayoutPanel pSET = new LayoutPanel();
        pSET.setPadding(5);
        pSET.setContentBorder(BorderFactory.createTitledBorder("Test Settings"));
        pSET.setContent(pSETContent);

        add(pSET);
    }

    public String getTextBTAdapterAddress() {
        return textBTAdapterAddress.getText();
    }

    public void setTextBTAdapterAddress(String t) {
        textBTAdapterAddress.setText(t);
        textBTAdapterAddress.setCaretPosition(0);
    }

    public String getTextBTDeviceAddress() {
        return textBTDeviceAddress.getText();
    }

    public void setTextBTDeviceAddress(String t) {
        textBTDeviceAddress.setText(t);
        textBTDeviceAddress.setCaretPosition(0);
    }

    public String getTextBTGattServiceUuid() {
        return textBTGattServiceUuid.getText();
    }

    public void setTextBTGattServiceUuid(String t) {
        textBTGattServiceUuid.setText(t);
        textBTGattServiceUuid.setCaretPosition(0);
    }

    public String getTextBTGattTxUuid() {
        return textBTGattTxUuid.getText();
    }

    public void setTextBTGattTxUuid(String t) {
        textBTGattTxUuid.setText(t);
        textBTGattTxUuid.setCaretPosition(0);
    }

    public String getTextBTGattRxUuid() {
        return textBTGattRxUuid.getText();
    }

    public void setTextBTGattRxUuid(String t) {
        textBTGattRxUuid.setText(t);
        textBTGattRxUuid.setCaretPosition(0);
    }

    private class ActionButton implements ActionListener {
        BTSettingSubPanel panel;

        public ActionButton(BTSettingSubPanel p) {
            panel = p;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String cmd = ev.getActionCommand();

            if (cmd.equalsIgnoreCase(ACT_SET_DEFAULT)) {
                actionUseDefaultGatt();
            }
        }

        public void actionUseDefaultGatt() {
            panel.setTextBTGattServiceUuid(BTSettingSubPanel.DEFAULT_UUID_SERVICE);
            panel.setTextBTGattTxUuid(BTSettingSubPanel.DEFAULT_UUID_TX);
            panel.setTextBTGattRxUuid(BTSettingSubPanel.DEFAULT_UUID_RX);
        }
    }
}
