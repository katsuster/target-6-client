package net.katsuster.scenario;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;

import net.katsuster.ui.MainWindow;

public class BTButtonHandler extends BTCommandHandler {
    private Scenario scenario;
    private MouseAdapter handlerMouse;

    public BTButtonHandler(Scenario s, MouseAdapter h) {
        super(s);

        scenario = s;
        handlerMouse = h;
    }

    @Override
    public void cmdButton(StringTokenizer st, int devid) {
        String next = st.nextToken();
        MainWindow wnd = scenario.getSwitcher().getMainWindow();

        if (next.equalsIgnoreCase("press")) {
            MouseEvent e = new MouseEvent(wnd, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                    0, 0, 0, 1, false, MouseEvent.BUTTON1);
            handlerMouse.mousePressed(e);
        } else if (next.equalsIgnoreCase("release")) {
            MouseEvent e = new MouseEvent(wnd, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(),
                    0, 0, 0, 1, false, MouseEvent.BUTTON1);
            handlerMouse.mouseReleased(e);
        } else {
            scenario.printError(Scenario.RES_BUTTON + ": unknown event " + next + ".", null);
        }
    }
}
