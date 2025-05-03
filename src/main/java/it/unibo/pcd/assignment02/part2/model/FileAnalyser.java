package it.unibo.pcd.assignment02.part2.model;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileAnalyser {
    public Observable<File> createDependenciesStream() {
        return Observable.<File>create(emitter -> {
            try(Stream<Path> files = Files.walk(Path.of(projectSourceRoot.toURI()))){
                files
                        .filter(file -> file.toString().endsWith(".java"))
                        .map(Path::toFile)
                        .forEach(emitter::onNext);
                emitter.onComplete();
            } catch (IOException e) {
                System.out.println("An error occurred while walking the project tree.");
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.computation());
    }

}
