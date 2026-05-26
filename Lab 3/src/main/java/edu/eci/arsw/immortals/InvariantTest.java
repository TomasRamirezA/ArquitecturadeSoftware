package edu.eci.arsw.immortals;

import java.util.List;

/**
 * Invariant test: verifies that total health is conserved during simulation.
 * Run with: mvn exec:java -Dexec.mainClass=edu.eci.arsw.immortals.InvariantTest
 */
public class InvariantTest {
    public static void main(String[] args) throws InterruptedException {
        int n = 10;
        int health = 100;
        int damage = 10;
        ImmortalManager manager = new ImmortalManager(n, "ordered", health, damage);

        long initialSum = manager.totalHealth();
        System.out.println("Initial total health: " + initialSum);
        System.out.println("Expected: " + (n * (long) health));

        manager.start();
        System.out.println("Simulation started...");
        Thread.sleep(1000); // Let them fight

        manager.pause();
        System.out.println("Simulation paused.");
        Thread.sleep(100); // Wait for sync

        long finalSum = manager.totalHealth();
        System.out.println("Final total health: " + finalSum);

        if (initialSum == finalSum) {
            System.out.println("✅ INVARIANT HOLDS: Total health conserved!");
        } else {
            System.err.println("❌ INVARIANT BROKEN! Health lost: " + (initialSum - finalSum));
            System.exit(1);
        }

        manager.stop();
        manager.close();
        System.out.println("Simulation stopped.");
    }
}
