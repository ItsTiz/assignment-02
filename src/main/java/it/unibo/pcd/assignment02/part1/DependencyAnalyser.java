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

import java.nio.file.Path;
import java.util.Objects;

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

        return Objects.requireNonNull(doRequest("parser.file", fileMessage))
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
    public Future<PackageDepsReport> getPackageDependencies(Path packageSrcFolder) {
        return null;
    }

    @Override
    public Future<ProjectDepsReport> getProjectDependencies(Path projectSrcFolder) {
        return null;
    }
}