package net.katsuster.ble;

import java.util.Map;
import org.freedesktop.dbus.handlers.AbstractPropertiesChangedHandler;
import org.freedesktop.dbus.interfaces.Properties.PropertiesChanged;
import org.freedesktop.dbus.types.Variant;

public class PropertiesChangedHandler extends AbstractPropertiesChangedHandler {
    @Override
    public void handle(PropertiesChanged props) {
        Map<String, Variant<?>> mapProp = props.getPropertiesChanged();

        String path = props.getPath();
        String name = props.getName();
        String intername = props.getInterfaceName();
        String inter = props.getInterface();
        for (Map.Entry<String, Variant<?>> e : mapProp.entrySet()) {
            System.out.printf("%s: %s\n", e.getKey(), e.getValue().getValue());
        }
    }
}
