package net.katsuster.ble;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.bluez.exceptions.*;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.handlers.AbstractPropertiesChangedHandler;
import org.freedesktop.dbus.interfaces.DBus;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;
import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;

public class BTDeviceStream {
    private BluetoothDevice BTDevice;
    private String UuidService;
    private String UuidTx;
    private String UuidRx;
    private BluetoothGattService GattService;
    private BluetoothGattCharacteristic GattTx;
    private BluetoothGattCharacteristic GattRx;
    private BTInputStream streamIn;
    private BTOutputStream streamOut;

    public BTDeviceStream(BluetoothDevice dev, String srv, String tx, String rx) throws IllegalArgumentException, DBusException {
        BTDevice = dev;
        UuidService = srv;
        UuidTx = tx;
        UuidRx = rx;

        GattService = BTDevice.getGattServiceByUuid(UuidService);
        if (GattService == null) {
            System.err.println("Error: Bluttooth device does not have GATT service " + UuidService + ".");
            throw new IllegalArgumentException("Error: GATT service " + UuidService + " is not found.");
        }

        GattTx = GattService.getGattCharacteristicByUuid(UuidTx);
        if (GattService == null) {
            System.err.println("Error: Bluttooth device does not have GATT characteristic (Tx) " + UuidTx + ".");
            throw new IllegalArgumentException("Error: GATT characteristic (Tx) " + UuidTx + " is not found.");
        }

        GattRx = GattService.getGattCharacteristicByUuid(UuidRx);
        if (GattRx == null) {
            System.err.println("Error: Bluttooth device does not have GATT characteristic (Rx) " + UuidRx + ".");
            throw new IllegalArgumentException("Error: GATT characteristic (Rx) " + UuidRx + " is not found.");
        }

        streamIn = new BTInputStream(GattRx);
        streamOut = new BTOutputStream(GattTx);
    }

    public InputStream getInputStream() {
        return streamIn;
    }

    public OutputStream getOutputStream() {
        return streamOut;
    }

    public class PropertiesChangedHandler extends AbstractPropertiesChangedHandler {
        private BTInputStream streamIn;

        public PropertiesChangedHandler(BTInputStream s) {
            streamIn = s;
        }

        @Override
        public void handle(Properties.PropertiesChanged props) {
            Map<String, Variant<?>> mapProp = props.getPropertiesChanged();

            for (Map.Entry<String, Variant<?>> e : mapProp.entrySet()) {
                if (GattRx.getDbusPath().equalsIgnoreCase(props.getPath())) {

                }
            }
        }
    }

    public class BTInputStream extends InputStream {
        private BluetoothGattCharacteristic GattRx;
        private PropertiesChangedHandler handler;

        public BTInputStream(BluetoothGattCharacteristic rx) throws DBusException {
            GattRx = rx;

            DeviceManager deviceManager = DeviceManager.getInstance();
            handler = new PropertiesChangedHandler(this);
            deviceManager.registerPropertyHandler(handler);

            GattRx.startNotify();
        }

        @Override
        public void close() throws IOException {
            super.close();

            try {
                GattRx.stopNotify();

                DeviceManager deviceManager = DeviceManager.getInstance();
                deviceManager.unRegisterPropertyHandler(handler);
            } catch (DBusException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int read() throws IOException {
            return 0;
        }
    }

    public class BTOutputStream extends OutputStream {
        private BluetoothGattCharacteristic GattTx;

        public BTOutputStream(BluetoothGattCharacteristic tx) {
            GattTx = tx;
        }

        @Override
        public void write(byte[] b) throws IOException {
            try {
                GattTx.writeValue(b, null);
            } catch (DBusException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void write(int i) throws IOException {
            byte[] v = new byte[1];

            v[0] = (byte)i;

            try {
                GattTx.writeValue(v, null);
            } catch (DBusException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
