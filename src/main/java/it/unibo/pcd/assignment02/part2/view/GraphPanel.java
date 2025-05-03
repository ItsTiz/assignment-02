package it.unibo.pcd.assignment02.part2.view;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {

    public GraphPanel() {
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Placeholder: draw a grid or mock edges
        g.drawString("Graph will go here", 10, 20);
    }

}
