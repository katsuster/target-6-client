package net.katsuster.ble;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.freedesktop.dbus.exceptions.DBusException;

import net.katsuster.scenario.ScenarioSwitcher;

public class BTInOut {
    public static final int NUM_DEVICES = 3;

    ScenarioSwitcher switcher;
    private BTStream[] streamBT = new BTStream[NUM_DEVICES];
    private InputStream[] streamIn = new InputStream[NUM_DEVICES];
    private OutputStream[] streamOut = new OutputStream[NUM_DEVICES];
    private BTDeviceReceiver[] receiver = new BTDeviceReceiver[NUM_DEVICES];
    private Thread[] receiverThread = new Thread[NUM_DEVICES];
    private BufferedWriter[] streamWr = new BufferedWriter[NUM_DEVICES];
    private BTStatus[] deviceStatus = new BTStatus[NUM_DEVICES];
    private List<BTDeviceListener> listener;

    public enum BTStatus {
        DISCONNECTED,
        CONNECTED,
    }

    public BTInOut(ScenarioSwitcher sw) {
        switcher = sw;
        Arrays.fill(deviceStatus, BTStatus.DISCONNECTED);
        listener = new ArrayList<>();
    }

    public void connectBTDevice(int id) throws DBusException {
        if (id < 0 || NUM_DEVICES <= id) {
            throw new IllegalArgumentException("Illegal device ID:" + id + ".");
        }

        if (deviceStatus[id] != BTStatus.DISCONNECTED) {
            return;
        }

        switcher.addLogLater("Connect to Device " + id + "\n");
        streamBT[id] = new BTStream(id, 3);
        streamIn[id] = streamBT[id].getInputStream();
        streamOut[id] = streamBT[id].getOutputStream();
        receiver[id] = new BTDeviceReceiver(streamBT[id]);
        receiver[id].addBTDeviceListener(new ReceiverHandler(this));
        receiverThread[id] = new Thread(receiver[id]);
        streamWr[id] = new BufferedWriter(new OutputStreamWriter(streamOut[id]));
        deviceStatus[id] = BTStatus.CONNECTED;

        receiverThread[id].start();
    }

    public void connectBTDevices() {
        int id = -1;

        try {
            for (id = 0; id < streamBT.length; id++) {
                connectBTDevice(id);
            }
        } catch (Exception ex) {
            switcher.addLogLater("Failed to connect device " + id + "\n");
            System.err.println("  msg:" + ex.getMessage());
        }
    }

    public void disconnectBTDevice(int id) throws DBusException {
        if (id < 0 || NUM_DEVICES <= id) {
            throw new IllegalArgumentException("Illegal device ID:" + id + ".");
        }

        if (deviceStatus[id] == BTStatus.DISCONNECTED) {
            return;
        }

        try {
            switcher.addLogLater("Disconnect to Device " + id + "\n");
            receiver[id].terminate();
            receiverThread[id].interrupt();
            receiverThread[id].join();
            deviceStatus[id] = BTStatus.DISCONNECTED;

            streamBT[id] = null;
            streamIn[id] = null;
            streamOut[id] = null;
            receiver[id] = null;
            receiverThread[id] = null;
            streamWr[id] = null;
        } catch (InterruptedException ex) {
            switcher.addLogLater("Failed to join receiver thread for device " + id + "\n");
            System.err.println("  msg:" + ex.getMessage());
        }
    }

    public void disconnectBTDevices() {
        int id = -1;

        try {
            for (id = 0; id < streamBT.length; id++) {
                disconnectBTDevice(id);
            }
        } catch (Exception ex) {
            switcher.addLogLater("Failed to disconnect device " + id + "\n");
            System.err.println("  msg:" + ex.getMessage());
        }
    }

    public int getNumberOfConnectedDevices() {
        int cnt = 0;

        for (BTStatus i : deviceStatus) {
            if (i != BTStatus.DISCONNECTED) {
                cnt++;
            }
        }

        return cnt;
    }

    public class ReceiverHandler implements BTDeviceListener {
        BTInOut btIO;

        public ReceiverHandler(BTInOut b) {
            btIO = b;
        }

        public void messageReceived(BTDeviceEvent e) {
            btIO.fireBTDeviceEvent(e);
        }
    }

    public BufferedWriter[] getWriters() {
        return streamWr;
    }

    public BufferedWriter getWriter(int id) {
        if (id < 0 || NUM_DEVICES <= id) {
            throw new IndexOutOfBoundsException("BTIO Writer: Device ID is out of bounds.");
        }

        return streamWr[id];
    }

    public BTStatus[] getDeviceStatus() {
        return deviceStatus;
    }

    public BTStatus getDeviceStatus(int id) {
        if (id < 0 || NUM_DEVICES <= id) {
            throw new IndexOutOfBoundsException("BTIO Status: Device ID is out of bounds.");
        }

        return deviceStatus[id];
    }

    public void addBTDeviceListener(BTDeviceListener l) {
        listener.add(l);
    }

    public BTDeviceListener[] getBTDeviceListers() {
        return (BTDeviceListener[])listener.toArray();
    }

    public void removeBTDeviceListener(BTDeviceListener l) {
        listener.remove(l);
    }

    protected void fireBTDeviceEvent(BTDeviceEvent e) {
        for (BTDeviceListener l : listener) {
            l.messageReceived(e);
        }
    }
}
