package it.unibo.pcd.assignment02.part2.view;

import it.unibo.pcd.assignment02.part2.model.Edge;
import it.unibo.pcd.assignment02.part2.model.Node;
import it.unibo.pcd.assignment02.part2.utils.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GraphPanel extends JPanel {

    private final Map<String, Set<Node>> packagesToNodes = new HashMap<>();
    private final List<Edge> edgesToPaint = new ArrayList<>();

    public GraphPanel() {
        setBackground(Color.WHITE);
    }

    public List<Node> getActualNodes(){
        return packagesToNodes
                .values()
                .stream()
                .flatMap(Set::stream)
                .collect(Collectors.toList());
    }

    public void requestNodePaint(Node node) {
        packagesToNodes
                .computeIfAbsent(node.packageName, k -> new HashSet<>())
                .add(node);
        repaint();
    }

    public void requestEdgePaint(Edge toPaint) {
        edgesToPaint.add(toPaint);
        repaint(); // Optional: ensure repaint happens after edge request
    }


    private Map<String, Point> preparePackage(String packageName, Point center) {
        Set<Node> nodes = packagesToNodes.get(packageName);
        int nodeCount = nodes.size();
        int angleStep = 360 / nodeCount;
        int i = 0;

        Map<String, Point> positions = new HashMap<>();

        for (Node node : nodes) {
            double angle = Math.toRadians(i * angleStep);
            int x = center.x + (int)(50 * Math.cos(angle)); // 100 radius
            int y = center.y + (int)(50 * Math.sin(angle));
            positions.put(node.name, new Point(x, y));
            i++;
        }

        return positions;
    }


    private void printNodes(Graphics g, Map<String, Point> positions) {
        Color packageColor = UIUtil.getRandomColor();
        for (Map.Entry<String, Point> entry : positions.entrySet()) {
            String name = entry.getKey();
            Point p = entry.getValue();
            drawNodeGraphics(g, name, p , packageColor);
        }
    }

    private void drawNodeGraphics(Graphics g, String name , Point pos, Color randomColor) {
        int radius = 15;
        g.setColor(randomColor);
        g.fillOval(pos.x - radius / 2, pos.y - radius / 2, radius, radius);
        g.setColor(Color.BLACK);
        g.drawString(name, pos.x - 5, pos.y - 10);
    }

    private void drawEdges(Graphics g, Map<String, Point> positions) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1.5f));

        for (Edge edge : edgesToPaint) {
            Point from = positions.get(edge.getFromNode().name);
            Point to = positions.get(edge.getToNode().name);


            if (from != null && to != null) {
                drawArrow(g2, from, to);
            }
        }
    }

    private void drawArrow(Graphics2D g2, Point from, Point to) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(from.x, from.y, to.x, to.y);

        // Arrowhead
        double phi = Math.toRadians(25); // angle of the arrow head
        int barb = 10; // length of arrow head

        double dy = to.y - from.y;
        double dx = to.x - from.x;
        double theta = Math.atan2(dy, dx);

        double x, y;
        g2.setColor(Color.BLACK);
        for (int j = -1; j <= 1; j += 2) {
            double rho = theta + j * phi;
            x = to.x - barb * Math.cos(rho);
            y = to.y - barb * Math.sin(rho);
            g2.drawLine(to.x, to.y, (int) x, (int) y);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();

        int cols = 5;
        int index = 0;

        // This will store ALL node positions globally by node name
        Map<String, Point> globalPositions = new HashMap<>();

        for (String packageName : packagesToNodes.keySet()) {
            int row = index / cols;
            int col = index % cols;

            int centerX = (panelWidth / cols) * col + (panelWidth / cols) / 2;
            int centerY = 150 + row * 200;

            Map<String, Point> positions = preparePackage(packageName, new Point(centerX, centerY));
            printNodes(g, positions);

            // Merge local positions into global map
            globalPositions.putAll(positions);

            index++;
        }

        drawEdges(g, globalPositions);
    }

}
