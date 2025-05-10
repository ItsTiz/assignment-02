package it.unibo.pcd.assignment02.part1;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import it.unibo.pcd.assignment02.part1.reports.*;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.Set;

public class TryAnalyser {

    public static void main(String[] args) {
        AsyncDependencyAnalyser analyser = new AsyncDependencyAnalyser();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the path to a Java class file (IMPORTANT: input folders splitter as \"/\" and not \"\\\"):");
        String classPath = scanner.nextLine();

        System.out.println("Enter the path to a Java package directory:");
        String packagePath = scanner.nextLine();

        System.out.println("Enter the path to a Java project directory:");
        String projectPath = scanner.nextLine();

        analyser.getClassDependencies(Path.of(classPath)).onComplete(classResult -> {
            if (classResult.succeeded()) {
                System.out.println("\n----- Class Report -----");
                printClassReport(classResult.result());
            } else {
                System.err.println("Error retrieving class report: " + classResult.cause().getMessage());
            }

            analyser.getPackageDependencies(Path.of(packagePath)).onComplete(packageResult -> {
                if (packageResult.succeeded()) {
                    System.out.println("\n----- Package Report -----");
                    printPackageReport(packageResult.result());
                } else {
                    System.err.println("Error retrieving package report: " + packageResult.cause().getMessage());
                }

                long start = System.nanoTime();
                analyser.getProjectDependencies(Path.of(projectPath)).onComplete(projectResult -> {
                    long elapsed = System.nanoTime() - start;
                    if (projectResult.succeeded()) {
                        System.out.println("\n----- Project Report -----");
                        printProjectReport(projectResult.result());
                    } else {
                        System.err.println("Error retrieving project report: " + projectResult.cause().getMessage());
                    }
                    System.out.println("Elapsed time for project analysis: " + elapsed / 1_000_000.0 + " ms");
                });
            });
        });
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
