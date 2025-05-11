package it.unibo.pcd.assignment02.part2.view;

import it.unibo.pcd.assignment02.part2.model.Edge;
import it.unibo.pcd.assignment02.part2.model.Node;
import it.unibo.pcd.assignment02.part2.utils.Pair;

import static it.unibo.pcd.assignment02.part2.utils.UIUtil.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GraphPanel extends JPanel {

    private final DepAnalyserView parent;
    private final Map<String, Set<Node>> packagesToNodes = new HashMap<>();
    private final Map<String, Color> packageToColors = new HashMap<>();
    private final Map<String, Map<Node, Point>> globalPositions = new HashMap<>();
    private final List<Edge> edgesToPaint = new ArrayList<>();

    private Node selectedNode = null;

    public GraphPanel(DepAnalyserView parent) {
        this.parent = parent;
        setBackground(Color.WHITE);
        addMouseListener(createMouseListener());
    }

    private MouseAdapter createMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                mouseHandler(e);
            }

            private void mouseHandler(MouseEvent e) {
                Point click = e.getPoint();
                for (Map.Entry<Node, Point> entry : getActualEdges().entrySet()) {
                    if (Math.abs(entry.getValue().x - click.x) <= NODE_RADIUS
                            && Math.abs(entry.getValue().y - click.y) <= NODE_RADIUS) {
                        selectedNode = entry.getKey();
                        repaint();
                        parent.inspectNode(entry.getKey());
                        break;
                    }
                }
            }
        };
    }

    public void resetSelectedNode() {
        selectedNode = null;
        repaint();
    }

    public Map<Node, Point> getActualEdges(){
        return globalPositions.values().stream()
                .flatMap(innerMap -> innerMap.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (p1, p2) -> p1
                ));
    }

    public void requestNodePaint(Node node) {
        packagesToNodes
                .computeIfAbsent(node.packageName, key -> new HashSet<>())
                .add(node);
        packageToColors
                .computeIfAbsent(node.packageName, key -> getRandomColor());
        repaint();
    }

    public void requestEdgePaint(Edge toPaint) {
        edgesToPaint.add(toPaint);
        repaint();
    }

    private Map<Node, Point> preparePackage(String packageName, Point center) {
        Set<Node> nodes = packagesToNodes.get(packageName);
        int nodeCount = nodes.size();
        int angleStep = 360 / nodeCount;
        int i = 0;

        Map<Node, Point> positions = new HashMap<>();

        for (Node node : nodes) {
            double angle = Math.toRadians(i * angleStep);
            int x = center.x + (int)(INTRA_PACKAGE_DISTANCE * Math.cos(angle));
            int y = center.y + (int)(INTRA_PACKAGE_DISTANCE * Math.sin(angle));
            positions.put(node, new Point(x, y));
            i++;
        }

        return positions;
    }

    private void prepareAllPackages() {
        int panelWidth = getWidth();
        int index = 0;

        for (String packageName : packagesToNodes.keySet()) {
            int row = index / LAYOUT_COLUMNS;
            int column = index % LAYOUT_COLUMNS;

            int centerX = (panelWidth / LAYOUT_COLUMNS) * column + (panelWidth / LAYOUT_COLUMNS) / 2;
            int centerY = LAYOUT_COLUMN_SPACING + row * LAYOUT_ROW_SPACING;

            if (centerY >= parent.getScreenHeight()) {
                parent.setGraphPanelHeight(centerY + LAYOUT_ROW_SPACING);
            }

            Map<Node, Point> positions = preparePackage(packageName, new Point(centerX, centerY));
            globalPositions.put(packageName, positions);
            index++;
        }
    }

    private void printNodesByPackage(String packageName, Graphics g, Map<Node, Point> positions) {
        Color packageColor = packageToColors.get(packageName);

        for (Map.Entry<Node, Point> entry : positions.entrySet()) {
            Node node = entry.getKey();
            Point p = entry.getValue();
            drawNodeGraphics(g, node.name, p, packageColor);
        }
    }

    private void drawNodes(Graphics g) {
        for (String packageName : packagesToNodes.keySet()) {
            Map<Node, Point> positions = globalPositions.get(packageName);
            printNodesByPackage(packageName, g, positions);
        }
    }

    private void drawNodeGraphics(Graphics g, String name, Point pos, Color randomColor) {
        int radius = (int) NODE_RADIUS;
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(randomColor);
        g2.fillOval(pos.x - radius / 2, pos.y - radius / 2, radius, radius);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(pos.x - radius / 2, pos.y - radius / 2, radius, radius);
        g2.drawString(name, pos.x - LABEL_OFFSET_X, pos.y - LABEL_OFFSET_Y);
    }

    private void drawEdges(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Map<Node, Point> positions = getActualEdges();

        g2.setStroke(new BasicStroke(EDGE_STROKE_WIDTH));
        for (Edge edge : edgesToPaint) {
            Point from = positions.get(edge.getFromNode());
            Point to = positions.get(edge.getToNode());

            if (from != null && to != null) {
                Pair<Point, Point> adjustedCoord = adjustCoordinates(from, to);
                if (selectedNode != null && selectedNode.equals(edge.getFromNode())) {
                    g2.setColor(Color.RED);
                    g2.setStroke(new BasicStroke(EDGE_STROKE_WIDTH + 1));
                } else {
                    g2.setColor(ARROW_BODY_COLOR);
                    g2.setStroke(new BasicStroke(EDGE_STROKE_WIDTH));
                }
                drawArrow(g2, adjustedCoord.getFirst(), adjustedCoord.getSecond());
            }
        }
    }

    private void drawArrow(Graphics2D g2, Point from, Point to) {
        g2.drawLine(from.x, from.y, to.x, to.y);

        double dy = to.y - from.y;
        double dx = to.x - from.x;
        double theta = Math.atan2(dy, dx);

        double x, y;
        g2.setColor(ARROW_TIP_COLOR);
        for (int j = -1; j <= 1; j += 2) {
            double rho = theta + j * ARROW_ANGLE;
            x = to.x - ARROW_TIP_SIZE * Math.cos(rho);
            y = to.y - ARROW_TIP_SIZE * Math.sin(rho);
            g2.drawLine(to.x, to.y, (int) x, (int) y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        prepareAllPackages();
        drawEdges(g);
        drawNodes(g);
    }
}

