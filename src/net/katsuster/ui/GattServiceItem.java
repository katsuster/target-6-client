package net.katsuster.ui;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;

public class GattServiceItem {
    private BluetoothGattService service;

    public GattServiceItem(BluetoothGattService srv) {
        service = srv;
    }

    public BluetoothGattService getBluetoothGattService() {
        return service;
    }

    public void setBluetoothGattService(BluetoothGattService srv) {
        service = srv;
    }

    @Override
    public String toString() {
        return service.getUuid();
    }
}
