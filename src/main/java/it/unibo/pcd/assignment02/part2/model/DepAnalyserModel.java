package it.unibo.pcd.assignment02.part2.model;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.internal.operators.observable.ObservableBlockingSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.pcd.assignment02.part2.utils.Util;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;

public class DepAnalyserModel {
    private File projectSourceRoot;
    private FileScanner scanner;
    private FileAnalyser analyser;
    private NodeMapper nodeMapper;
    private EdgeMapper edgeMapper;
    Observable<ParsedJavaFile> stream;
    
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
        analyser = new FileAnalyser();
        nodeMapper = new NodeMapper();
        edgeMapper = new EdgeMapper();

        stream = getDataStream();
    }

    private Observable<ParsedJavaFile> getDataStream(){
        return scanner.createFileStream()
                .doOnNext(e-> System.out.println("[FILE]: "+e))
                .map(analyser.dependenciesMapper())
                .doOnNext(e-> System.out.println("[PARSED REPORT]: "+e));
    }

    public Observable<Node> getNodes() {
        return stream
                .flatMap(nodeMapper.flattenToNodes())
                .distinct();
    }

    public Observable<Edge> getEdges() {
        return stream
                .flatMap(edgeMapper.flattenToEdges());
    }
}
