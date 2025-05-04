package it.unibo.pcd.assignment02.part2.utils;

import java.awt.Color;
import java.util.Random;
public class UIUtil {
    private static final Random random = new Random();

    public static Color getRandomColor() {
        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        return new Color(r, g, b);
    }
}
