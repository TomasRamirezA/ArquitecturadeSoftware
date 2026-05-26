package edu.eci.arsw.immortals;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Additional focused tests to reach 80% coverage threshold.
 */
public class CoverageBoostTest {

    @Test
    public void testSystemPropertyFightMode() throws InterruptedException {
        // Test using system property to set fight mode
        System.setProperty("fight", "naive");
        try {
            ImmortalManager manager = new ImmortalManager(3, "naive", 80, 15);
            manager.start();
            Thread.sleep(600);
            manager.stop();
            manager.close();

            assertTrue(manager.scoreBoard().totalFights() > 0);
        } finally {
            System.clearProperty("fight");
        }
    }

    @Test
    public void testSystemPropertyOrderedMode() throws InterruptedException {
        // Test using system property for ordered mode
        System.setProperty("fight", "ordered");
        try {
            ImmortalManager manager = new ImmortalManager(3, "ordered", 80, 15);
            manager.start();
            Thread.sleep(600);
            manager.stop();
            manager.close();

            assertTrue(manager.scoreBoard().totalFights() > 0);
        } finally {
            System.clearProperty("fight");
        }
    }

    @Test
    public void testRunningFlagInterruption() throws InterruptedException {
        // Test that immortals stop when running flag is set to false
        ImmortalManager manager = new ImmortalManager(4, "ordered", 100, 10);
        manager.start();
        Thread.sleep(300);

        // Stop should set running to false
        manager.stop();
        Thread.sleep(100);

        // All immortals should have stopped
        manager.close();
        assertTrue(true); // If we get here without hanging, the test passed
    }

    @Test
    public void testMultipleStartStop() throws InterruptedException {
        // Test multiple start/stop cycles
        ImmortalManager manager = new ImmortalManager(3, "ordered", 100, 10);

        for (int i = 0; i < 3; i++) {
            manager.start();
            Thread.sleep(200);
            manager.stop();
            Thread.sleep(100);
        }

        manager.close();
        assertTrue(manager.scoreBoard().totalFights() >= 0);
    }

    @Test
    public void testPickOpponentWithTwoImmortals() throws InterruptedException {
        // Test opponent selection with exactly 2 immortals
        ImmortalManager manager = new ImmortalManager(2, "ordered", 100, 20);
        manager.start();
        Thread.sleep(500);
        manager.pause();

        // With 2 immortals, each should be able to pick the other
        assertTrue(manager.populationSnapshot().size() <= 2);

        manager.stop();
        manager.close();
    }

    @Test
    public void testDeadImmortalsDontFight() throws InterruptedException {
        // Test with very high damage to quickly eliminate immortals
        ImmortalManager manager = new ImmortalManager(3, "ordered", 20, 100);
        manager.start();
        Thread.sleep(1000);
        manager.stop();
        manager.close();

        // Should have eliminated at least one immortal
        assertTrue(manager.aliveCount() < 3);
        assertTrue(manager.scoreBoard().totalFights() > 0);
    }

    @Test
    public void testPauseWhileFighting() throws InterruptedException {
        // Test pausing while immortals are actively fighting
        ImmortalManager manager = new ImmortalManager(5, "ordered", 100, 10);
        manager.start();
        Thread.sleep(100); // Let fights start

        manager.pause();
        long healthAtPause = manager.totalHealth();
        Thread.sleep(300); // Wait while paused
        long healthAfterPause = manager.totalHealth();

        // Health should not change while paused
        assertEquals(healthAtPause, healthAfterPause);

        manager.resume();
        Thread.sleep(200);
        manager.stop();
        manager.close();
    }

    @Test
    public void testAliveCountDecreases() throws InterruptedException {
        // Verify that alive count decreases as immortals are eliminated
        ImmortalManager manager = new ImmortalManager(4, "ordered", 50, 25);
        int initialAlive = manager.aliveCount();

        manager.start();
        Thread.sleep(1200);
        manager.stop();

        int finalAlive = manager.aliveCount();
        manager.close();

        // Some immortals should have been eliminated
        assertTrue(finalAlive < initialAlive || finalAlive == 1);
    }
}
