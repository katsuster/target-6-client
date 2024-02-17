package net.katsuster.scenario;

import java.awt.*;

public interface Scenario {
    String getName();
    ScenarioSwitcher getSwitcher();
    boolean getActivated();
    void setActivated(boolean a);

    void activate();
    void deactivate();
    void drawFrame(Graphics2D g2);
}
