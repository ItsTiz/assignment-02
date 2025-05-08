package it.unibo.pcd.assignment02.part2.controller;

import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.pcd.assignment02.part2.model.DepAnalyserModel;
import it.unibo.pcd.assignment02.part2.model.Edge;
import it.unibo.pcd.assignment02.part2.model.Node;
import it.unibo.pcd.assignment02.part2.view.DepAnalyserView;
import it.unibo.pcd.assignment02.part2.utils.Util;

import java.io.File;
import java.util.Optional;

public class DepAnalyserController implements InputListener {

    private Optional<DepAnalyserView> view;
    private final DepAnalyserModel model;

    public DepAnalyserController(DepAnalyserModel model) {
        this.model = model;
    }

    public void attachView(DepAnalyserView view) {
        this.view = Optional.of(view);
        model.setProjectSourceRoot(getDirectory());
    }

    private Optional<File> getDirectory() {
        if(view.isPresent()){
            return view.get().openFolderDialog();
        }
        return Optional.empty();
    }

    @Override
    public void started() {
        Util.log("Process started. Creating pipeline for project folder: " + model.getProjectSourceRoot());
        model.createPipeline();
        subscribeToNodes();
        subscribeToEdges();
    }

    @Override
    public void fileChooserTriggered() {
        model.setProjectSourceRoot(getDirectory());
    }

    public void subscribeToNodes(){
        var t0 = System.nanoTime();
        model.getNodes()
                .observeOn(Schedulers.newThread())
                .subscribe(consumeNodeByPaint(), handleOnError(), handleOnComplete(t0));
    }

    public void subscribeToEdges(){
        var t0 = System.nanoTime();
        model.getEdges()
                .observeOn(Schedulers.newThread())
                .subscribe(consumeEdgeByPaint(), handleOnError(), handleOnComplete(t0));
    }

    private Consumer<Node> consumeNodeByPaint() {
        if(view.isEmpty()) return consumeNodeByLog();
        return e -> view.get().signalNodePaint(e);
    }

    private Consumer<Edge> consumeEdgeByPaint() {
        if(view.isEmpty()) return consumeEdgeByLog();
        return e -> view.get().signalEdgePaint(e);
    }

    private Consumer<Node> consumeNodeByLog() {
        return e -> Util.log("[NODE]" + e.toString());
    }

    private Consumer<Edge> consumeEdgeByLog() {
        return e -> Util.log("[EDGE]" + e.toString());
    }

    private Action handleOnComplete(long t0) {
        return () -> {
            Util.log("File walking complete");
            Util.log("Time elapsed: " + (System.nanoTime() - t0) / 1_000_000 + "ms");
        };
    }

    private Consumer<Throwable> handleOnError() {
        return throwable -> {
            Util.err("An error occurred while walking path: " + throwable.getMessage());
            throwable.printStackTrace();
        };
    }
}
