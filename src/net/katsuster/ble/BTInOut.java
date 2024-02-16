package net.katsuster.ble;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private BTStatus[] streamBTStatus = new BTStatus[NUM_DEVICES];
    private List<BTDeviceListener> listener;

    public enum BTStatus {
        DISCONNECTED,
        CONNECTED,
    }

    public BTInOut(ScenarioSwitcher sw) {
        switcher = sw;
        Arrays.fill(streamBTStatus, BTStatus.DISCONNECTED);
        listener = new ArrayList<>();
    }

    public void connectBTDevices() {
        int id = -1;

        try {
            for (id = 0; id < streamBT.length; id++) {
                if (streamBTStatus[id] != BTStatus.DISCONNECTED) {
                    continue;
                }

                switcher.addLogLater("Connect to Device " + id + "\n");
                streamBT[id] = new BTStream(id, 3);
                streamIn[id] = streamBT[id].getInputStream();
                streamOut[id] = streamBT[id].getOutputStream();
                receiver[id] = new BTDeviceReceiver(streamBT[id]);
                receiver[id].addBTDeviceListener(new ReceiverHandler(this));
                receiverThread[id] = new Thread(receiver[id]);
                streamWr[id] = new BufferedWriter(new OutputStreamWriter(streamOut[id]));
                streamBTStatus[id] = BTStatus.CONNECTED;

                receiverThread[id].start();
            }
        } catch (Exception ex) {
            switcher.addLogLater("Failed to connect device " + id + "\n");
            System.err.println("  msg:" + ex.getMessage());
        }
    }

    public void disconnectBTDevices() {
        int id = -1;

        try {
            for (id = 0; id < streamBT.length; id++) {
                if (streamBTStatus[id] == BTStatus.DISCONNECTED) {
                    continue;
                }

                switcher.addLogLater("Disconnect to Device " + id + "\n");
                receiver[id].terminate();
                receiverThread[id].interrupt();
                receiverThread[id].join();
                streamBTStatus[id] = BTStatus.DISCONNECTED;

                streamBT[id] = null;
                streamIn[id] = null;
                streamOut[id] = null;
                receiver[id] = null;
                receiverThread[id] = null;
                streamWr[id] = null;
            }
        } catch (Exception ex) {
            switcher.addLogLater("Failed to disconnect device " + id + "\n");
            System.err.println("  msg:" + ex.getMessage());
        }
    }

    public int getNumberOfConnectedDevices() {
        int cnt = 0;

        for (BTStatus i : streamBTStatus) {
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

    public BufferedWriter[] getBTWriters() {
        return streamWr;
    }

    public BufferedWriter getBTWriter(int id) {
        if (id < 0 || NUM_DEVICES <= id) {
            throw new IndexOutOfBoundsException("BTIO Writer: Device ID is out of bounds.");
        }

        return streamWr[id];
    }

    public BTDeviceReceiver[] getBTReceivers() {
        return receiver;
    }

    public BTDeviceReceiver getBTReceiver(int id) {
        if (id < 0 || NUM_DEVICES <= id) {
            throw new IndexOutOfBoundsException("BTIO Receiver: Device ID is out of bounds.");
        }

        return receiver[id];
    }

    public void addBTDeviceListener(BTDeviceListener l) {
        listener.add(l);
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
