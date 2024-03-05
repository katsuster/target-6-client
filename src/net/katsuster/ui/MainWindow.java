package net.katsuster.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;

public class MainWindow extends JFrame {
    private Image img;

    public MainWindow() {
        setTitle("Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setUndecorated(true);
        setSize(1024, 768);
    }

    public void setImg(VolatileImage i) {
        img = i;
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        if (img != null) {
            g.drawImage(img, 0, 0, this);
        }
    }
}
