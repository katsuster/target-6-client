package net.katsuster.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class MouseAdapterEx extends MouseAdapter {
    public static final int LONG_PRESS_TIMEOUT_MS = 1500;

    private AtomicInteger press = new AtomicInteger(0);
    private Timer tm;

    protected class HoldTask extends TimerTask {
        MouseEvent event;

        public HoldTask(MouseEvent e) {
            event = e;
        }

        @Override
        public void run() {
            if (press.get() != 1) {
                return;
            }
            press.set(0);

            switch (event.getButton()) {
            case MouseEvent.BUTTON1:
                mouseLeftLongPressed();
                break;
            case MouseEvent.BUTTON2:
                mouseCenterLongPressed();
                break;
            case MouseEvent.BUTTON3:
                mouseRightLongPressed();
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        tm = new Timer();
        tm.schedule(new HoldTask(e), LONG_PRESS_TIMEOUT_MS);
        press.getAndAdd(1);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (press.get() == 0) {
            //Do not fire Clicked event if Kept event has already happened
        } else if (press.get() == 1) {
            switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                mouseLeftClicked();
                break;
            case MouseEvent.BUTTON2:
                mouseCenterClicked();
                break;
            case MouseEvent.BUTTON3:
                mouseRightClicked();
                break;
            }
        }

        if (tm != null) {
            tm.cancel();
        }
        press.set(0);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseReleased(e);
    }

    public void mouseLeftClicked() {
    }

    public void mouseCenterClicked() {
    }

    public void mouseRightClicked() {
    }

    public void mouseLeftLongPressed() {
    }

    public void mouseCenterLongPressed() {
    }

    public void mouseRightLongPressed() {
    }
}
