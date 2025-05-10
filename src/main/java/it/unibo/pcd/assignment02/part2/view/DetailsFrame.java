package it.unibo.pcd.assignment02.part2.view;

import it.unibo.pcd.assignment02.part2.model.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DetailsFrame {

    private final JDialog dialog;

    private DetailsFrame(DepAnalyserView parent, Node node) {
        dialog = new JDialog(parent, "Node Info", Dialog.ModalityType.APPLICATION_MODAL); // Modal dialog
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(parent);

        JTextArea info = new JTextArea(node.toString());
        info.setEditable(false);
        dialog.add(new JScrollPane(info), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        dialog.add(closeBtn, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                parent.resetSelectedNode();
            }
        });
    }

    public static void showForNode(DepAnalyserView parent, Node node) {
        DetailsFrame details = new DetailsFrame(parent, node);
        details.dialog.setVisible(true);
    }
}
