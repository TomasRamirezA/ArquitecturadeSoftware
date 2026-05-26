package edu.eci.arsw.immortals;

import edu.eci.arsw.concurrency.PauseController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Manages the immortal population and simulation lifecycle.
 */
public final class ImmortalManager implements AutoCloseable {
  private final List<Immortal> population = new CopyOnWriteArrayList<>();
  private final List<Future<?>> futures = new ArrayList<>();
  private final PauseController controller = new PauseController();
  private final ScoreBoard scoreBoard = new ScoreBoard();
  private ExecutorService exec;

  private final String fightMode;
  private final int initialHealth;
  private final int damage;

  /**
   * Creates a new ImmortalManager with system properties for health and damage.
   *
   * @param n         number of immortals
   * @param fightMode combat mode ("naive" or "ordered")
   */
  public ImmortalManager(int n, String fightMode) {
    this(n, fightMode, Integer.getInteger("health", 100), Integer.getInteger("damage", 10));
  }

  /**
   * Creates a new ImmortalManager.
   *
   * @param n             number of immortals
   * @param fightMode     combat mode ("naive" or "ordered")
   * @param initialHealth starting health for each immortal
   * @param damage        maximum damage per attack
   */
  public ImmortalManager(int n, String fightMode, int initialHealth, int damage) {
    this.fightMode = fightMode;
    this.initialHealth = initialHealth;
    this.damage = damage;
    for (int i = 0; i < n; i++) {
      population.add(new Immortal("Immortal-" + i, initialHealth, damage, population, scoreBoard, controller));
    }
  }

  public synchronized void start() {
    if (exec != null)
      stop();
    exec = Executors.newVirtualThreadPerTaskExecutor();
    for (Immortal im : population) {
      futures.add(exec.submit(im));
    }
  }

  public void pause() {
    controller.pause();
  }

  public void resume() {
    controller.resume();
  }

  public void stop() {
    for (Immortal im : population)
      im.stop();
    if (exec != null) {
      exec.shutdownNow();
      exec = null;
    }
    futures.clear();
  }

  public int aliveCount() {
    int c = 0;
    for (Immortal im : population)
      if (im.isAlive())
        c++;
    return c;
  }

  public long totalHealth() {
    long sum = 0;
    for (Immortal im : population)
      sum += im.getHealth();
    return sum;
  }

  public List<Immortal> populationSnapshot() {
    return Collections.unmodifiableList(new ArrayList<>(population));
  }

  public ScoreBoard scoreBoard() {
    return scoreBoard;
  }

  public PauseController controller() {
    return controller;
  }

  @Override
  public void close() {
    stop();
  }
}
