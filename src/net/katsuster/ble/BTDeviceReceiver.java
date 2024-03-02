package net.katsuster.ble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BTDeviceReceiver implements Runnable {
    private BTStream bt;
    private BufferedReader rd;
    private boolean term;
    private List<BTDeviceListener> listener;

    public BTDeviceReceiver(BTStream s) {
        bt = s;
        rd = new BufferedReader(new InputStreamReader(bt.getInputStream()));
        term = false;
        listener = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            while (!term) {
                String line = rd.readLine();

                fireBTDeviceEvent(new BTDeviceEvent(line));
            }
        } catch (IOException ex) {
            System.err.println("I/O error in read.\n");
            System.err.println("  msg:" + ex.getMessage());
            ex.printStackTrace(System.err);
        }

        try {
            bt.getInputStream().close();
            bt.getOutputStream().close();
        } catch (IOException ex) {
            System.err.println("I/O error in close.\n");
            System.err.println("  msg:" + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    public void terminate() {
        term = true;
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
