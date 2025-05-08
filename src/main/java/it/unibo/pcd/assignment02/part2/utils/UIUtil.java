package it.unibo.pcd.assignment02.part2.utils;

import java.awt.*;
import java.util.Random;
public class UIUtil {

    public static final double NODE_RADIUS = 30.0;
    public static final int INTRA_PACKAGE_DISTANCE = 50;
    public static final int LABEL_OFFSET_X = 25;
    public static final int LABEL_OFFSET_Y = 20;
    public static final double ARROW_ANGLE = Math.toRadians(25);
    public static final float EDGE_STROKE_WIDTH = 1.5f;
    public static final int ARROW_TIP_SIZE = 10;
    public static final Color ARROW_BODY_COLOR = Color.LIGHT_GRAY;
    public static final Color ARROW_TIP_COLOR = Color.DARK_GRAY;
    public static final int LAYOUT_COLUMNS = 5;
    public static final int LAYOUT_COLUMN_SPACING = 150;
    public static final int LAYOUT_ROW_SPACING = 200;

    private static final Random random = new Random();

    public static Pair<Point, Point> adjustCoordinates(Point from, Point to) {
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int x = from.x + (int) (Math.cos(angle) * NODE_RADIUS * 0.5);
        int y = from.y + (int) (Math.sin(angle) * NODE_RADIUS * 0.5);
        int x2 = to.x - (int) (Math.cos(angle) * NODE_RADIUS * 0.5);
        int y2 = to.y - (int) (Math.sin(angle) * NODE_RADIUS * 0.5);
        return new Pair<> (new Point(x, y), new Point(x2, y2));
    }


    public static Color getRandomColor() {
        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        return new Color(r, g, b);
    }
}
