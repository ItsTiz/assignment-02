package it.unibo.pcd.assignment02.part1;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonObject;
import it.unibo.pcd.assignment02.part1.reports.ClassDepsReport;
import it.unibo.pcd.assignment02.part1.reports.PackageDepsReport;
import it.unibo.pcd.assignment02.part1.reports.ProjectDepsReport;
import it.unibo.pcd.assignment02.part1.verticles.DependecyAnalyserVerticle;
import it.unibo.pcd.assignment02.part1.verticles.FileParserVerticle;
import it.unibo.pcd.assignment02.part1.utils.Errors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DependencyAnalyser implements DependencyAnalyserLib {

    private final Vertx vertx;

    public DependencyAnalyser(){
        this.vertx = Vertx.vertx();
        deployVerticles();
    }

    private void deployVerticles() {
        vertx.deployVerticle(new FileParserVerticle());
        vertx.deployVerticle(new DependecyAnalyserVerticle());
    }

    private Future<Message<Object>> doRequest(String address, JsonObject message) {
        if(message.isEmpty()) {
            return Future.failedFuture("Message was empty.");
        }
        return vertx.eventBus().request(address, message);
    }

    private Handler<Throwable> handleFailure() {
        return throwable -> {
            System.out.println("Compose chain failed to produce future.");
            if (throwable instanceof ReplyException re) {
                Errors error = Errors.getErrorFromCode(re.failureCode());

                switch (error) {
                    case PARSING_ERROR -> System.err.println("Parsing error: " + re.getMessage());
                    case ANALYSER_ERROR -> System.err.println("Dependency analysis error: " + re.getMessage());
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

        JsonObject fileMessage = new JsonObject().put("file", classSrcFile.toString());

        return (doRequest("parser.file", fileMessage))
                .compose(parserResponse -> {
                    JsonObject parsedData = (JsonObject) parserResponse.body();
                    return doRequest("dependency.analyse", parsedData);
                })
                .map(analyserResponse -> {
                    JsonObject analysedData = (JsonObject) analyserResponse.body();
                    return ClassDepsReport.fromJson(analysedData);
                })
                .onFailure(handleFailure());
    }

    @Override
    public Future<PackageDepsReport> getPackageDependencies(Path packageSrcFolder) throws NullPointerException, IllegalArgumentException  {
        if (packageSrcFolder == null) throw new NullPointerException("The specified path file is null.");
        if (!Files.isDirectory(packageSrcFolder)) throw new IllegalArgumentException("Path is not a folder.");

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

    @Override
    public Future<ProjectDepsReport> getProjectDependencies(Path projectSrcFolder) throws NullPointerException, IllegalArgumentException {
        if (projectSrcFolder == null) throw new NullPointerException("The specified project path is null.");
        if (!Files.isDirectory(projectSrcFolder)) throw new IllegalArgumentException("Path is not a folder.");

        try (Stream<Path> paths = Files.walk(projectSrcFolder)) {

            List<Future<PackageDepsReport>> packageReports = paths
                    .filter(Files::isDirectory)
                    .filter(this::hasJavaFiles)
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
            throw new RuntimeException("Failed to walk the project directory.", e);
        }
    }

    // Helper method: check if a folder has any .java files inside it
    private boolean hasJavaFiles(Path dir) {
        try (Stream<Path> entries = Files.list(dir)) {
            return entries.anyMatch(file -> file.toString().endsWith(".java"));
        } catch (IOException e) {
            return false;
        }
    }


}