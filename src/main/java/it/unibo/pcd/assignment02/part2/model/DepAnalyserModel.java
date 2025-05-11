package it.unibo.pcd.assignment02.part2.model;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.pcd.assignment02.part2.controller.ModelUpdatesListener;
import it.unibo.pcd.assignment02.part2.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

public class DepAnalyserModel {
    private File projectSourceRoot;
    private FileScanner scanner;
    private FileAnalyser analyser;
    private NodeMapper nodeMapper;
    private EdgeMapper edgeMapper;
    Observable<ParsedJavaFile> stream;
    private final ArrayList<ModelUpdatesListener> listeners = new ArrayList<>();

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

    public void addListener(ModelUpdatesListener listener) {
        listeners.add(listener);
    }

    public void createPipeline() {
        if(projectSourceRoot == null) return;
        scanner = new FileScanner(projectSourceRoot);
        analyser = new FileAnalyser();
        edgeMapper = new EdgeMapper();
        nodeMapper = new NodeMapper();
        stream = getDataStream();
        computeProjectFileSet();
    }

    private Observable<ParsedJavaFile> getDataStream(){
        return scanner.createFileStream()
                //.doOnNext(e-> System.out.println("[FILE]: "+e))
                .map(analyser.dependenciesMapper());
                //.doOnNext(e-> System.out.println("[PARSED REPORT]: "+e));
    }

    private void computeProjectFileSet() {
        HashSet<String> toReturn = new HashSet<>();
        stream
                .observeOn(Schedulers.newThread())
                .subscribe(
                        parsedJavaFile -> toReturn.add(parsedJavaFile.getPackageName() + "." + parsedJavaFile.getFileName()),
                        this::handleError,
                        () -> handleCompletion(toReturn)
                );
    }

    private void handleError(Throwable throwable) {
        Util.err("An error occurred while walking path: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void handleCompletion(HashSet<String> toReturn) {
        nodeMapper.setProjectFileNames(toReturn);
        notifyReadyForSubscription();
    }

    private void notifyReadyForSubscription() {
        for (ModelUpdatesListener listener : listeners) {
            listener.subscriptionReady();
        }
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
