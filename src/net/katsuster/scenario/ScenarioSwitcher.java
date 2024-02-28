package net.katsuster.scenario;

import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;

import net.katsuster.ble.BTInOut;
import net.katsuster.ui.LogWindow;
import net.katsuster.ui.MainWindow;

public class ScenarioSwitcher implements Runnable {
    public static final long NS_1SEC = 1000000000L;
    public static final long NS_1MSEC = 1000000L;

    private ScenarioSetting setting;
    private BufferStrategy strategy;
    private MainWindow mainWnd;
    private LogWindow logWnd;
    private boolean term = false;
    private boolean btRecover = false;
    private StringBuffer logBuffer;
    private long nsStart;
    private BTInOut btIO;
    private Scenario curScenario;
    private Scenario nextScenario;
    private int targetFPS = 60;

    public ScenarioSwitcher(ScenarioSetting s, MainWindow mw, LogWindow lw) {
        setting = s;
        mainWnd = mw;
        logWnd = lw;
        logBuffer = new StringBuffer();
        btIO = new BTInOut(this);
    }

    @Override
    public void run() {
        initGraphics();

        while (!term) {
            long tFrame = System.nanoTime();

            drawFrame();
            adjustFPS(tFrame);
            switchScenario();

            /* Check health of Bluetooth devices */
            if (getBTRecover() && !isReadyBTIO()) {
                System.err.println("Warn: bluetooth device health is not good, try to recover...");
                termBTIO();
                setBTRecover(false);
                setNextScenario(new OpeningScenario(this));
            }
        }
    }

    protected void initBTIO() {
        boolean done = false;

        for (int i = 0; i < 5; i++) {
            addLogLater("Try to connect " + i + ".\n");
            btIO.connectBTDevices();
            if (btIO.getNumberOfConnectedDevices() == BTInOut.NUM_DEVICES) {
                done = true;
                break;
            }
        }
        if (!done) {
            addLogLater("Failed to connect. Please check bluetooth settings.\n");
            return;
        }
        addLogLater("Connected.\n");
    }

    protected void termBTIO() {
        boolean done = false;

        for (int i = 0; i < 5; i++) {
            btIO.disconnectBTDevices();
            if (btIO.getNumberOfConnectedDevices() == 0) {
                done = true;
                break;
            }
        }
        if (!done) {
            addLogLater("Failed to disconnect. Please check bluetooth settings.\n");
            return;
        }
        addLogLater("Disconnected.\n");
    }

    protected void initGraphics() {
        strategy = mainWnd.getBufferStrategy();
        setStartTime(System.nanoTime());
        clearLogLater();
    }

    protected void drawFrame() {
        if (curScenario == null) {
            return;
        }

        do {
            do {
                Graphics2D g2 = (Graphics2D)strategy.getDrawGraphics();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setBackground(Color.WHITE);
                g2.clearRect(0, 0, mainWnd.getWidth(), mainWnd.getHeight());

                synchronized (curScenario) {
                    curScenario.drawFrame(g2);
                }

                g2.dispose();
            } while (strategy.contentsRestored());

            strategy.show();
        } while (strategy.contentsLost());
    }

    protected void adjustFPS(long nsFrameStart) {
        long tPast = System.nanoTime() - nsFrameStart;
        long tTarget = NS_1SEC / getTargetFPS();

        if (tPast - tTarget < 0) {
            try {
                Thread.sleep((tTarget - tPast) / NS_1MSEC);
            } catch (InterruptedException ex) {
                //ignore
            }
        }
    }

    protected void switchScenario() {
        if (nextScenario != curScenario) {
            if (curScenario != null) {
                curScenario.deactivate();
                curScenario.setActivated(false);
                addLogLater("Leaving " + curScenario.getName() + "\n");
            }

            addLogLater("Entering " + nextScenario.getName() + "\n");
            nextScenario.setActivated(true);
            nextScenario.activate();
            curScenario = nextScenario;
        }
    }

    public ScenarioSetting getSetting() {
        return setting;
    }

    public MainWindow getMainWindow() {
        return mainWnd;
    }

    public boolean isReadyBTIO() {
        return btIO.getNumberOfConnectedDevices() == btIO.NUM_DEVICES;
    }

    public BTInOut getBTInOut() {
        return btIO;
    }

    public boolean getBTRecover() {
        return btRecover;
    }

    public void setBTRecover(boolean b) {
        btRecover = b;
    }

    public void terminate() {
        term = true;
    }

    public Scenario getNextScenario() {
        return nextScenario;
    }

    public void setNextScenario(Scenario s) {
        nextScenario = s;
    }

    public long getStartTime() {
        return nsStart;
    }

    public void setStartTime(long ns) {
        nsStart = ns;
    }

    public int getTargetFPS() {
        return targetFPS;
    }

    public void setTargetFPS(int n) {
        targetFPS = n;
    }

    public String getTimeStampString(long ns) {
        long ms = ns / 1000000;
        long sOnly = ms / 1000;
        long msOnly = ms % 1000;

        return String.format("%d.%03d", sOnly, msOnly);
    }

    public void clearLogLater() {
        logBuffer = new StringBuffer();
        SwingUtilities.invokeLater(() -> {
            logWnd.getLogGame().setText("");
        });
    }

    public void addLogLater(String msg) {
        logBuffer.append(getTimeStampString(System.nanoTime() - getStartTime()) + ": " + msg);
        SwingUtilities.invokeLater(() -> {
            logWnd.getLogGame().setText(logBuffer.toString());
        });
    }
}
