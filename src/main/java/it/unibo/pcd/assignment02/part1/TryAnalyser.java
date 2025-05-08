package it.unibo.pcd.assignment02.part1;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import it.unibo.pcd.assignment02.part1.reports.*;

import java.nio.file.Path;
import java.util.Set;

public class TryAnalyser {

    public static void main(String[] args) {
        AsyncDependencyAnalyser analyser = new AsyncDependencyAnalyser();

        analyser.getClassDependencies(Path.of("C:/Users/Tiziano/Desktop/Tiziano/UNI/Magistrale/Corsi/First year/PCD/assignment-02/src/main/java/it/unibo/pcd/assignment02/part1/AsyncDependencyAnalyser.java"))
                .onComplete(createHandler("----- Class Report -----", TryAnalyser::printClassReport));

        analyser.getPackageDependencies(Path.of("C:/Users/Tiziano/Desktop/Tiziano/UNI/Magistrale/Corsi/First year/PCD/assignment-02/src/main/java/it/unibo/pcd/assignment02/part1"))
                .onComplete(createHandler("----- Package Report -----", TryAnalyser::printPackageReport));

        long start = System.nanoTime();

        analyser.getProjectDependencies(Path.of("C:/Users/Tiziano/Desktop/Tiziano/UNI/Triennale/Test/Alchemist fork/Alchemist"))
                .onComplete(createHandler("----- Project Report -----", TryAnalyser::printProjectReport));

        long elapsed = System.nanoTime() - start;
        System.out.println("Elapsed: " + elapsed / 1_000_000.0 + " ms");
    }

    private static <T extends Report> Handler<AsyncResult<T>> createHandler(String header, Handler<T> onSuccess) {
        return result -> {
            if (result.succeeded()) {
                System.out.println("\n" + header);
                onSuccess.handle(result.result());
            } else {
                System.err.println("Error retrieving report: " + result.cause().getMessage());
            }
        };
    }

    private static void printClassReport(ClassDepsReport report) {
        System.out.println("Class Path: " + report.getClassPath());
        System.out.println("Top Level Type: " + report.getTopLevelType());
        System.out.println("Package: " + report.getPackageDeclaration());
        printDependencies(report);
    }

    private static void printPackageReport(PackageDepsReport report) {
        System.out.println("Package Path: " + report.getPackagePath());
        printDependencies(report);
    }

    private static void printProjectReport(ProjectDepsReport report) {
        System.out.println("Project Root Path: " + report.getProjectPath());
        printDependencies(report);
    }

    private static void printDependencies(Report report) {
        Set<String> types = report.getTypes();
        Set<String> dependencies = report.getDependencies();

        System.out.println("Types (" + types.size() + "):");
        types.forEach(type -> System.out.println("  - " + type));

        System.out.println("Dependencies (" + dependencies.size() + "):");
        dependencies.forEach(dep -> System.out.println("  - " + dep));
    }
}
