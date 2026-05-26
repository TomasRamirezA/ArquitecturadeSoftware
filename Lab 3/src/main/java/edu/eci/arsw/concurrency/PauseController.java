package edu.eci.arsw.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cooperative pause controller using Lock and Condition.
 */
public final class PauseController {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition unpaused = lock.newCondition();
  private volatile boolean paused = false;

  public void pause() {
    lock.lock();
    try {
      paused = true;
    } finally {
      lock.unlock();
    }
  }

  public void resume() {
    lock.lock();
    try {
      paused = false;
      unpaused.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public boolean paused() {
    return paused;
  }

  /**
   * Blocks while paused.
   *
   * @throws InterruptedException if interrupted while waiting
   */
  public void awaitIfPaused() throws InterruptedException {
    lock.lockInterruptibly();
    try {
      while (paused)
        unpaused.await();
    } finally {
      lock.unlock();
    }
  }
}
