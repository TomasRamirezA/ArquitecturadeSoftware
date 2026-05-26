package edu.eci.arsw.dogsrace.control;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Common monitor to pause/resume all runners.
 */
public final class RaceControl {

    private final Object monitor = new Object();
    private boolean paused = false;
    private final AtomicBoolean finished = new AtomicBoolean(false);

    public void pause() {
        synchronized (monitor) {
            paused = true;
        }
    }

    public void resume() {
        synchronized (monitor) {
            paused = false;
            monitor.notifyAll();
        }
    }

    public void finish() {
        finished.set(true);
    }

    public boolean isFinished() {
        return finished.get();
    }

    public void restart() {
        finished.set(false);
    }

    public boolean isPaused() {
        synchronized (monitor) {
            return paused;
        }
    }

    /**
     * Call frequently from the running threads to honor pause/resume.
     */
    public void awaitIfPaused() throws InterruptedException {
        synchronized (monitor) {
            while (paused) {
                monitor.wait();
            }
        }
    }
}
