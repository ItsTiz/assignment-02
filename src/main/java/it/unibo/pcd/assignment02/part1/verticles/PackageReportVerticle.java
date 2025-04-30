package it.unibo.pcd.assignment02.part1.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import it.unibo.pcd.assignment02.part1.reports.ClassDepsReport;
import it.unibo.pcd.assignment02.part1.reports.PackageDepsReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PackageReportVerticle extends AbstractVerticle {

    public PackageReportVerticle(){


    }

    public void start() {
        Promise<PackageDepsReport> packageReport = Promise.promise();

        try (Stream<Path> javaFiles = Files.list(packageSrcFolder)) {

            List<Future<ClassDepsReport>> classReports = javaFiles
                    .filter(e -> e.toString().endsWith(".java"))
                    .map(this::getClassDependencies)
                    .toList();

            return Future.all(new ArrayList<>(classReports))
                    .map(composite -> {
                        List<ClassDepsReport> reports = composite.list();
                        return PackageDepsReport.fromClassReportList(packageSrcFolder,reports);
                    })
                    .onFailure(handleFailure());

        } catch (IOException e) {
            packageReport.fail("An error occurred while listing files in the package.");
            throw new RuntimeException(e);
        }
    }
}
