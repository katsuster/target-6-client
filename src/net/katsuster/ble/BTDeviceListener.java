package net.katsuster.ble;

import java.util.EventListener;

public interface BTDeviceListener extends EventListener {
    void messageReceived(BTDeviceEvent e);
}
