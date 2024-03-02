package net.katsuster.ble;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.freedesktop.dbus.handlers.AbstractPropertiesChangedHandler;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;
import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;

public class BTStream {
    private String BTAdapterMac;
    private String BTDeviceMac;
    private String UuidService;
    private String UuidTx;
    private String UuidRx;
    private BluetoothAdapter BTAdapter;
    private BluetoothDevice BTDevice;
    private BluetoothGattService GattService;
    private BluetoothGattCharacteristic GattTx;
    private BluetoothGattCharacteristic GattRx;
    private BTInputStream streamIn;
    private BTOutputStream streamOut;

    public BTStream(int id, int timeout) throws DBusException, InterruptedException {
        initStream(id, timeout);
    }

    public BTStream(String ada, String dev, String srv, String tx, String rx, int timeout) throws DBusException, InterruptedException {
        initStream(ada, dev, srv, tx, rx, timeout);
    }

    private void initStream(int id, int timeout) throws DBusException, InterruptedException {
        String ada = BTSetting.getSetting(id, BTSetting.SETTING_ADAPTER);
        String dev = BTSetting.getSetting(id, BTSetting.SETTING_DEVICE);
        String srv = BTSetting.getSetting(id, BTSetting.SETTING_GATT_SERVICE);
        String tx = BTSetting.getSetting(id, BTSetting.SETTING_GATT_TX);
        String rx = BTSetting.getSetting(id, BTSetting.SETTING_GATT_RX);

        initStream(ada, dev, srv, tx, rx, timeout);
    }

    private void initStream(String ada, String dev, String srv, String tx, String rx, int timeout) throws DBusException, InterruptedException {
        BTAdapterMac = ada;
        BTDeviceMac = dev;
        UuidService = srv;
        UuidTx = tx;
        UuidRx = rx;

        BTAdapter = findAdapter(BTAdapterMac);
        BTDevice = findDevice(BTAdapter, BTDeviceMac, timeout);
        GattService = findService(BTDevice, UuidService, timeout);

        GattTx = GattService.getGattCharacteristicByUuid(UuidTx);
        if (GattTx == null) {
            BTDevice.disconnect();
            System.err.println("Error: Bluetooth device does not have GATT characteristic (Tx) '" + UuidTx + "'.");
            throw new IllegalArgumentException("Error: GATT characteristic (Tx) '" + UuidTx + "' is not found.");
        }

        GattRx = GattService.getGattCharacteristicByUuid(UuidRx);
        if (GattRx == null) {
            BTDevice.disconnect();
            System.err.println("Error: Bluetooth device does not have GATT characteristic (Rx) '" + UuidRx + "'.");
            throw new IllegalArgumentException("Error: GATT characteristic (Rx) '" + UuidRx + "' is not found.");
        }

        try {
            streamIn = new BTInputStream(GattRx);
            streamOut = new BTOutputStream(GattTx);
        } catch (IOException ex) {
            BTDevice.disconnect();
            System.err.println("Error: Cannot init Bluetooth IO stream.");
            System.err.println("  msg:" + ex.getMessage());
            ex.printStackTrace(System.err);
            throw new IllegalArgumentException("Error: Cannot init Bluetooth IO stream.");
        }
    }

    private BluetoothAdapter findAdapter(String mac) {
        DeviceManager deviceManager = DeviceManager.getInstance();
        BluetoothAdapter adapter = null;

        List<BluetoothAdapter> listAdas = deviceManager.getAdapters();
        if (listAdas.isEmpty()) {
            System.err.println("Error: No Bluttooth adapter.");
            throw new IllegalArgumentException("Error: No BT adapter.");
        }
        for (BluetoothAdapter ada : listAdas) {
            if (ada.getAddress().equalsIgnoreCase(mac)) {
                adapter = ada;
                break;
            }
        }
        if (adapter == null) {
            System.err.println("Error: Bluetooth adapter MAC:'" + mac + "' is not found.");
            throw new IllegalArgumentException("Error: BT adapter '" + mac + "' is not found.");
        }

        return adapter;
    }

    private BluetoothDevice findDevice(BluetoothAdapter adapter, String mac, int timeout) throws InterruptedException {
        DeviceManager deviceManager = DeviceManager.getInstance();
        BluetoothDevice device = null;

        adapter.startDiscovery();
        Thread.sleep(timeout * 1000);
        adapter.stopDiscovery();

        List<BluetoothDevice> devices = deviceManager.getDevices(true);
        if (devices.isEmpty()) {
            System.err.println("Error: No Bluetooth device.");
            throw new IllegalArgumentException("Error: No BT device.");
        }
        for (BluetoothDevice dev : devices) {
            if (dev.getAddress().equalsIgnoreCase(mac)) {
                device = dev;
            }
        }
        if (device == null) {
            System.err.println("Error: Bluetooth device MAC:'" + mac + "' is not found.");
            throw new IllegalArgumentException("Error: BT device '" + mac + "' is not found.");
        }

        return device;
    }

    private BluetoothGattService findService(BluetoothDevice device, String uuid, int timeout) throws InterruptedException {
        BluetoothGattService service = null;
        AtomicBoolean finish = new AtomicBoolean(false);
        int retry;

        Thread t = new Thread(() -> {
            device.connect();
            finish.set(true);
        });
        t.start();

        retry = 0;
        while (!finish.get()) {
            if (retry >= timeout * 10) {
                t.interrupt();
                t.join();
                break;
            }

            Thread.sleep(100);
            retry += 1;
        }
        if (!finish.get()) {
            device.disconnect();
            System.err.println("Error: Cannot connect to Bluetooth device.");
            throw new IllegalArgumentException("Error: Cannot connect to BT device.");
        }

        device.refreshGattServices();
        List<BluetoothGattService> gattServices = device.getGattServices();
        retry = 0;
        while (gattServices.isEmpty()) {
            if (retry >= timeout * 10) {
                break;
            }

            Thread.sleep(100);
            retry += 1;

            device.refreshGattServices();
            gattServices = device.getGattServices();
        }
        if (gattServices.isEmpty()) {
            device.disconnect();
            System.err.println("Error: No Bluetooth GATT service.");
            throw new IllegalArgumentException("Error: No BT GATT service.");
        }
        for (BluetoothGattService srv : gattServices) {
            if (srv.getUuid().equalsIgnoreCase(uuid)) {
                service = srv;
            }
        }
        if (service == null) {
            device.disconnect();
            System.err.println("Error: Bluetooth GATT service UUID:'" + uuid + "' is not found.");
            throw new IllegalArgumentException("Error: BT GATT service '" + uuid + "' is not found.");
        }

        return service;
    }

    public InputStream getInputStream() {
        return streamIn;
    }

    public OutputStream getOutputStream() {
        return streamOut;
    }

    public class BTInputStream extends InputStream {
        private BluetoothGattCharacteristic GattRx;
        private PropertiesChangedHandler handler;
        private PipedInputStream pipeIn;
        private PipedOutputStream pipeOut;

        public BTInputStream(BluetoothGattCharacteristic rx) throws IOException, DBusException {
            GattRx = rx;

            pipeIn = new PipedInputStream();
            pipeOut = new PipedOutputStream(pipeIn);

            DeviceManager deviceManager = DeviceManager.getInstance();
            handler = new PropertiesChangedHandler(this, GattRx);
            deviceManager.registerPropertyHandler(handler);

            GattRx.startNotify();
        }

        public PipedOutputStream getPipeOut() {
            return pipeOut;
        }

        @Override
        public void close() throws IOException {
            super.close();

            try {
                GattRx.stopNotify();
                GattRx.getService().getDevice().disconnect();

                DeviceManager deviceManager = DeviceManager.getInstance();
                deviceManager.unRegisterPropertyHandler(handler);
            } catch (DBusException ex) {
                System.err.println("Error: failed to close bluetooth stream.");
                System.err.println("  msg:" + ex.getMessage());
                ex.printStackTrace(System.err);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public int read() throws IOException {
            return pipeIn.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return pipeIn.read(b, off, len);
        }

        public class PropertiesChangedHandler extends AbstractPropertiesChangedHandler {
            private BTInputStream streamIn;
            private BluetoothGattCharacteristic GattRx;

            public PropertiesChangedHandler(BTInputStream s, BluetoothGattCharacteristic g) {
                streamIn = s;
                GattRx = g;
            }

            @Override
            public void handle(Properties.PropertiesChanged props) {
                Map<String, Variant<?>> mapProp = props.getPropertiesChanged();
                byte[] dat = new byte[0];
                boolean found = false;

                if (GattRx.getDbusPath().equalsIgnoreCase(props.getPath())) {
                    for (Map.Entry<String, Variant<?>> e : mapProp.entrySet()) {
                        if (e.getValue().getValue() instanceof byte[]) {
                            dat = (byte[])e.getValue().getValue();
                            found = true;
                        }
                    }
                }
                if (!found) {
                    return;
                }

                try {
                    //System.out.println("recv: '" + new String(dat) + "'");
                    streamIn.getPipeOut().write(dat);
                    streamIn.getPipeOut().flush();
                } catch (IOException ex) {
                    System.err.println("Error: I/O error in write to bluetooth device.");
                    System.err.println("  msg:" + ex.getMessage());
                    System.err.println("  dat:" + dat);
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    public class BTOutputStream extends OutputStream {
        private BluetoothGattCharacteristic GattTx;

        public BTOutputStream(BluetoothGattCharacteristic tx) {
            GattTx = tx;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            try {
                byte[] bpart = new byte[len];
                System.arraycopy(b, off, bpart, 0, len);
                GattTx.writeValue(bpart, null);
                //System.out.println("send: '" + new String(bpart) + "'");
            } catch (DBusException ex) {
                System.err.println("Error: I/O error in write bytes to bluetooth GATT.");
                System.err.println("  msg:" + ex.getMessage());
                ex.printStackTrace(System.err);
                throw new RuntimeException(ex);
            } catch (DBusExecutionException ex) {
                System.err.println("Error: Runtime error in write bytes to bluetooth GATT.");
                System.err.println("  msg:" + ex.getMessage());
                ex.printStackTrace(System.err);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void write(int i) throws IOException {
            byte[] v = new byte[1];

            v[0] = (byte)i;

            try {
                GattTx.writeValue(v, null);
                //System.out.println("send: '" + new String(v) + "'");
            } catch (DBusException ex) {
                System.err.println("Error: I/O error in write a byte to bluetooth GATT.");
                System.err.println("  msg:" + ex.getMessage());
                ex.printStackTrace(System.err);
                throw new RuntimeException(ex);
            }
        }
    }
}
