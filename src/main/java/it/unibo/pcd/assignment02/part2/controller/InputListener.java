package it.unibo.pcd.assignment02.part2.controller;

/**
 * An interface representing a listener for user input events
 * within a GUI or controller context.
 * <p>
 * Implementations of this interface are notified when
 * specific user actions occur
 * @author Tiziano Vuksan - tiziano.vuksan@studio.unibo.it
 */
public interface InputListener {

    /**
     * Invoked when the user initiates an action to start
     * the dependency analysis process or a related operation.
     */
    void started();

    /**
     * Invoked when the user triggers the file chooser,
     * typically to select a file or directory for analysis.
     */
    void fileChooserTriggered();
}
