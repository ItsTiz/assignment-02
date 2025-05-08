package it.unibo.pcd.assignment02.part1;

import com.github.javaparser.JavaParser;
import io.vertx.core.*;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import it.unibo.pcd.assignment02.part1.reports.ClassDepsReport;
import it.unibo.pcd.assignment02.part1.reports.PackageDepsReport;
import it.unibo.pcd.assignment02.part1.reports.ProjectDepsReport;
import it.unibo.pcd.assignment02.part1.utils.Helper;
import it.unibo.pcd.assignment02.part1.utils.Errors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AsyncDependencyAnalyser implements DependencyAnalyserLib {

    private final Vertx vertx;
    private final JavaParser parser;

    public AsyncDependencyAnalyser(){
        this.vertx = Vertx.vertx();
        this.parser = new JavaParser();
    }

    private Handler<Throwable> handleFailure() {
        return throwable -> {
            System.out.println("Compose chain failed to produce future.");
            if (throwable instanceof ReplyException re) {
                Errors error = Errors.getErrorFromCode(re.failureCode());

                switch (error) {
                    case PARSING_ERROR -> System.err.println("Parsing error: " + re.getMessage());
                    case UNKNOWN_ERROR -> System.err.println("Unknown processing error: " + re.getMessage());
                }
            } else {
                System.err.println("Non-ReplyException error: " + throwable.getMessage());
            }
        };
    }

    @Override
    public Future<ClassDepsReport> getClassDependencies(Path classSrcFile) throws NullPointerException, IllegalArgumentException {
        if (classSrcFile == null) throw new NullPointerException("The specified path file is null.");
        if (!classSrcFile.toFile().exists()) throw new IllegalArgumentException("File does not exist.");

        return this.vertx.executeBlocking(() -> {
            var cu = parser.parse(classSrcFile)
                    .getResult()
                    .orElseThrow(() -> new Exception("Parsing failed"));
            return ClassDepsReport.fromCompilationUnit(classSrcFile, cu);
        }).recover(err -> Future.failedFuture(new ReplyException(
                ReplyFailure.RECIPIENT_FAILURE,
                Errors.PARSING_ERROR.getCode(),
                "Parsing failed: " + err.getMessage()
        )));


    }

    @Override
    public Future<PackageDepsReport> getPackageDependencies(Path packageSrcFolder) throws NullPointerException, IllegalArgumentException  {
        if (packageSrcFolder == null) throw new NullPointerException("The specified path file is null.");
        if (!Files.isDirectory(packageSrcFolder)) throw new IllegalArgumentException("Path is not a folder.");

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
            throw new ReplyException(
                    ReplyFailure.RECIPIENT_FAILURE,
                    Errors.UNKNOWN_ERROR.getCode(),
                    "Failed to walk the package directory: " + e.getMessage()
            );
        }

    }

    @Override
    public Future<ProjectDepsReport> getProjectDependencies(Path projectSrcFolder) throws NullPointerException, IllegalArgumentException {
        if (projectSrcFolder == null) throw new NullPointerException("The specified project path is null.");
        if (!Files.isDirectory(projectSrcFolder)) throw new IllegalArgumentException("Path is not a folder.");

        try (Stream<Path> paths = Files.walk(projectSrcFolder)) {

            List<Future<PackageDepsReport>> packageReports = paths
                    .filter(Files::isDirectory)
                    .filter(Helper::hasJavaFiles)
                    .map(this::getPackageDependencies)
                    .toList();

            if (packageReports.isEmpty()) {
                return Future.succeededFuture(ProjectDepsReport.emptyProjectReport(projectSrcFolder));
            }

            return Future.all(new ArrayList<>(packageReports))
                    .map(composite -> {
                        List<PackageDepsReport> reports = composite.list();
                        return ProjectDepsReport.fromPackageReportList(projectSrcFolder, reports);
                    })
                    .onFailure(handleFailure());

        } catch (IOException e) {
            throw new ReplyException(
                    ReplyFailure.RECIPIENT_FAILURE,
                    Errors.UNKNOWN_ERROR.getCode(),
                    "Failed to walk the project directory: " + e.getMessage()
            );
        }
    }
}