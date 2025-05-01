package com.verr1.valkyrienmanager.foundation.executor;


/**
 * Represents an executable task that can be run, checked for execution conditions,
 * and removed when no longer needed. The execution flow is as follows:
 * <p>
 * tick() --> shouldRun() ? run() : __ --> shouldRemove() --> tick()
 */
public interface Executable extends Runnable {

    /**
     * Determines whether the task should run during the current tick.
     *
     * @return true if the task should run, false otherwise.
     */
    boolean shouldRun();

    /**
     * Determines whether the task should be removed after the current tick.
     *
     * @return true if the task should be removed, false otherwise.
     */
    boolean shouldRemove();

    /**
     * Called when the task is removed. This method can be overridden to perform
     * cleanup or other actions upon removal.
     */
    default void onRemove() {}

    /**
     * Executes a single tick of the task. This method handles the execution flow
     * by checking whether the task should run and whether it should be removed.
     */
    void tick();
}

