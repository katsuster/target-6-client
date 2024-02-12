package net.katsuster.ble;

import java.awt.*;
import javax.swing.*;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;

public class BTScanPanel extends JPanel {
    private int fontSize = 16;
    private DefaultListModel<BTAdapterItem> modelBTAdapter = new DefaultListModel<>();
    private DefaultListModel<BTDeviceItem> modelBTDevice = new DefaultListModel<>();
    private DefaultListModel<GattServiceItem> modelGattService = new DefaultListModel<>();
    private DefaultListModel<GattCharacteristicItem> modelGattCharacteristic = new DefaultListModel<>();
    private BluetoothAdapter BTAdapter;
    private BluetoothDevice BTDevice;
    private BluetoothGattService BTGattService;
    private BluetoothGattCharacteristic BTGattCharacteristic;
    BTAdapterSubPanel subPanelAdapter;
    BTDeviceSubPanel subPanelDevice;
    BTServiceSubPanel subPanelService;
    BTCharacteristicSubPanel subPanelCharacteristic;
    BTSettingSubPanel subPanelSetting;
    BTTestSubPanel subPanelTest;

    public BTScanPanel() {
        Dimension preferredListSize = new Dimension(200, (int)(fontSize * 1.3 * 8));

        subPanelAdapter = new BTAdapterSubPanel(this);
        subPanelDevice = new BTDeviceSubPanel(this);
        subPanelService = new BTServiceSubPanel(this);
        subPanelCharacteristic = new BTCharacteristicSubPanel(this);
        subPanelSetting = new BTSettingSubPanel(this);
        subPanelTest = new BTTestSubPanel(this);

        setLayout(new GridLayout(2, 3));
        add(subPanelAdapter);
        add(subPanelDevice);
        add(subPanelService);
        add(subPanelCharacteristic);
        add(subPanelSetting);
        add(subPanelTest);
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int s) {
        fontSize = s;
    }

    public DefaultListModel<BTAdapterItem> getModelBTAdapter() {
        return modelBTAdapter;
    }

    public DefaultListModel<BTDeviceItem> getModelBTDevice() {
        return modelBTDevice;
    }

    public DefaultListModel<GattServiceItem> getModelGattService() {
        return modelGattService;
    }

    public DefaultListModel<GattCharacteristicItem> getModelGattCharacteristic() {
        return modelGattCharacteristic;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return BTAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter ada) {
        BTAdapter = ada;
    }

    public BluetoothDevice getBluetoothDevice() {
        return BTDevice;
    }

    public void setBluetoothDevice(BluetoothDevice dev) {
        BTDevice = dev;
    }

    public BluetoothGattService getBluetoothGattService() {
        return BTGattService;
    }

    public void setBluetoothGattService(BluetoothGattService srv) {
        BTGattService = srv;
    }

    public BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        return BTGattCharacteristic;
    }

    public void setBluetoothGattCharacteristic(BluetoothGattCharacteristic chr) {
        BTGattCharacteristic = chr;
    }

    public String getTextBTAdapterAddress() {
        return subPanelSetting.getTextBTAdapterAddress();
    }

    public void setTextBTAdapterAddress(String t) {
        subPanelSetting.setTextBTAdapterAddress(t);
    }

    public String getTextBTDeviceAddress() {
        return subPanelSetting.getTextBTDeviceAddress();
    }

    public void setTextBTDeviceAddress(String t) {
        subPanelSetting.setTextBTDeviceAddress(t);
    }

    public String getTextBTGattServiceUuid() {
        return subPanelSetting.getTextBTGattServiceUuid();
    }

    public void setTextBTGattServiceUuid(String t) {
        subPanelSetting.setTextBTGattServiceUuid(t);
    }

    public String getTextBTGattTxUuid() {
        return subPanelSetting.getTextBTGattTxUuid();
    }

    public void setTextBTGattTxUuid(String t) {
        subPanelSetting.setTextBTGattTxUuid(t);
    }

    public String getTextBTGattRxUuid() {
        return subPanelSetting.getTextBTGattRxUuid();
    }

    public void setTextBTGattRxUuid(String t) {
        subPanelSetting.setTextBTGattRxUuid(t);
    }
}
