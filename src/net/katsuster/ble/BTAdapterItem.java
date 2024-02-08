package net.katsuster.ble;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;

public class BTAdapterItem {
    private BluetoothAdapter adapter;

    public BTAdapterItem(BluetoothAdapter ada) {
        adapter = ada;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return adapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter ada) {
        adapter = ada;
    }

    @Override
    public String toString() {
        return adapter.getDbusPath() + " (" + adapter.getAddress() + ")";
    }
}
