package it.unibo.pcd.assignment02.part1;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import it.unibo.pcd.assignment02.part1.reports.ClassDepsReport;
import it.unibo.pcd.assignment02.part1.reports.PackageDepsReport;
import it.unibo.pcd.assignment02.part1.reports.ProjectDepsReport;
import it.unibo.pcd.assignment02.part1.reports.Report;

import java.nio.file.Path;
import java.util.Set;

public class TryAnalyser {

    public static void main(String[] args){
        DependencyAnalyser dep = new DependencyAnalyser();

//        Future<ClassDepsReport> report = dep.getClassDependencies(Path.of("C:/Users/Tiziano/Desktop/Tiziano/UNI/Magistrale/Corsi/First year/PCD/assignment-02/src/main/java/it/unibo/pcd/assignment02/part1/DependencyAnalyser.java"));
//
//        report.onComplete(TryAnalyser::classReportHandler);
//
//        Future<PackageDepsReport> packageReport = dep.getPackageDependencies(Path.of("C:\\Users\\Tiziano\\Desktop\\Tiziano\\UNI\\Magistrale\\Corsi\\First year\\PCD\\assignment-02\\src\\main\\java\\it\\unibo\\pcd\\assignment02\\part1"));
//
//        packageReport.onComplete(paackageReportHandler());

        long t0 = System.nanoTime();

        Future<ProjectDepsReport> projectReport = dep.getProjectDependencies(
                Path.of("C:\\Users\\Tiziano\\Desktop\\Tiziano\\UNI\\Triennale\\Test\\Alchemist fork\\Alchemist")
        );

        long t1 = System.nanoTime();

        long elapsed = t1 - t0;

        projectReport.onComplete(TryAnalyser::projectReportHandler);

        System.out.println("Elapsed: " + elapsed/1_000_000.0 + " ms");

    }

    private static void visualiseReport(Report dependencyReport){
        if(dependencyReport == null) return;

        Set<String> types = dependencyReport.getTypes();
        System.out.println("All Types in Class (" + types.size() + ") :");
        types.forEach(type -> System.out.println("  - " + type));

        Set<String> deps = dependencyReport.getDependencies();
        System.out.println("All Dependencies in Class (" + deps.size() + ") :");
        deps.forEach(dep -> System.out.println("  - " + dep));
    }

    private static Handler<AsyncResult<PackageDepsReport>> paackageReportHandler() {
        return packageDepsReport -> {
            if (packageDepsReport.succeeded()) {
                PackageDepsReport reportObject = packageDepsReport.result();

                System.out.println("Path: " + reportObject.getPackagePath());

                visualiseReport(reportObject);

            } else {
                System.out.println("Error while retrieving the report.");

            }
        };
    }

    private static void classReportHandler(AsyncResult<ClassDepsReport> classDepsReport) {
        if(classDepsReport.succeeded()){
            ClassDepsReport reportObject = classDepsReport.result();

            System.out.println("Path: " + reportObject.getClassPath());
            System.out.println("Top level name: " + reportObject.getTopLevelType());
            System.out.println("Package Declaration: " + reportObject.getPackageDeclaration());

            visualiseReport(reportObject);
        }else{
            System.out.println("Error while retrieving the report.");

        }
    }

    private static void projectReportHandler(AsyncResult<ProjectDepsReport> projectDepsReport) {
        if (projectDepsReport.succeeded()) {
            ProjectDepsReport reportObject = projectDepsReport.result();

            System.out.println("\n----- Project Report -----");
            System.out.println("Project Root Path: " + reportObject.getProjectPath());

            visualiseReport(reportObject);
        } else {
            System.err.println("Error retrieving the project report: " + projectDepsReport.cause().getMessage());
        }
    }

}
