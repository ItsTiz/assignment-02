package it.unibo.pcd.assignment02.part2.utils;

import java.util.Arrays;

public class Util {

    public static void log(String msg) {
        System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
    }

    public static void err(String msg) {
        System.err.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
    }

    public static String extractName(String address, String splitter) {
        String nodeName = "";
        String[] stringPath = address.split(splitter);
        if(stringPath.length > 0) {
            nodeName = stringPath[stringPath.length - 1];
        }
        if(splitter.equals("\\\\")) {
            nodeName = nodeName.replaceFirst("\\.java$", "");
        }
        return nodeName;
    }

    public static String extractPackageName(String fullyQualifiedName) {
        if (fullyQualifiedName == null || !fullyQualifiedName.contains(".")) {
            return "";
        }
        int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
        return fullyQualifiedName.substring(0, lastDotIndex);
    }
}
