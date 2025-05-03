package it.unibo.pcd.assignment02.part2.view;

import it.unibo.pcd.assignment02.part2.controller.InputListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class DepAnalyserView extends JFrame implements ActionListener {
    private final int screenWidth;
    private final int screenHeight;
    private final JFileChooser chooser = new JFileChooser("C:\\Users\\Tiziano\\Desktop\\Tiziano\\UNI\\Magistrale\\Corsi\\First year\\PCD");;
    private final JButton chooseProjectButton = new JButton("Choose Project Folder");
    private final JButton startButton = new JButton("Start");
    private final JLabel classCounter = new JLabel("Classes: 0");
    private final JLabel depCounter = new JLabel("Dependencies: 0");
    private final GraphPanel graphPanel = new GraphPanel();
    private final ArrayList<InputListener> listeners = new ArrayList<>();

    public DepAnalyserView(int screenWidth, int screenHeight) {
        super("Dependency Analyser");
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setupUI();
    }

    private void setupUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(screenWidth, screenHeight);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(chooseProjectButton);
        topPanel.add(startButton);
        topPanel.add(classCounter);
        topPanel.add(depCounter);

        startButton.addActionListener(this);

        add(topPanel, BorderLayout.NORTH);
        graphPanel.setSize(screenWidth, screenHeight - topPanel.getHeight());
        add(graphPanel, BorderLayout.CENTER);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        setVisible(true);
    }

    public void addListener(InputListener listener) {
        listeners.add(listener);
    }

    private void notifyStarted() {
        for (InputListener listener : listeners) {
            listener.started();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Start")){
            notifyStarted();
        }
    }

    public Optional<File> openFolderDialog() {
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            return Optional.of(chooser.getSelectedFile());
        }
        return Optional.empty();
    }
}
