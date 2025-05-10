package it.unibo.pcd.assignment02.part2;

import it.unibo.pcd.assignment02.part2.controller.DepAnalyserController;
import it.unibo.pcd.assignment02.part2.model.DepAnalyserModel;
import it.unibo.pcd.assignment02.part2.view.DepAnalyserView;

public class DependencyAnalyser {

    final static int SCREEN_WIDTH = 1000;
    final static int SCREEN_HEIGHT = 1000;

    public static void main(String[] args) {
        var model = new DepAnalyserModel();
        var view = new DepAnalyserView(SCREEN_WIDTH, SCREEN_HEIGHT);
        var controller = new DepAnalyserController(model);
        view.addListener(controller);
        model.addListener(controller);
        controller.attachView(view);
    }
}
