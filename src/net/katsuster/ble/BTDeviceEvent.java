package net.katsuster.ble;

public class BTDeviceEvent {
    private String message;

    public BTDeviceEvent(String msg) {
        message = msg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        message = msg;
    }
}
