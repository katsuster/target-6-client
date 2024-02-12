package net.katsuster.ble;

import java.util.prefs.Preferences;

public class BTSetting {
    public static String SETTING_PREFIX       = "Target";
    public static String SETTING_ADAPTER      = "BTAdapterAddress";
    public static String SETTING_DEVICE       = "BTDeviceAddress";
    public static String SETTING_GATT_SERVICE = "GattServiceUuid";
    public static String SETTING_GATT_TX      = "GattTxUuid";
    public static String SETTING_GATT_RX      = "GattRxUuid";

    public BTSetting() {
        //Do nothing
    }

    public static String getKeyPrefix(int id) {
        return SETTING_PREFIX + id + ":";
    }

    public static String getSetting(int id, String key) {
        Preferences prefs = Preferences.userNodeForPackage(MainWindow.class);
        String pre = BTSetting.getKeyPrefix(id);

        return prefs.get(pre + key, "");
    }

    public static void putSetting(int id, String key, String value) {
        Preferences prefs = Preferences.userNodeForPackage(MainWindow.class);
        String pre = BTSetting.getKeyPrefix(id);

        prefs.put(pre + key, value);
    }
}
