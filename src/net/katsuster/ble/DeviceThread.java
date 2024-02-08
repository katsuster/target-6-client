package net.katsuster.ble;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattDescriptor;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceThread implements Runnable {
    private class ThreadSwitch {
        public boolean fTerm = false;
    }
    private class GattThread implements Runnable {
        private BluetoothGattCharacteristic gattChar;
        private ThreadSwitch switchThread;

        public GattThread(BluetoothGattCharacteristic g, ThreadSwitch s) {
            gattChar = g;
            switchThread = s;
        }

        private void procRead() {
            System.out.println("Reeeeeeeeeeeeeeeeeeeeeeeeed!!!!!!!!!!");
            Map<String, Object> mapRead = new HashMap<>();

            try {
                gattChar.startNotify();
            } catch (DBusException e) {
                e.printStackTrace();
                switchThread.fTerm = true;
                return;
            } catch (DBusExecutionException e) {
                e.printStackTrace();
                switchThread.fTerm = true;
                return;
            }

            while (!switchThread.fTerm) {
                /*if (!gattChar.isNotifying()) {
                    continue;
                }

                try {
                    byte[] val = gattChar.readValue(null);
                    if (val.length == 0) {
                        continue;
                    }
                    String s = new String(val);
                    System.out.println(s);

                    for (Map.Entry<String, Object> e : mapRead.entrySet()) {
                        System.out.printf("      %s, %s\n", e.getKey(), e.getValue());
                    }
                } catch (DBusException e) {
                    e.printStackTrace();
                    break;
                }*/
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //ignore
                }
            }

            try {
                gattChar.stopNotify();
            } catch (DBusException e) {
                e.printStackTrace();
            } catch (DBusExecutionException e) {
                e.printStackTrace();
            }
            switchThread.fTerm = true;
        }

        private String readLineFromReader(Reader r) throws IOException {
            StringBuilder s = new StringBuilder();
            int c;

            try {
                while ((c = r.read()) != 10) {
                    if (c == 13) {
                        continue;
                    }

                    s.append((char)c);
                }
            } catch (IOException e) {
                //ignore
            }

            return s.toString();
        }

        private void procWrite() {
            System.out.println("Wriiiiiiiiiiiiiiiiiiiiiiiiiiiiite!!!!!!!!!!");
            Map<String, Object> mapOpt = new HashMap<>();
            InputStreamReader in = new InputStreamReader(System.in);

            while (!switchThread.fTerm) {
                String str = "";

                try {
                    str = readLineFromReader(in);
                } catch (IOException e) {

                }
                if (str.equalsIgnoreCase("exit")) {
                    break;
                }

                try {
                    gattChar.writeValue(str.getBytes(), null);
                } catch (DBusException e) {
                    e.printStackTrace();
                    break;
                } catch (DBusExecutionException e) {
                    e.printStackTrace();
                    break;
                }
            }

            switchThread.fTerm = true;
        }

        @Override
        public void run() {
            gattChar.refreshGattDescriptors();
            List<BluetoothGattDescriptor> gattDescs = gattChar.getGattDescriptors();

            System.out.printf("    char    uuid: %s (notify:%s, descs:%d)\n",
                    gattChar.getUuid(), gattChar.isNotifying(), gattDescs.size());
            System.out.printf("    char    uuid: %s (%s)\n",
                    gattChar.getUuid(), gattChar.toString());

            for (BluetoothGattDescriptor gattDesc : gattDescs) {
                System.out.printf("      desc uuid: %s (%s)\n", gattDesc.getUuid(), gattDesc);
            }

            if (gattChar.getUuid().equalsIgnoreCase("6e400002-b5a3-f393-e0a9-e50e24dcca9e")) {
                procRead();
            } else if (gattChar.getUuid().equalsIgnoreCase("6e400003-b5a3-f393-e0a9-e50e24dcca9e")) {
                procWrite();
            }
        }
    }

    private BluetoothDevice dev;

    public DeviceThread(BluetoothDevice d) {
        dev = d;
    }

    private void procGattService(BluetoothGattService gattService) {
        gattService.refreshGattCharacteristics();
        List<BluetoothGattCharacteristic> gattChars = gattService.getGattCharacteristics();

        System.out.printf("  service uuid: %s (chars:%d)\n", gattService.getUuid(), gattChars.size());
        System.out.printf("  service uuid: %s (%s)\n", gattService.getUuid(), gattService);

        List<Thread> listThread = new ArrayList<>();
        ThreadSwitch switchThread = new ThreadSwitch();
        for (BluetoothGattCharacteristic gattChar : gattChars) {
            System.out.printf("    char    uuid: %s\n", gattChar.getUuid());
            Thread th = new Thread(new GattThread(gattChar, switchThread));
            th.start();

            listThread.add(th);
        }

        for (Thread th : listThread) {
            try {
                th.join();
                System.out.println("join!!");
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

    @Override
    public void run() {
        dev.connect();
        dev.refreshGattServices();

        List<BluetoothGattService> gattServices = dev.getGattServices();
        int retry = 0;
        while (gattServices.size() == 0) {
            System.out.println("retry");
            if (retry == 5) {
                System.err.println("There is no GATT service.");
                dev.disconnect();
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }
            retry += 1;

            dev.refreshGattServices();
            gattServices = dev.getGattServices();
        }

        System.out.printf("connect %s (services:%d)\n", dev.getAddress(), gattServices.size());

        for (BluetoothGattService gattService : gattServices) {
            procGattService(gattService);
        }

        dev.disconnect();
    }
}
