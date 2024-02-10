package net.katsuster.ble;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.CharBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;
import org.freedesktop.dbus.exceptions.DBusException;

public class PanelBTScan extends JPanel {
    public static final String DEFAULT_UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String DEFAULT_UUID_RX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String DEFAULT_UUID_TX = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    public static final String ACT_PROBE = "Probe";
    public static final String ACT_USE_ADAPTER = "Use Adapter";
    public static final String ACT_SCAN = "Scan";
    public static final String ACT_USE_DEVICE = "Use Device";
    public static final String ACT_SERVICE = "Service";
    public static final String ACT_USE_SERVICE = "Use Service";
    public static final String ACT_CHARACTERISTIC = "Characteristic";
    public static final String ACT_USE_TX = "Use Tx";
    public static final String ACT_USE_RX = "Use Rx";
    public static final String ACT_SET_DEFAULT = "Default";
    public static final String ACT_RUN_TEST = "Run";

    private DefaultListModel<BTAdapterItem> modelBTAdapter = new DefaultListModel<>();
    private DefaultListModel<BTDeviceItem> modelBTDevice = new DefaultListModel<>();
    private DefaultListModel<GattServiceItem> modelGattService = new DefaultListModel<>();
    private DefaultListModel<GattCharacteristicItem> modelGattCharacteristic = new DefaultListModel<>();
    private BluetoothAdapter BTAdapter;
    private BluetoothDevice BTDevice;
    private BluetoothGattService BTGattService;
    private BluetoothGattCharacteristic BTGattCharacteristic;
    private JTextField textBTAdapterAddress;
    private JTextField textBTDeviceAddress;
    private JTextField textBTGattServiceUuid;
    private JTextField textBTGattTxUuid;
    private JTextField textBTGattRxUuid;
    private JTextArea textLog;
    private JButton buttonUseAdapter;
    private JButton buttonProbe;
    private JButton buttonScan;
    private JButton buttonUseDevice;
    private JButton buttonService;
    private JButton buttonUseService;
    private JButton buttonCharacteristic;
    private JButton buttonUseTx;
    private JButton buttonUseRx;
    private SpinnerNumberModel modelScanTimeout;
    private SpinnerNumberModel modelServiceTimeout;
    private JTextField statusScanDevice;
    private JTextField statusScanService;
    private JTextField statusScanCharactersitic;
    private JTextField statusTest;

    public PanelBTScan(int fontSize) {
        Dimension preferredListSize = new Dimension(200, (int)(fontSize * 1.3 * 8));
        Dimension preferredTextSize = new Dimension(160, (int)(fontSize * 1.3));

        //Status
        statusScanDevice = new JTextField();
        statusScanDevice.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        statusScanDevice.setEditable(false);

        statusScanService = new JTextField();
        statusScanService.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        statusScanService.setEditable(false);

        statusScanCharactersitic = new JTextField();
        statusScanCharactersitic.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        statusScanCharactersitic.setEditable(false);

        statusTest = new JTextField();
        statusTest.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        statusTest.setEditable(false);

        //Buttons
        ActionButton actButton = new ActionButton(this);
        buttonProbe = new JButton(ACT_PROBE);
        buttonProbe.addActionListener(actButton);

        buttonUseAdapter = new JButton(ACT_USE_ADAPTER);
        buttonUseAdapter.addActionListener(actButton);
        buttonUseAdapter.setEnabled(false);

        buttonScan = new JButton(ACT_SCAN);
        buttonScan.addActionListener(actButton);
        buttonScan.setEnabled(false);

        JPanel panelBtnProbeAndScan = new JPanel();
        panelBtnProbeAndScan.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnProbeAndScan.add(buttonProbe);
        panelBtnProbeAndScan.add(buttonUseAdapter);
        panelBtnProbeAndScan.add(buttonScan);

        buttonUseDevice = new JButton(ACT_USE_DEVICE);
        buttonUseDevice.addActionListener(actButton);
        buttonUseDevice.setEnabled(false);

        buttonService = new JButton(ACT_SERVICE);
        buttonService.addActionListener(actButton);
        buttonService.setEnabled(false);

        JPanel panelBtnService = new JPanel();
        panelBtnService.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnService.add(buttonUseDevice);
        panelBtnService.add(buttonService);

        buttonUseService = new JButton(ACT_USE_SERVICE);
        buttonUseService.addActionListener(actButton);
        buttonUseService.setEnabled(false);

        buttonCharacteristic = new JButton(ACT_CHARACTERISTIC);
        buttonCharacteristic.addActionListener(actButton);
        buttonCharacteristic.setEnabled(false);

        JPanel panelBtnCharacteristic = new JPanel();
        panelBtnCharacteristic.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnCharacteristic.add(buttonUseService);
        panelBtnCharacteristic.add(buttonCharacteristic);

        buttonUseTx = new JButton(ACT_USE_TX);
        buttonUseTx.addActionListener(actButton);
        buttonUseTx.setEnabled(false);

        buttonUseRx = new JButton(ACT_USE_RX);
        buttonUseRx.addActionListener(actButton);
        buttonUseRx.setEnabled(false);

        JPanel panelBtnTxRx = new JPanel();
        panelBtnTxRx.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnTxRx.add(buttonUseTx);
        panelBtnTxRx.add(buttonUseRx);

        JButton buttonDefaultGATT = new JButton(ACT_SET_DEFAULT);
        buttonDefaultGATT.addActionListener(actButton);

        JPanel panelBtnSettings = new JPanel();
        panelBtnSettings.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnSettings.add(buttonDefaultGATT);

        JButton buttonRun = new JButton(ACT_RUN_TEST);
        buttonRun.addActionListener(actButton);

        JPanel panelBtnRun = new JPanel();
        panelBtnRun.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnRun.add(buttonRun);

        //Settings
        textBTAdapterAddress = new JTextField();
        textBTAdapterAddress.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTAdapterAddress.setPreferredSize(preferredTextSize);

        textBTDeviceAddress = new JTextField();
        textBTDeviceAddress.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTDeviceAddress.setPreferredSize(preferredTextSize);

        textBTGattServiceUuid = new JTextField();
        textBTGattServiceUuid.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTGattServiceUuid.setPreferredSize(preferredTextSize);

        textBTGattTxUuid = new JTextField();
        textBTGattTxUuid.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTGattTxUuid.setPreferredSize(preferredTextSize);

        textBTGattRxUuid = new JTextField();
        textBTGattRxUuid.setBorder(BorderFactory.createLoweredBevelBorder());
        textBTGattRxUuid.setPreferredSize(preferredTextSize);

        PanelComponentWithLabel panelTestSetting = new PanelComponentWithLabel();
        panelTestSetting.setLayout(new GridLayout(5, 1, fontSize / 3, fontSize / 3));
        panelTestSetting.addComponentWithLabel("Adapter MAC: ", textBTAdapterAddress);
        panelTestSetting.addComponentWithLabel("Device MAC: ", textBTDeviceAddress);
        panelTestSetting.addComponentWithLabel("Service GATT: ", textBTGattServiceUuid);
        panelTestSetting.addComponentWithLabel("Tx GATT: ", textBTGattTxUuid);
        panelTestSetting.addComponentWithLabel("Rx GATT: ", textBTGattRxUuid);

        //Log
        textLog = new JTextArea();
        textLog.setEditable(false);
        JScrollPane scrTextLog = new JScrollPane(textLog);
        scrTextLog.setPreferredSize(preferredListSize);

        //Lists
        JList<BTAdapterItem> listBTAdapter = new JList<>(modelBTAdapter);
        listBTAdapter.addListSelectionListener(new AdapterSelection(this, listBTAdapter));
        JScrollPane scrListBTAdapter = new JScrollPane(listBTAdapter);
        scrListBTAdapter.setPreferredSize(preferredListSize);

        JList<BTDeviceItem> listBTDevice = new JList<>(modelBTDevice);
        listBTDevice.addListSelectionListener(new DeviceSelection(this, listBTDevice));
        JScrollPane scrListBTDevice = new JScrollPane(listBTDevice);
        scrListBTDevice.setPreferredSize(preferredListSize);

        JList<GattServiceItem> listGattService = new JList<>(modelGattService);
        listGattService.addListSelectionListener(new ServiceSelection(this, listGattService));
        JScrollPane scrListGattService = new JScrollPane(listGattService);
        scrListGattService.setPreferredSize(preferredListSize);

        JList<GattCharacteristicItem> listGattCharacteristic = new JList<>(modelGattCharacteristic);
        listGattCharacteristic.addListSelectionListener(new CharacteristicSelection(this, listGattCharacteristic));
        JScrollPane scrListGattCharacteristic = new JScrollPane(listGattCharacteristic);
        scrListGattCharacteristic.setPreferredSize(preferredListSize);

        //Timeout
        JPanel panelScanTimeout = new JPanel();
        modelScanTimeout = new SpinnerNumberModel(3, 1, 10, 1);
        JSpinner spinnerScanTimeout = new JSpinner(modelScanTimeout);
        panelScanTimeout.setLayout(new BorderLayout());
        panelScanTimeout.add(new JLabel("Timeout[sec]: "), BorderLayout.WEST);
        panelScanTimeout.add(spinnerScanTimeout, BorderLayout.CENTER);

        JPanel panelServiceTimeout = new JPanel();
        modelServiceTimeout = new SpinnerNumberModel(3, 1, 10, 1);
        JSpinner spinnerServiceTimeout = new JSpinner(modelServiceTimeout);
        panelServiceTimeout.setLayout(new BorderLayout());
        panelServiceTimeout.add(new JLabel("Timeout[sec]: "), BorderLayout.WEST);
        panelServiceTimeout.add(spinnerServiceTimeout, BorderLayout.CENTER);

        //Layout
        JPanel pBTAContent = new JPanel();
        pBTAContent.setLayout(new BoxLayout(pBTAContent, BoxLayout.PAGE_AXIS));
        pBTAContent.add(scrListBTAdapter);
        pBTAContent.add(panelScanTimeout);
        pBTAContent.add(panelBtnProbeAndScan);
        pBTAContent.add(statusScanDevice);
        LayoutPanel pBTA = new LayoutPanel();
        pBTA.setPadding(5);
        pBTA.setContentBorder(BorderFactory.createTitledBorder("Adapter"));
        pBTA.setContent(pBTAContent);

        JPanel pBTDContent = new JPanel();
        pBTDContent.setLayout(new BoxLayout(pBTDContent, BoxLayout.PAGE_AXIS));
        pBTDContent.add(scrListBTDevice);
        pBTDContent.add(panelServiceTimeout);
        pBTDContent.add(panelBtnService);
        pBTDContent.add(statusScanService);
        LayoutPanel pBTD = new LayoutPanel();
        pBTD.setPadding(5);
        pBTD.setContentBorder(BorderFactory.createTitledBorder("Device"));
        pBTD.setContent(pBTDContent);

        JPanel pSRVContent = new JPanel();
        pSRVContent.setLayout(new BoxLayout(pSRVContent, BoxLayout.PAGE_AXIS));
        pSRVContent.add(scrListGattService);
        pSRVContent.add(panelBtnCharacteristic);
        pSRVContent.add(statusScanCharactersitic);
        LayoutPanel pSRV = new LayoutPanel();
        pSRV.setPadding(5);
        pSRV.setContentBorder(BorderFactory.createTitledBorder("GATT Service"));
        pSRV.setContent(pSRVContent);

        JPanel pCHRContent = new JPanel();
        pCHRContent.setLayout(new BoxLayout(pCHRContent, BoxLayout.PAGE_AXIS));
        pCHRContent.add(scrListGattCharacteristic);
        pCHRContent.add(panelBtnTxRx);
        LayoutPanel pCHR = new LayoutPanel();
        pCHR.setPadding(5);
        pCHR.setContentBorder(BorderFactory.createTitledBorder("GATT Characteristic"));
        pCHR.setContent(pCHRContent);

        JPanel pTSTContent = new JPanel();
        pTSTContent.setLayout(new BoxLayout(pTSTContent, BoxLayout.PAGE_AXIS));
        pTSTContent.add(panelTestSetting);
        pTSTContent.add(panelBtnSettings);
        LayoutPanel pTST = new LayoutPanel();
        pTST.setPadding(5);
        pTST.setContentBorder(BorderFactory.createTitledBorder("Test Settings"));
        pTST.setContent(pTSTContent);

        JPanel pRUNContent = new JPanel();
        pRUNContent.setLayout(new BoxLayout(pRUNContent, BoxLayout.PAGE_AXIS));
        pRUNContent.add(scrTextLog);
        pRUNContent.add(panelBtnRun);
        pRUNContent.add(statusTest);
        LayoutPanel pRUN = new LayoutPanel();
        pRUN.setPadding(5);
        pRUN.setContentBorder(BorderFactory.createTitledBorder("Test"));
        pRUN.setContent(pRUNContent);

        setLayout(new GridLayout(2, 3));
        add(pBTA);
        add(pBTD);
        add(pSRV);
        add(pCHR);
        add(pTST);
        add(pRUN);
    }

    public DefaultListModel<BTAdapterItem> getModelBTAdapter() {
        return modelBTAdapter;
    }

    private DefaultListModel<BTDeviceItem> getModelBTDevice() {
        return modelBTDevice;
    }

    private DefaultListModel<GattServiceItem> getModelGattService() {
        return modelGattService;
    }

    private DefaultListModel<GattCharacteristicItem> getModelGattCharacteristic() {
        return modelGattCharacteristic;
    }

    private JTextArea getTextLog() {
        return textLog;
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return BTAdapter;
    }

    private void setBluetoothAdapter(BluetoothAdapter ada) {
        BTAdapter = ada;
    }

    private BluetoothDevice getBluetoothDevice() {
        return BTDevice;
    }

    private void setBluetoothDevice(BluetoothDevice dev) {
        BTDevice = dev;
    }

    private BluetoothGattService getBluetoothGattService() {
        return BTGattService;
    }

    private void setBluetoothGattService(BluetoothGattService srv) {
        BTGattService = srv;
    }

    private BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        return BTGattCharacteristic;
    }

    private void setBluetoothGattCharacteristic(BluetoothGattCharacteristic chr) {
        BTGattCharacteristic = chr;
    }

    public String getTextBTAdapterAddress() {
        return textBTAdapterAddress.getText();
    }

    public void setTextBTAdapterAddress(String t) {
        textBTAdapterAddress.setText(t);
        textBTAdapterAddress.setCaretPosition(0);
    }

    public String getTextBTDeviceAddress() {
        return textBTDeviceAddress.getText();
    }

    public void setTextBTDeviceAddress(String t) {
        textBTDeviceAddress.setText(t);
        textBTDeviceAddress.setCaretPosition(0);
    }

    public String getTextBTGattServiceUuid() {
        return textBTGattServiceUuid.getText();
    }

    public void setTextBTGattServiceUuid(String t) {
        textBTGattServiceUuid.setText(t);
        textBTGattServiceUuid.setCaretPosition(0);
    }

    public String getTextBTGattTxUuid() {
        return textBTGattTxUuid.getText();
    }

    public void setTextBTGattTxUuid(String t) {
        textBTGattTxUuid.setText(t);
        textBTGattTxUuid.setCaretPosition(0);
    }

    public String getTextBTGattRxUuid() {
        return textBTGattRxUuid.getText();
    }

    public void setTextBTGattRxUuid(String t) {
        textBTGattRxUuid.setText(t);
        textBTGattRxUuid.setCaretPosition(0);
    }

    private boolean getAdapterEnabled() {
        return buttonScan.isEnabled();
    }

    private void setAdapterEnabled(boolean b) {
        buttonUseAdapter.setEnabled(b);
        buttonScan.setEnabled(b);
    }

    private boolean getDeviceEnabled() {
        return buttonService.isEnabled();
    }

    private void setDeviceEnabled(boolean b) {
        buttonUseDevice.setEnabled(b);
        buttonService.setEnabled(b);
    }

    private boolean getServiceEnabled() {
        return buttonCharacteristic.isEnabled();
    }

    private void setServiceEnabled(boolean b) {
        buttonUseService.setEnabled(b);
        buttonCharacteristic.setEnabled(b);
    }

    private boolean getCharacteristicEnabled() {
        return buttonUseRx.isEnabled();
    }

    private void setCharacteristicEnabled(boolean b) {
        buttonUseTx.setEnabled(b);
        buttonUseRx.setEnabled(b);
    }

    private int getScanTimeout() {
        return modelScanTimeout.getNumber().intValue();
    }

    private int getServiceTimeout() {
        return modelServiceTimeout.getNumber().intValue();
    }

    private void setStatusScanDeviceText(String txt) {
        statusScanDevice.setText(txt);
        statusScanDevice.setCaretPosition(0);
    }

    private void setStatusScanServiceText(String txt) {
        statusScanService.setText(txt);
        statusScanService.setCaretPosition(0);
    }

    private void setStatusScanCharactersiticText(String txt) {
        statusScanCharactersitic.setText(txt);
        statusScanCharactersitic.setCaretPosition(0);
    }

    private void setStatusTest(String txt) {
        statusTest.setText(txt);
        statusTest.setCaretPosition(0);
    }

    public class AdapterSelection implements ListSelectionListener {
        PanelBTScan panelBTScan;
        JList<BTAdapterItem> list;

        public AdapterSelection(PanelBTScan p, JList<BTAdapterItem> l) {
            panelBTScan = p;
            list = l;
        }

        @Override
        public void valueChanged(ListSelectionEvent ev) {
            panelBTScan.getModelBTDevice().clear();
            panelBTScan.setAdapterEnabled(false);
            panelBTScan.setStatusScanDeviceText("");

            if (list.isSelectionEmpty()) {
                return;
            }

            BluetoothAdapter ada = list.getModel().getElementAt(list.getSelectedIndex()).getBluetoothAdapter();
            panelBTScan.setBluetoothAdapter(ada);
            panelBTScan.setAdapterEnabled(true);
        }
    }

    public class DeviceSelection implements ListSelectionListener {
        PanelBTScan panelBTScan;
        JList<BTDeviceItem> list;

        public DeviceSelection(PanelBTScan p, JList<BTDeviceItem> l) {
            panelBTScan = p;
            list = l;
        }

        @Override
        public void valueChanged(ListSelectionEvent ev) {
            panelBTScan.getModelGattService().clear();
            panelBTScan.setDeviceEnabled(false);
            panelBTScan.setStatusScanServiceText("");

            if (list.isSelectionEmpty()) {
                return;
            }

            BluetoothDevice dev = list.getModel().getElementAt(list.getSelectedIndex()).getBluetoothDevice();
            panelBTScan.setBluetoothDevice(dev);
            panelBTScan.setDeviceEnabled(true);
        }
    }

    public class ServiceSelection implements ListSelectionListener {
        PanelBTScan panelBTScan;
        JList<GattServiceItem> list;

        public ServiceSelection(PanelBTScan p, JList<GattServiceItem> l) {
            panelBTScan = p;
            list = l;
        }

        @Override
        public void valueChanged(ListSelectionEvent ev) {
            panelBTScan.getModelGattCharacteristic().clear();
            panelBTScan.setServiceEnabled(false);
            panelBTScan.setStatusScanCharactersiticText("");

            if (list.isSelectionEmpty()) {
                return;
            }

            BluetoothGattService srv = list.getModel().getElementAt(list.getSelectedIndex()).getBluetoothGattService();
            panelBTScan.setBluetoothGattService(srv);
            panelBTScan.setServiceEnabled(true);
        }
    }

    public class CharacteristicSelection implements ListSelectionListener {
        PanelBTScan panelBTScan;
        JList<GattCharacteristicItem> list;

        public CharacteristicSelection(PanelBTScan p, JList<GattCharacteristicItem> l) {
            panelBTScan = p;
            list = l;
        }

        @Override
        public void valueChanged(ListSelectionEvent ev) {
            panelBTScan.setCharacteristicEnabled(false);

            if (list.isSelectionEmpty()) {
                return;
            }

            BluetoothGattCharacteristic chr = list.getModel().getElementAt(list.getSelectedIndex()).getBluetoothGattCharacteristic();
            panelBTScan.setBluetoothGattCharacteristic(chr);
            panelBTScan.setCharacteristicEnabled(true);
        }
    }

    public class ActionButton implements ActionListener {
        PanelBTScan panelBTScan;

        public ActionButton(PanelBTScan p) {
            panelBTScan = p;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String cmd = ev.getActionCommand();

            if (cmd.equalsIgnoreCase(ACT_PROBE)) {
                actionProbeAdapter();
            }
            if (cmd.equalsIgnoreCase(ACT_USE_ADAPTER)) {
                actionUseAdapter();
            }
            if (cmd.equalsIgnoreCase(ACT_SCAN)) {
                actionScanDevice();
            }
            if (cmd.equalsIgnoreCase(ACT_USE_DEVICE)) {
                actionUseDevice();
            }
            if (cmd.equalsIgnoreCase(ACT_SERVICE)) {
                actionScanService();
            }
            if (cmd.equalsIgnoreCase(ACT_USE_SERVICE)) {
                actionUseService();
            }
            if (cmd.equalsIgnoreCase(ACT_CHARACTERISTIC)) {
                actionScanCharacteristic();
            }
            if (cmd.equalsIgnoreCase(ACT_USE_TX)) {
                actionUseGattTx();
            }
            if (cmd.equalsIgnoreCase(ACT_USE_RX)) {
                actionUseGattRx();
            }
            if (cmd.equalsIgnoreCase(ACT_SET_DEFAULT)) {
                actionUseDefaultGatt();
            }
            if (cmd.equalsIgnoreCase(ACT_RUN_TEST)) {
                actionRunTest();
            }
        }

        public void actionProbeAdapter() {
            DefaultListModel<BTAdapterItem> listBTA = panelBTScan.getModelBTAdapter();

            listBTA.clear();

            DeviceManager deviceManager = DeviceManager.getInstance();
            List<BluetoothAdapter> adapters = deviceManager.getAdapters();
            if (adapters.isEmpty()) {
                System.err.println("Error: There is no bluetooth adapter.");
                panelBTScan.setStatusScanDeviceText("No bluetooth adapter.");
                return;
            }
            for (BluetoothAdapter ada : adapters) {
                listBTA.addElement(new BTAdapterItem(ada));
            }

            panelBTScan.setStatusScanDeviceText(adapters.size() + " adapter(s) found.");
        }

        public void actionUseAdapter() {
            BluetoothAdapter adapter = panelBTScan.getBluetoothAdapter();

            panelBTScan.setTextBTAdapterAddress(adapter.getAddress());
        }

        public void actionScanDevice() {
            DefaultListModel<BTDeviceItem> listBTD = panelBTScan.getModelBTDevice();
            BluetoothAdapter adapter = panelBTScan.getBluetoothAdapter();

            listBTD.clear();

            panelBTScan.setStatusScanDeviceText("Searching device...");
            DeviceManager deviceManager = DeviceManager.getInstance();
            System.out.println("discovery...");
            adapter.startDiscovery();
            try {
                Thread.sleep((long) panelBTScan.getScanTimeout() * 1000);
            } catch (InterruptedException e) {
                //ignore
            }
            adapter.stopDiscovery();
            System.out.println("discovery done...");
            List<BluetoothDevice> devices = deviceManager.getDevices(true);
            if (devices.isEmpty()) {
                System.err.println("Error: There is no bluetooth device.");
                panelBTScan.setStatusScanDeviceText("No bluetooth device.");
                return;
            }
            for (BluetoothDevice dev : devices) {
                listBTD.addElement(new BTDeviceItem(dev));
            }

            panelBTScan.setStatusScanDeviceText(devices.size() + " device(s) found.");
        }

        public void actionUseDevice() {
            BluetoothDevice device = panelBTScan.getBluetoothDevice();

            panelBTScan.setTextBTDeviceAddress(device.getAddress());
        }

        public void actionScanService() {
            DefaultListModel<GattServiceItem> listGattService = panelBTScan.getModelGattService();
            BluetoothDevice device = panelBTScan.getBluetoothDevice();
            AtomicBoolean finish = new AtomicBoolean(false);
            int retry;

            listGattService.clear();

            panelBTScan.setStatusScanServiceText("Searching service...");
            Thread t = new Thread(() -> {
                device.connect();
                finish.set(true);
            });
            t.start();

            retry = 0;
            while (!finish.get()) {
                System.out.println("connect...");
                if (retry >= panelBTScan.getServiceTimeout() * 5) {
                    t.interrupt();
                    break;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    //ignore
                }
                retry += 1;
            }
            if (!finish.get()) {
                System.err.println("Error: Cannot connect to bluetooth device.");
                panelBTScan.setStatusScanServiceText("Cannot connect to BT device.");
                return;
            }

            device.refreshGattServices();
            List<BluetoothGattService> gattServices = device.getGattServices();
            retry = 0;
            while (gattServices.isEmpty()) {
                System.out.println("search GATT...");
                if (retry >= panelBTScan.getServiceTimeout() * 5) {
                    break;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    //ignore
                }
                retry += 1;

                device.refreshGattServices();
                gattServices = device.getGattServices();
            }

            device.disconnect();

            if (gattServices.isEmpty()) {
                System.err.println("Error: There is no bluetooth GATT service.");
                panelBTScan.setStatusScanServiceText("No bluetooth GATT service.");
                return;
            }
            for (BluetoothGattService srv : gattServices) {
                listGattService.addElement(new GattServiceItem(srv));
            }

            panelBTScan.setStatusScanServiceText(gattServices.size() + " service(s) found.");
        }

        public void actionUseService() {
            BluetoothGattService service = panelBTScan.getBluetoothGattService();

            panelBTScan.setTextBTGattServiceUuid(service.getUuid());
        }

        public void actionScanCharacteristic() {
            DefaultListModel<GattCharacteristicItem> listGattCharacteristic = panelBTScan.getModelGattCharacteristic();
            BluetoothGattService srv = panelBTScan.getBluetoothGattService();
            AtomicBoolean finish = new AtomicBoolean(false);
            int retry;

            listGattCharacteristic.clear();

            panelBTScan.setStatusScanCharactersiticText("Searching characteristics...");
            srv.refreshGattCharacteristics();
            List<BluetoothGattCharacteristic> gattCharacteristics = srv.getGattCharacteristics();
            retry = 0;
            while (gattCharacteristics.isEmpty()) {
                System.out.println("retry");
                if (retry >= panelBTScan.getServiceTimeout()) {
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //ignore
                }
                retry += 1;

                srv.refreshGattCharacteristics();
                gattCharacteristics = srv.getGattCharacteristics();
            }

            if (gattCharacteristics.isEmpty()) {
                System.err.println("Error: There is no bluetooth GATT characteristic.");
                panelBTScan.setStatusScanCharactersiticText("No bluetooth GATT characteristic.");
                return;
            }
            for (BluetoothGattCharacteristic chr : gattCharacteristics) {
                listGattCharacteristic.addElement(new GattCharacteristicItem(chr));
            }

            panelBTScan.setStatusScanCharactersiticText(gattCharacteristics.size() + " characteristic(s) found.");
        }

        public void actionUseGattTx() {
            BluetoothGattCharacteristic chr = panelBTScan.getBluetoothGattCharacteristic();

            panelBTScan.setTextBTGattTxUuid(chr.getUuid());
        }

        public void actionUseGattRx() {
            BluetoothGattCharacteristic chr = panelBTScan.getBluetoothGattCharacteristic();

            panelBTScan.setTextBTGattRxUuid(chr.getUuid());
        }

        public void actionUseDefaultGatt() {
            panelBTScan.setTextBTGattServiceUuid(PanelBTScan.DEFAULT_UUID_SERVICE);
            panelBTScan.setTextBTGattTxUuid(PanelBTScan.DEFAULT_UUID_TX);
            panelBTScan.setTextBTGattRxUuid(PanelBTScan.DEFAULT_UUID_RX);
        }

        public void actionRunTest() {
            BTDeviceStream stream;
            String macada = panelBTScan.getTextBTAdapterAddress();
            String macdev = panelBTScan.getTextBTDeviceAddress();
            String srv = panelBTScan.getTextBTGattServiceUuid();
            String tx = panelBTScan.getTextBTGattTxUuid();
            String rx = panelBTScan.getTextBTGattRxUuid();

            panelBTScan.setStatusTest("");

            long nsStart = System.nanoTime();
            StringBuffer log = new StringBuffer();

            log.append(getTimeStampString(System.nanoTime() - nsStart) + ": > open\n");

            try {
                stream = new BTDeviceStream(macada, macdev, srv, tx, rx, 3);
            } catch (IllegalArgumentException e) {
                panelBTScan.setStatusTest(e.getMessage());
                return;
            } catch (DBusException e) {
                panelBTScan.setStatusTest(e.getMessage());
                return;
            }

            log.append(getTimeStampString(System.nanoTime() - nsStart) + ": > open done\n");

            InputStream in = new BufferedInputStream(stream.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));
            OutputStream out = stream.getOutputStream();
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));

            try {

                for (int i = 0; i < 5; i++) {
                    wr.write("blink\n");
                    wr.flush();
                    log.append(getTimeStampString(System.nanoTime() - nsStart) + ": > blink\n");
                    String sss = rd.readLine();
                    log.append(getTimeStampString(System.nanoTime() - nsStart) + ": " + sss + "\n");
                }
            } catch (IOException e) {
                panelBTScan.setStatusTest(e.getMessage());
            }

            log.append(getTimeStampString(System.nanoTime() - nsStart) + ": > close\n");

            try {
                in.close();
            } catch (IOException e) {
                panelBTScan.setStatusTest(e.getMessage());
            }

            log.append(getTimeStampString(System.nanoTime() - nsStart) + ": > close done\n");

            panelBTScan.getTextLog().setText(log.toString());
        }

        public String getTimeStampString(long ns) {
            long ms = ns / 1000000;
            long sOnly = ms / 1000;
            long msOnly = ms % 1000;

            return String.format("%d.%03d", sOnly, msOnly);
        }
    }
}
