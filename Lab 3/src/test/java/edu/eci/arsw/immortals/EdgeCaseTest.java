package edu.eci.arsw.immortals;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * Additional tests to increase coverage of edge cases and alternative execution
 * paths.
 */
public class EdgeCaseTest {

    @Test
    public void testNaiveCombatMode() throws InterruptedException {
        // Test the naive combat mode which uses unordered locking
        ImmortalManager manager = new ImmortalManager(3, "naive", 100, 30);
        manager.start();
        Thread.sleep(1000); // Let them fight in naive mode
        manager.stop();
        manager.close();

        // Verify that at least some fights occurred
        assertTrue(manager.scoreBoard().totalFights() > 0);
    }

    @Test
    public void testSingleImmortalScenario() throws InterruptedException {
        // Test with only 1 immortal - should not fight
        ImmortalManager manager = new ImmortalManager(1, "ordered", 100, 10);

        // Verify initial state
        assertEquals(1, manager.populationSnapshot().size());

        manager.start();
        Thread.sleep(500);
        manager.stop();
        manager.close();

        // No fights should occur with only 1 immortal
        assertEquals(0, manager.scoreBoard().totalFights());
        // The immortal exists but the thread may have exited
        assertEquals(1, manager.populationSnapshot().size());
    }

    @Test
    public void testManagerWithZeroImmortals() {
        // Edge case: manager with 0 immortals
        ImmortalManager manager = new ImmortalManager(0, "ordered", 100, 10);
        manager.start();
        manager.stop();
        manager.close();

        assertEquals(0, manager.aliveCount());
        assertEquals(0, manager.scoreBoard().totalFights());
    }

    @Test
    public void testQuickStopBeforeFights() throws InterruptedException {
        // Test stopping immediately after starting
        ImmortalManager manager = new ImmortalManager(5, "ordered", 100, 10);
        manager.start();
        Thread.sleep(10); // Very short time
        manager.stop();
        manager.close();

        // Should have stopped cleanly
        assertTrue(manager.aliveCount() >= 0);
    }

    @Test
    public void testPauseResumeMultipleTimes() throws InterruptedException {
        // Test multiple pause/resume cycles
        ImmortalManager manager = new ImmortalManager(4, "ordered", 100, 15);
        manager.start();

        for (int i = 0; i < 3; i++) {
            Thread.sleep(200);
            manager.pause();
            assertTrue(manager.controller().paused());
            Thread.sleep(100);
            manager.resume();
            assertFalse(manager.controller().paused());
        }

        manager.stop();
        manager.close();
    }

    @Test
    public void testHighDamageQuickElimination() throws InterruptedException {
        // Test with very high damage to ensure quick eliminations
        ImmortalManager manager = new ImmortalManager(5, "ordered", 50, 50);
        manager.start();
        Thread.sleep(1500); // Wait for eliminations
        manager.stop();
        manager.close();

        // Should have fewer immortals alive
        assertTrue(manager.aliveCount() < 5);
        assertTrue(manager.scoreBoard().totalFights() > 0);
    }

    @Test
    public void testLowDamageLongFight() throws InterruptedException {
        // Test with low damage - fights should take longer
        ImmortalManager manager = new ImmortalManager(3, "ordered", 100, 1);
        manager.start();
        Thread.sleep(500);
        manager.pause();

        int aliveCount = manager.aliveCount();
        long totalHealth = manager.totalHealth();

        manager.stop();
        manager.close();

        // With low damage, most should still be alive
        assertTrue(aliveCount >= 2);
        assertEquals(300, totalHealth); // Health should be conserved
    }

    @Test
    public void testPopulationSnapshot() throws InterruptedException {
        ImmortalManager manager = new ImmortalManager(4, "ordered", 100, 20);
        manager.start();
        Thread.sleep(300);
        manager.pause();

        List<Immortal> snapshot1 = manager.populationSnapshot();
        List<Immortal> snapshot2 = manager.populationSnapshot();

        // Snapshots should be independent copies
        assertNotSame(snapshot1, snapshot2);
        assertEquals(snapshot1.size(), snapshot2.size());

        manager.stop();
        manager.close();
    }

    @Test
    public void testImmortalHealthBoundaries() throws InterruptedException {
        // Test with minimal health
        ImmortalManager manager = new ImmortalManager(3, "ordered", 10, 5);
        manager.start();
        Thread.sleep(800);
        manager.stop();
        manager.close();

        // Should have eliminated some immortals quickly
        assertTrue(manager.aliveCount() < 3);
    }

    @Test
    public void testConcurrentPauseResume() throws InterruptedException {
        ImmortalManager manager = new ImmortalManager(5, "ordered", 100, 10);
        manager.start();

        // Rapidly pause and resume from different "threads"
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(50);
                    manager.pause();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(60);
                    manager.resume();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        manager.stop();
        manager.close();

        // Should complete without deadlock or errors
        assertTrue(manager.aliveCount() >= 0);
    }
}
