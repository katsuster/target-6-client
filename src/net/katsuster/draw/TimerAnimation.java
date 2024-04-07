package net.katsuster.draw;

import java.awt.*;

public class TimerAnimation extends AbstractDrawable {
    private Runnable task;
    private long interval = 0;
    private long last;
    private boolean enabled = true;

    public TimerAnimation() {
        last = System.currentTimeMillis();
    }

    public TimerAnimation(Runnable t) {
        this();
        task = t;
    }

    @Override
    protected void drawInner(Graphics2D g2) {
        if (!enabled || task == null) {
            return;
        }

        if (System.currentTimeMillis() >= last + interval) {
            task.run();
            last = System.currentTimeMillis();
        }
    }

    public void schedule(Runnable t, long i) {
        task = t;
        interval = i;
        enabled = true;
    }

    public void cancel() {
        enabled = false;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long i) {
        interval = i;
    }
}
