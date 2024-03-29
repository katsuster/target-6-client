package net.katsuster.scenario;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.katsuster.draw.Drawable;

public class AbstractScenario implements Scenario {
    private ScenarioSwitcher switcher;
    private boolean activated = false;
    private List<Drawable> listDrawable = new ArrayList<>();

    public AbstractScenario(ScenarioSwitcher sw) {
        switcher = sw;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public ScenarioSwitcher getSwitcher() {
        return switcher;
    }

    protected void setSwitcher(ScenarioSwitcher s) {
        switcher = s;
    }

    @Override
    public boolean getActivated() {
        return activated;
    }

    @Override
    public void setActivated(boolean a) {
        activated = a;
    }

    @Override
    public void activate() {
        //do nothing
    }

    @Override
    public void deactivate() {
        //do nothing
    }

    @Override
    public void drawFrame(Graphics2D g2) {
        //do nothing
    }

    @Override
    public void printError(String str, Exception ex) {
        printErrorInner("Error", str, ex);
    }

    @Override
    public void printWarn(String str, Exception ex) {
        printErrorInner("Warn ", str, ex);
    }

    @Override
    public void printInfo(String str, Exception ex) {
        printErrorInner("Info ", str, ex);
    }

    public void clearDrawable() {
        listDrawable.clear();
    }

    public void addDrawable(Drawable d) {
        listDrawable.add(d);
    }

    public Drawable[] getDrawables() {
        return (Drawable[])listDrawable.toArray();
    }

    public void removeDrawable(Drawable d) {
        listDrawable.remove(d);
    }

    public void drawAllDrawable(Graphics2D g2) {
        for (Drawable d : listDrawable) {
            if (!d.getVisible()) {
                continue;
            }

            d.draw(g2);
        }
    }

    public boolean writeLine(int id, String s) {
        BufferedWriter[] btWr = getSwitcher().getBTInOut().getWriters();
        boolean success = false;
        int retry = 0;

        while (!success) {
            try {
                    btWr[id].write(s);
                    btWr[id].flush();
                    success = true;
            } catch (IOException ex) {
                printError("I/O error in write.", ex);
            } catch (RuntimeException ex) {
                printError("Runtime error in write.", ex);
            } catch (Exception ex) {
                printError("Unknown error in write.", ex);
            }

            if (retry > 1) {
                printError("Too many error in write, maybe failed.", null);
                getSwitcher().getBTInOut().failDevice(id);
                return false;
            }
            retry++;
        }

        return true;
    }

    public void printErrorInner(String header, String str, Exception ex) {
        getSwitcher().addLogLater(header + ": " + getName() + ": " + str + "\n");
        System.err.println(header + ": " + getName() + ": " + str);
        if (ex != null) {
            System.err.println("  msg:" + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }
}
