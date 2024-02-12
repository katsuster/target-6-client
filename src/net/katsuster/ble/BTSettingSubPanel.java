package net.katsuster.ble;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class BTSettingSubPanel extends JPanel {
    public static final String DEFAULT_UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String DEFAULT_UUID_RX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String DEFAULT_UUID_TX = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    public static final String ACT_SET_DEFAULT = "Default";
    public static final String ACT_LOAD_SETTING = "Load";
    public static final String ACT_SAVE_SETTING = "Save";

    private BTScanPanel parent;
    private JTextField textBTAdapterAddress;
    private JTextField textBTDeviceAddress;
    private JTextField textBTGattServiceUuid;
    private JTextField textBTGattTxUuid;
    private JTextField textBTGattRxUuid;
    private SpinnerNumberModel modelTargetDeviceID;

    public BTSettingSubPanel(BTScanPanel p) {
        parent = p;

        Dimension preferredTextSize = new Dimension(150, (int)(parent.getFontSize() * 1.3));

        //Buttons
        ActionButton actButton = new ActionButton(this);
        JButton buttonDefaultGATT = new JButton(ACT_SET_DEFAULT);
        buttonDefaultGATT.addActionListener(actButton);

        JPanel panelBtnSettings = new JPanel();
        panelBtnSettings.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnSettings.add(buttonDefaultGATT);

        JButton buttonLoad = new JButton(ACT_LOAD_SETTING);
        buttonLoad.addActionListener(actButton);
        JButton buttonSave = new JButton(ACT_SAVE_SETTING);
        buttonSave.addActionListener(actButton);

        JPanel panelBtnLoadSave = new JPanel();
        panelBtnLoadSave.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnLoadSave.add(buttonLoad);
        panelBtnLoadSave.add(buttonSave);

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

        //Target device ID
        JPanel panelTargetDeviceID = new JPanel();
        modelTargetDeviceID = new SpinnerNumberModel(0, 0, 2, 1);
        JSpinner spinnerTargetDeviceID = new JSpinner(modelTargetDeviceID);
        spinnerTargetDeviceID.addChangeListener(new TargetDeviceIDListener(this));
        panelTargetDeviceID.setLayout(new BorderLayout());
        panelTargetDeviceID.add(new JLabel("ID(0:manager, 1...:sensor): "), BorderLayout.WEST);
        panelTargetDeviceID.add(spinnerTargetDeviceID, BorderLayout.CENTER);

        //Layout
        JPanel pSETContent = new JPanel();
        pSETContent.setLayout(new BoxLayout(pSETContent, BoxLayout.PAGE_AXIS));
        pSETContent.add(panelTestSetting);
        pSETContent.add(panelBtnSettings);
        pSETContent.add(panelTargetDeviceID);
        pSETContent.add(panelBtnLoadSave);
        LayoutPanel pSET = new LayoutPanel();
        pSET.setPadding(2, 5, 2, 5);
        pSET.setContentBorder(BorderFactory.createTitledBorder("Settings"));
        pSET.setContent(pSETContent);

        add(pSET);

        loadTargetSetting(0);
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

    public int getTargetDeviceID() {
        return modelTargetDeviceID.getNumber().intValue();
    }

    public void loadTargetSetting(int id) {
        setTextBTAdapterAddress(BTSetting.getSetting(id, BTSetting.SETTING_ADAPTER));
        setTextBTDeviceAddress(BTSetting.getSetting(id, BTSetting.SETTING_DEVICE));
        setTextBTGattServiceUuid(BTSetting.getSetting(id, BTSetting.SETTING_GATT_SERVICE));
        setTextBTGattTxUuid(BTSetting.getSetting(id, BTSetting.SETTING_GATT_TX));
        setTextBTGattRxUuid(BTSetting.getSetting(id, BTSetting.SETTING_GATT_RX));
    }

    public void saveTargetSetting(int id) {
        BTSetting.putSetting(id, BTSetting.SETTING_ADAPTER, getTextBTAdapterAddress());
        BTSetting.putSetting(id, BTSetting.SETTING_DEVICE, getTextBTDeviceAddress());
        BTSetting.putSetting(id, BTSetting.SETTING_GATT_SERVICE, getTextBTGattServiceUuid());
        BTSetting.putSetting(id, BTSetting.SETTING_GATT_TX, getTextBTGattTxUuid());
        BTSetting.putSetting(id, BTSetting.SETTING_GATT_RX, getTextBTGattRxUuid());
    }

    private class TargetDeviceIDListener implements ChangeListener {
        BTSettingSubPanel panel;

        public TargetDeviceIDListener(BTSettingSubPanel p) {
            panel = p;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            panel.loadTargetSetting(panel.getTargetDeviceID());
        }
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
            if (cmd.equalsIgnoreCase(ACT_LOAD_SETTING)) {
                actionLoadSetting();
            }
            if (cmd.equalsIgnoreCase(ACT_SAVE_SETTING)) {
                actionSaveSetting();
            }
        }

        public void actionUseDefaultGatt() {
            panel.setTextBTGattServiceUuid(BTSettingSubPanel.DEFAULT_UUID_SERVICE);
            panel.setTextBTGattTxUuid(BTSettingSubPanel.DEFAULT_UUID_TX);
            panel.setTextBTGattRxUuid(BTSettingSubPanel.DEFAULT_UUID_RX);
        }

        public void actionLoadSetting() {
            panel.loadTargetSetting(panel.getTargetDeviceID());
        }

        public void actionSaveSetting() {
            panel.saveTargetSetting(panel.getTargetDeviceID());
        }
    }
}
