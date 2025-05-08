package it.unibo.pcd.assignment02.part2.view;

import it.unibo.pcd.assignment02.part2.controller.InputListener;
import it.unibo.pcd.assignment02.part2.model.Edge;
import it.unibo.pcd.assignment02.part2.model.Node;

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
    private final JFileChooser chooser = new JFileChooser("C:\\Users\\Tiziano\\Desktop\\Tiziano\\UNI\\Magistrale\\Corsi\\First year\\PCD");
    private final JButton chooseProjectButton = new JButton("Choose Project Folder");
    private final JButton startButton = new JButton("Start");
    private final JLabel classCounter = new JLabel("");
    private final JLabel depCounter = new JLabel("");
    private final GraphPanel graphPanel = new GraphPanel(this);
    private final ArrayList<InputListener> listeners = new ArrayList<>();
    private int classCount;
    private int dependenciesCount;
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
        add(topPanel, BorderLayout.NORTH);

        graphPanel.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Tall enough for scrolling

        JScrollPane scrollPane = new JScrollPane(graphPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        chooseProjectButton.addActionListener(e -> notifyFileChooserOpened());
        startButton.addActionListener(this);

        setVisible(true);
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setGraphPanelHeight(int graphPanelHeight) {
        graphPanel.setPreferredSize(new Dimension(screenWidth, graphPanelHeight));
        graphPanel.revalidate();
    }

    public void addListener(InputListener listener) {
        listeners.add(listener);
    }

    private void notifyStarted() {
        for (InputListener listener : listeners) {
            listener.started();
        }
    }

    private void notifyFileChooserOpened() {
        for (InputListener listener : listeners) {
            listener.fileChooserTriggered();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Start")){
            notifyStarted();
            startButton.setEnabled(false);
        }
    }

    public Optional<File> openFolderDialog() {
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            chooseProjectButton.setEnabled(false);
            return Optional.of(chooser.getSelectedFile());
        }
        return Optional.empty();
    }

    public void signalNodePaint(Node toPaint){
        SwingUtilities.invokeLater(() -> {
            graphPanel.requestNodePaint(toPaint);
            classCounter.setText("Classes/Interfaces: "+ (++classCount));
        });
    }

    public void signalEdgePaint(Edge toPaint) {
        SwingUtilities.invokeLater(() -> {
            graphPanel.requestEdgePaint(toPaint);
            depCounter.setText("Dependencies: "+ (++dependenciesCount));
        });
    }

    public void inspectNode(Node node){
        DetailsFrame.showForNode(this, node);
    }
}
