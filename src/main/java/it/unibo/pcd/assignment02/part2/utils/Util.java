package it.unibo.pcd.assignment02.part2.utils;

public class Util {
    public static void log(String msg) {
        System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
    }
    public static void err(String msg) {
        System.err.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
    }
}
