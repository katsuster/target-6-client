package net.katsuster.ui;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;

public class BTDeviceItem {
    private BluetoothDevice device;

    public BTDeviceItem(BluetoothDevice dev) {
        device = dev;
    }

    public BluetoothDevice getBluetoothDevice() {
        return device;
    }

    public void setBluetoothDevice(BluetoothDevice dev) {
        device = dev;
    }

    @Override
    public String toString() {
        return device.getAddress() + " (" + device.getDbusPath() + ")";
    }
}
