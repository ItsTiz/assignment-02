package it.unibo.pcd.assignment02.part1.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Helper {

    public static boolean hasJavaFiles(Path dir) {
        try (Stream<Path> entries = Files.list(dir)) {
            return entries.anyMatch(file -> file.toString().endsWith(".java"));
        } catch (IOException e) {
            return false;
        }
    }
}
