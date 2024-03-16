package net.katsuster.scenario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import net.katsuster.ble.BTDeviceEvent;
import net.katsuster.ble.BTDeviceListener;

public class BTCommandHandler implements BTDeviceListener {
    public static final String PREFIX_DEVICE_ID = "d";
    public static final String PREFIX_SENSOR_ID = "s";

    private Scenario scenario;

    public BTCommandHandler(Scenario s) {
        scenario = s;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void messageReceived(BTDeviceEvent e) {
        try {
            parse(e);
        } catch (RuntimeException ex) {
            scenario.printError("BTCommand: Illegal format in answers.", ex);
            scenario.printError("BTCommand: ans:" + e.getMessage(), null);
        } catch (ParseException ex) {
            scenario.printError("BTCommand: Illegal format format (time) in answers.", ex);
            scenario.printError("BTCommand: ans:" + e.getMessage(), null);
        }
    }

    public void cmdInit(StringTokenizer st, int devid) throws ParseException {
    }

    public void cmdSingle(StringTokenizer st, int devid) throws ParseException {
    }

    public void cmdMulti(StringTokenizer st, int devid) throws ParseException {
    }

    public void cmdBeep(StringTokenizer st, int devid) throws ParseException {
    }

    public void cmdHit(StringTokenizer st, int devid) throws ParseException {
    }

    protected void parse(BTDeviceEvent e) throws ParseException {
        StringTokenizer st = new StringTokenizer(e.getMessage(), " ", false);
        String next;

        int devid = parseID(st.nextToken(), PREFIX_DEVICE_ID);

        next = st.nextToken();
        if (next.equalsIgnoreCase(Scenario.CMD_INIT)) {
            cmdInit(st, devid);
        } else if (next.equalsIgnoreCase(Scenario.CMD_SINGLE)) {
            cmdSingle(st, devid);
        } else if (next.equalsIgnoreCase(Scenario.CMD_MULTI)) {
            cmdMulti(st, devid);
        } else if (next.equalsIgnoreCase(Scenario.CMD_BEEP)) {
            cmdBeep(st, devid);
        } else if (next.equalsIgnoreCase(Scenario.CMD_HIT)) {
            cmdHit(st, devid);
        }
    }

    protected int parseID(String token, String prefix) {
        StringTokenizer st = new StringTokenizer(token, ":", false);
        String pre = st.nextToken();
        if (!pre.equalsIgnoreCase(prefix)) {
            throw new IllegalArgumentException("ParseID: Answers have no prefix '" + prefix + "'.");
        }

        return Integer.parseInt(st.nextToken());
    }
}
