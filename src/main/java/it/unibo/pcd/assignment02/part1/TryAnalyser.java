package it.unibo.pcd.assignment02.part1;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import it.unibo.pcd.assignment02.part1.reports.ClassDepsReport;
import it.unibo.pcd.assignment02.part1.reports.PackageDepsReport;
import it.unibo.pcd.assignment02.part1.reports.ProjectDepsReport;

import java.nio.file.Path;

public class TryAnalyser {

    public static void main(String[] args){
        DependencyAnalyser dep = new DependencyAnalyser();

        Future<ClassDepsReport> report = dep.getClassDependencies(Path.of("C:/Users/Tiziano/Desktop/Tiziano/UNI/Magistrale/Corsi/First year/PCD/assignment-02/src/main/java/it/unibo/pcd/assignment02/part1/DependencyAnalyser.java"));

        report.onComplete(TryAnalyser::classReportHandler);

        Future<PackageDepsReport> packageReport = dep.getPackageDependencies(Path.of("C:\\Users\\Tiziano\\Desktop\\Tiziano\\UNI\\Magistrale\\Corsi\\First year\\PCD\\assignment-02\\src\\main\\java\\it\\unibo\\pcd\\assignment02\\part1"));

        packageReport.onComplete(packageDepsReport -> {
            if(packageDepsReport.succeeded()){
                PackageDepsReport reportObject = packageDepsReport.result();

                System.out.println("Path: " + reportObject.getPackagePath());

                System.out.println("Dependencies: ");

                for (String arg : reportObject.getDependencies()) {
                    System.out.println(arg);
                }

                System.out.println("Types: ");

                for (String arg : reportObject.getTypes()) {
                    System.out.println(arg);
                }
            }else{
                System.out.println("Error while retrieving the report.");

            }
        });

        // Test getProjectDependencies
        Future<ProjectDepsReport> projectReport = dep.getProjectDependencies(
                Path.of("C:/Users/Tiziano/Desktop/Tiziano/UNI/Magistrale/Corsi/First year/PCD/assignment-01") // <-- set it to your project root
        );

        projectReport.onComplete(TryAnalyser::projectReportHandler);


    }

    private static void classReportHandler(AsyncResult<ClassDepsReport> classDepsReport) {
        if(classDepsReport.succeeded()){

            ClassDepsReport reportObject = classDepsReport.result();

            System.out.println("Path: " + reportObject.getClassPath());
            System.out.println("Top level name: " + reportObject.getTopLevelType());
            System.out.println("Package Declaration: " + reportObject.getPackageDeclaration());

            System.out.println("Dependencies: ");

            for (String arg : reportObject.getDependencies()) {
                System.out.println(arg);
            }

            System.out.println("Types: ");

            for (String arg : reportObject.getTypes()) {
                System.out.println(arg);
            }
        }else{
            System.out.println("Error while retrieving the report.");

        }
    }

    private static void projectReportHandler(AsyncResult<ProjectDepsReport> projectDepsReport) {
        if (projectDepsReport.succeeded()) {
            ProjectDepsReport reportObject = projectDepsReport.result();

            System.out.println("\n----- Project Report -----");
            System.out.println("Project Root Path: " + reportObject.getProjectPath());

            System.out.println("All Types in Project:");
            reportObject.getTypes().forEach(type -> System.out.println("  - " + type));

            System.out.println("All Dependencies in Project:");
            reportObject.getDependencies().forEach(dep -> System.out.println("  - " + dep));

        } else {
            System.err.println("Error retrieving the project report: " + projectDepsReport.cause().getMessage());
        }
    }

}
