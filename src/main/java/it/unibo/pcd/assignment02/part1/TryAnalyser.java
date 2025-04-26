package it.unibo.pcd.assignment02.part1;

import io.vertx.core.Future;
import it.unibo.pcd.assignment02.part1.reports.ClassDepsReport;

import java.nio.file.Path;

public class TryAnalyser {

    public static void main(String[] args){
        DependencyAnalyser dep = new DependencyAnalyser();

        Future<ClassDepsReport> report = dep.getClassDependencies(Path.of("C:/Users/Tiziano/Desktop/Tiziano/UNI/Magistrale/Corsi/First year/PCD/assignment-02/src/main/java/it/unibo/pcd/assignment02/part1/DependencyAnalyser.java"));

        report.onComplete(classDepsReport -> {
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
        });
    }
}
