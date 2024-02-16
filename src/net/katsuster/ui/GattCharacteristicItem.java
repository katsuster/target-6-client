package net.katsuster.ui;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;

public class GattCharacteristicItem {
    private BluetoothGattCharacteristic charactersistic;

    public GattCharacteristicItem(BluetoothGattCharacteristic ch) {
        charactersistic = ch;
    }

    public BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        return charactersistic;
    }

    public void setBluetoothGattCharacteristic(BluetoothGattCharacteristic ch) {
        charactersistic = ch;
    }

    @Override
    public String toString() {
        return charactersistic.getUuid();
    }
}
