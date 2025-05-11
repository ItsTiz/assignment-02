package it.unibo.pcd.assignment02.part2.controller;

/**
 * An interface for receiving notifications when the model is ready
 * to publish updates to interested components, such as the view.
 * @author Tiziano Vuksan - tiziano.vuksan@studio.unibo.it
 */
public interface ModelUpdatesListener {

    /**
     * Invoked when the model has completed its initial setup and is ready to
     * send updates, allowing the controller to start subscriptions.
     */
    void subscriptionReady();
}
