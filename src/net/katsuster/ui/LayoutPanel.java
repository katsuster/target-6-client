package net.katsuster.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class LayoutPanel extends JPanel {
    private Border border;
    private Insets insetsMargin;
    private Insets insetsPadding;

    private JPanel panelMargin;
    private JPanel panelBorder;
    private JPanel panelPadding;
    private JPanel content;

    public LayoutPanel() {
        super();

        border = BorderFactory.createEmptyBorder();
        insetsMargin = new Insets(0, 0, 0, 0);
        insetsPadding = new Insets(0, 0, 0, 0);

        panelMargin = new JPanel();
        panelMargin.setBorder(BorderFactory.createEmptyBorder());
        panelBorder = new JPanel();
        panelBorder.setLayout(new BorderLayout());
        panelPadding = new JPanel();
        panelPadding.setBorder(BorderFactory.createEmptyBorder());
        panelPadding.setLayout(new BorderLayout());
        content = new JPanel();

        setBorder(BorderFactory.createEmptyBorder());
        setLayout(new BorderLayout());
        refresh();
    }

    public Border getContentBorder() {
        return border;
    }

    public void setContentBorder(Border b) {
        border = b;
    }

    public void setMargin(int margin) {
        setMargin(margin, margin, margin, margin);
    }

    public void setMargin(int top, int left, int bottom, int right) {
        insetsMargin = new Insets(top, left, bottom, right);

        refresh();
    }

    public void setPadding(int padding) {
        setPadding(padding, padding, padding, padding);
    }

    public void setPadding(int top, int left, int bottom, int right) {
        insetsPadding = new Insets(top, left, bottom, right);

        refresh();
    }

    public void setContent(JComponent c) {
        content.removeAll();
        content.add(c);

        refresh();
    }

    protected void refresh() {
        panelPadding.removeAll();
        panelPadding.add(content);
        panelPadding.setBorder(new EmptyBorder(insetsPadding));

        panelBorder.removeAll();
        panelBorder.add(panelPadding);
        panelBorder.setBorder(border);

        panelMargin.removeAll();
        panelMargin.add(panelBorder);
        panelMargin.setBorder(new EmptyBorder(insetsMargin));
        //Cannot use BorderLayout
        //Need margin to write border??
        panelMargin.setLayout(new FlowLayout());

        removeAll();
        add(panelMargin);
    }
}
