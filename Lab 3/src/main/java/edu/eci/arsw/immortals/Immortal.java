package edu.eci.arsw.immortals;

import edu.eci.arsw.concurrency.PauseController;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents an immortal fighter in the Highlander simulation.
 */
public final class Immortal implements Runnable {
  private final String name;
  private int health;
  private final int damage;
  private final List<Immortal> population;
  private final ScoreBoard scoreBoard;
  private final PauseController controller;
  private volatile boolean running = true;

  /**
   * Creates a new Immortal.
   *
   * @param name       the immortal's name
   * @param health     initial health points
   * @param damage     maximum damage per attack
   * @param population shared population list
   * @param scoreBoard shared scoreboard
   * @param controller pause controller
   */
  public Immortal(String name, int health, int damage, List<Immortal> population, ScoreBoard scoreBoard,
      PauseController controller) {
    this.name = Objects.requireNonNull(name);
    this.health = health;
    this.damage = damage;
    this.population = Objects.requireNonNull(population);
    this.scoreBoard = Objects.requireNonNull(scoreBoard);
    this.controller = Objects.requireNonNull(controller);
  }

  public String name() {
    return name;
  }

  public synchronized int getHealth() {
    return health;
  }

  public boolean isAlive() {
    return getHealth() > 0 && running;
  }

  public void stop() {
    running = false;
  }

  @Override
  public void run() {
    try {
      while (running && isAlive()) {
        controller.awaitIfPaused();
        if (!running || !isAlive())
          break;

        if (population.size() <= 1)
          break;

        var opponent = pickOpponent();
        if (opponent == null)
          continue;

        String mode = System.getProperty("fight", "ordered");
        if ("naive".equalsIgnoreCase(mode))
          fightNaive(opponent);
        else
          fightOrdered(opponent);

        Thread.sleep(1);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    if (isAlive() && population.size() == 1) {
      System.out.println("[GAME OVER] The Highlander is: " + name);
    }
  }

  private Immortal pickOpponent() {
    int size = population.size();
    if (size <= 1)
      return null;
    Immortal other = population.get(ThreadLocalRandom.current().nextInt(size));
    return (other == this) ? null : other;
  }

  private void fightNaive(Immortal other) {
    synchronized (this) {
      synchronized (other) {
        if (!this.isAlive() || !other.isAlive())
          return;

        int damageValue = ThreadLocalRandom.current().nextInt(this.damage) + 1;
        int effectiveDamage = Math.min(damageValue, other.health);

        other.health -= effectiveDamage;
        this.health += effectiveDamage;
        scoreBoard.recordFight();

        if (other.health <= 0) {
          population.remove(other);
          System.out.println("[DEATH] %s eliminated by %s".formatted(other.name, this.name));
        }
      }
    }
  }

  private void fightOrdered(Immortal other) {
    Immortal first = this.name.compareTo(other.name) < 0 ? this : other;
    Immortal second = this.name.compareTo(other.name) < 0 ? other : this;
    synchronized (first) {
      synchronized (second) {
        if (!this.isAlive() || !other.isAlive())
          return;

        int damageValue = ThreadLocalRandom.current().nextInt(this.damage) + 1;
        int effectiveDamage = Math.min(damageValue, other.health);

        other.health -= effectiveDamage;
        this.health += effectiveDamage;
        scoreBoard.recordFight();

        if (other.health <= 0) {
          population.remove(other);
          System.out.println("[DEATH] %s eliminated by %s".formatted(other.name, this.name));
        }
      }
    }
  }
}
