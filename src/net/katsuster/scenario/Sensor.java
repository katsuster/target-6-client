package net.katsuster.scenario;

public class Sensor {
    public enum SensorState {
        DISABLED,
        RESET,
        BLIGHT_WAIT,
        BLIGHT,
        HIT,
    }

    private SensorState state;
    private long timeHit;

    public Sensor() {
        state = SensorState.RESET;
    }

    public SensorState getState() {
        return state;
    }

    public void setState(SensorState s) {
        state = s;
    }

    public long getTimeHit() {
        return timeHit;
    }

    public void setTimeHit(long t) {
        timeHit = t;
    }
}
