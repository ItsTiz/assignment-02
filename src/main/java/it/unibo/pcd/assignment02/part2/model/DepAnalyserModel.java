package it.unibo.pcd.assignment02.part2.model;

import io.reactivex.rxjava3.core.Observable;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Flow;

public class DepAnalyserModel {
    private File projectSourceRoot;
    private FileScanner scanner;
    
    public DepAnalyserModel() {}

    public void setProjectSourceRoot(Optional<File> projectSourceRoot) {
        projectSourceRoot.ifPresent(file -> {
                this.projectSourceRoot = file;
                this.scanner = new FileScanner(file);
            }
        );
    }

    public File getProjectSourceRoot() {
        return projectSourceRoot;
    }

    public void createPipeline() {
        if(projectSourceRoot == null) return;
        scanner = new FileScanner(projectSourceRoot);
    }


    public Observable<File> getNodes() {
        return scanner.createFileStream();
    }
}
