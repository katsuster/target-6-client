package net.katsuster.ble;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BTAdapterSubPanel extends JPanel {
    public static final String ACT_PROBE = "Probe";
    public static final String ACT_USE_ADAPTER = "Use Adapter";
    public static final String ACT_SCAN = "Scan";

    private BTScanPanel parent;
    private JButton buttonUseAdapter;
    private JButton buttonProbe;
    private JButton buttonScan;
    private SpinnerNumberModel modelAdapterTimeout;
    private JTextField statusAdapter;

    public BTAdapterSubPanel(BTScanPanel p) {
        parent = p;

        Dimension preferredListSize = new Dimension(250, (int)(parent.getFontSize() * 1.3 * 8));

        //Status
        statusAdapter = new JTextField();
        statusAdapter.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        statusAdapter.setEditable(false);

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

        //Lists
        JList<BTAdapterItem> listBTAdapter = new JList<>(parent.getModelBTAdapter());
        listBTAdapter.addListSelectionListener(new AdapterSelection(this, listBTAdapter));
        JScrollPane scrListBTAdapter = new JScrollPane(listBTAdapter);
        scrListBTAdapter.setPreferredSize(preferredListSize);

        //Timeout
        JPanel panelAdapterTimeout = new JPanel();
        modelAdapterTimeout = new SpinnerNumberModel(3, 1, 10, 1);
        JSpinner spinnerAdapterTimeout = new JSpinner(modelAdapterTimeout);
        panelAdapterTimeout.setLayout(new BorderLayout());
        panelAdapterTimeout.add(new JLabel("Timeout[sec]: "), BorderLayout.WEST);
        panelAdapterTimeout.add(spinnerAdapterTimeout, BorderLayout.CENTER);

        //Layout
        JPanel pBTAContent = new JPanel();
        pBTAContent.setLayout(new BoxLayout(pBTAContent, BoxLayout.PAGE_AXIS));
        pBTAContent.add(scrListBTAdapter);
        pBTAContent.add(panelAdapterTimeout);
        pBTAContent.add(panelBtnProbeAndScan);
        pBTAContent.add(statusAdapter);
        LayoutPanel pBTA = new LayoutPanel();
        pBTA.setPadding(2, 5, 2, 5);
        pBTA.setContentBorder(BorderFactory.createTitledBorder("Adapter"));
        pBTA.setContent(pBTAContent);

        add(pBTA);
    }

    private DefaultListModel<BTAdapterItem> getModelBTAdapter() {
        return parent.getModelBTAdapter();
    }

    private DefaultListModel<BTDeviceItem> getModelBTDevice() {
        return parent.getModelBTDevice();
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return parent.getBluetoothAdapter();
    }

    private void setBluetoothAdapter(BluetoothAdapter ada) {
        parent.setBluetoothAdapter(ada);
    }

    private void setTextBTAdapterAddress(String t) {
        parent.setTextBTAdapterAddress(t);
    }

    public boolean getAdapterEnabled() {
        return buttonScan.isEnabled();
    }

    public void setAdapterEnabled(boolean b) {
        buttonUseAdapter.setEnabled(b);
        buttonScan.setEnabled(b);
    }

    public int getAdapterTimeout() {
        return modelAdapterTimeout.getNumber().intValue();
    }

    public void setStatusTextAdapter(String txt) {
        statusAdapter.setText(txt);
        statusAdapter.setCaretPosition(0);
    }

    private class AdapterSelection implements ListSelectionListener {
        BTAdapterSubPanel panel;
        JList<BTAdapterItem> list;

        public AdapterSelection(BTAdapterSubPanel p, JList<BTAdapterItem> l) {
            panel = p;
            list = l;
        }

        @Override
        public void valueChanged(ListSelectionEvent ev) {
            panel.getModelBTDevice().clear();
            panel.setAdapterEnabled(false);
            panel.setStatusTextAdapter("");

            if (list.isSelectionEmpty()) {
                return;
            }

            BluetoothAdapter ada = list.getModel().getElementAt(list.getSelectedIndex()).getBluetoothAdapter();
            panel.setBluetoothAdapter(ada);
            panel.setAdapterEnabled(true);
        }
    }

    private class ActionButton implements ActionListener {
        BTAdapterSubPanel panel;

        public ActionButton(BTAdapterSubPanel p) {
            panel = p;
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
        }

        public void actionProbeAdapter() {
            DefaultListModel<BTAdapterItem> listBTA = panel.getModelBTAdapter();

            listBTA.clear();

            DeviceManager deviceManager = DeviceManager.getInstance();
            List<BluetoothAdapter> adapters = deviceManager.getAdapters();
            if (adapters.isEmpty()) {
                System.err.println("Error: There is no bluetooth adapter.");
                panel.setStatusTextAdapter("No bluetooth adapter.");
                return;
            }
            for (BluetoothAdapter ada : adapters) {
                listBTA.addElement(new BTAdapterItem(ada));
            }

            panel.setStatusTextAdapter(adapters.size() + " adapter(s) found.");
        }

        public void actionUseAdapter() {
            BluetoothAdapter adapter = panel.getBluetoothAdapter();

            panel.setTextBTAdapterAddress(adapter.getAddress());
        }

        public void actionScanDevice() {
            DefaultListModel<BTDeviceItem> listBTD = panel.getModelBTDevice();
            BluetoothAdapter adapter = panel.getBluetoothAdapter();

            listBTD.clear();

            panel.setStatusTextAdapter("Searching device...");
            DeviceManager deviceManager = DeviceManager.getInstance();
            System.out.println("discovery...");
            adapter.startDiscovery();
            try {
                Thread.sleep((long) panel.getAdapterTimeout() * 1000);
            } catch (InterruptedException e) {
                //ignore
            }
            adapter.stopDiscovery();
            System.out.println("discovery done...");
            List<BluetoothDevice> devices = deviceManager.getDevices(true);
            if (devices.isEmpty()) {
                System.err.println("Error: There is no bluetooth device.");
                panel.setStatusTextAdapter("No bluetooth device.");
                return;
            }
            for (BluetoothDevice dev : devices) {
                listBTD.addElement(new BTDeviceItem(dev));
            }

            panel.setStatusTextAdapter(devices.size() + " device(s) found.");
        }
    }
}
