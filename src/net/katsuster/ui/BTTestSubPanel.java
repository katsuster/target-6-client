package net.katsuster.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import org.freedesktop.dbus.exceptions.DBusException;
import net.katsuster.ble.BTStream;

public class BTTestSubPanel extends JPanel {
    public static final String ACT_RUN_TEST = "Run";

    BTScanPanel parent;
    private JTextArea textLog;
    private JTextField statusTest;

    public BTTestSubPanel(BTScanPanel p) {
        parent = p;

        Dimension preferredListSize = new Dimension(250, (int)(parent.getFontSize() * 1.3 * 8));

        //Status
        statusTest = new JTextField();
        statusTest.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        statusTest.setEditable(false);

        //Buttons
        ActionButton actButton = new ActionButton(this);

        JButton buttonRun = new JButton(ACT_RUN_TEST);
        buttonRun.addActionListener(actButton);

        JPanel panelBtnRun = new JPanel();
        panelBtnRun.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBtnRun.add(buttonRun);

        //Log
        textLog = new JTextArea();
        textLog.setEditable(false);
        JScrollPane scrTextLog = new JScrollPane(textLog);
        scrTextLog.setPreferredSize(preferredListSize);

        //Layout
        JPanel pRUNContent = new JPanel();
        pRUNContent.setLayout(new BoxLayout(pRUNContent, BoxLayout.PAGE_AXIS));
        pRUNContent.add(scrTextLog);
        pRUNContent.add(panelBtnRun);
        pRUNContent.add(statusTest);
        LayoutPanel pRUN = new LayoutPanel();
        pRUN.setPadding(2, 5, 2, 5);
        pRUN.setContentBorder(BorderFactory.createTitledBorder("Test"));
        pRUN.setContent(pRUNContent);

        add(pRUN);
    }

    private String getTextBTAdapterAddress() {
        return parent.getTextBTAdapterAddress();
    }

    private void setTextBTAdapterAddress(String t) {
        parent.setTextBTAdapterAddress(t);
    }

    private String getTextBTDeviceAddress() {
        return parent.getTextBTDeviceAddress();
    }

    private void setTextBTDeviceAddress(String t) {
        parent.setTextBTDeviceAddress(t);
    }

    private String getTextBTGattServiceUuid() {
        return parent.getTextBTGattServiceUuid();
    }

    private void setTextBTGattServiceUuid(String t) {
        parent.setTextBTGattServiceUuid(t);
    }

    private String getTextBTGattTxUuid() {
        return parent.getTextBTGattTxUuid();
    }

    private void setTextBTGattTxUuid(String t) {
        parent.setTextBTGattTxUuid(t);
    }

    private String getTextBTGattRxUuid() {
        return parent.getTextBTGattRxUuid();
    }

    private void setTextBTGattRxUuid(String t) {
        parent.setTextBTGattRxUuid(t);
    }

    public JTextArea getTextLog() {
        return textLog;
    }

    public void setStatusTest(String txt) {
        statusTest.setText(txt);
        statusTest.setCaretPosition(0);
    }

    private class ActionButton implements ActionListener {
        BTTestSubPanel panel;

        public ActionButton(BTTestSubPanel p) {
            panel = p;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String cmd = ev.getActionCommand();

            if (cmd.equalsIgnoreCase(ACT_RUN_TEST)) {
                actionRunTest();
            }
        }

        public void actionRunTest() {
            String macada = panel.getTextBTAdapterAddress();
            String macdev = panel.getTextBTDeviceAddress();
            String srv = panel.getTextBTGattServiceUuid();
            String tx = panel.getTextBTGattTxUuid();
            String rx = panel.getTextBTGattRxUuid();

            panel.setStatusTest("Test Start...");
            panel.getTextLog().setText("");

            Thread testThread = new Thread(() -> {
                BTStream stream;
                long nsStart = System.nanoTime();

                SwingUtilities.invokeLater(() -> {
                    addLogWithTime(nsStart, ": > open\n");
                });

                try {
                    stream = new BTStream(macada, macdev, srv, tx, rx, 3);
                } catch (IllegalArgumentException | DBusException ex) {
                    System.err.println("Error: failed to create bluetooth stream.");
                    System.err.println("  msg:" + ex.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        panel.setStatusTest(ex.getMessage());
                    });
                    return;
                } catch (InterruptedException ex) {
                    System.err.println("Error: interrupted in BTStream.");
                    System.err.println("  msg:" + ex.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        panel.setStatusTest(ex.getMessage());
                    });
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    addLogWithTime(nsStart, ": > open done\n");
                });

                InputStream in = new BufferedInputStream(stream.getInputStream());
                BufferedReader rd = new BufferedReader(new InputStreamReader(in));
                OutputStream out = stream.getOutputStream();
                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));

                try {
                    for (int i = 0; i < 5; i++) {
                        wr.write("blink\n");
                        wr.flush();
                        SwingUtilities.invokeLater(() -> {
                            addLogWithTime(nsStart, ": > blink\n");
                        });
                        String sss = rd.readLine();
                        SwingUtilities.invokeLater(() -> {
                            addLogWithTime(nsStart, ": " + sss + "\n");
                        });
                    }
                } catch (IOException ex) {
                    System.err.println("Error: failed to read or write error to bluetooth device.");
                    System.err.println("  msg:" + ex.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        panel.setStatusTest(ex.getMessage());
                    });
                }

                SwingUtilities.invokeLater(() -> {
                    addLogWithTime(nsStart, ": > close\n");
                });

                try {
                    in.close();
                } catch (IOException ex) {
                    System.err.println("Error: failed to close bluetooth stream.");
                    System.err.println("  msg:" + ex.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        panel.setStatusTest(ex.getMessage());
                    });
                }

                SwingUtilities.invokeLater(() -> {
                    addLogWithTime(nsStart, ": > close done\n");
                });

                SwingUtilities.invokeLater(() -> {
                    panel.setStatusTest("Done.");
                });
            });
            testThread.start();
        }

        public void addLogWithTime(long nsStart, String msg) {
            String log = panel.getTextLog().getText();
            panel.getTextLog().setText(log + getTimeStampString(System.nanoTime() - nsStart) + msg);
        }

        public String getTimeStampString(long ns) {
            long ms = ns / 1000000;
            long sOnly = ms / 1000;
            long msOnly = ms % 1000;

            return String.format("%d.%03d", sOnly, msOnly);
        }
    }
}
