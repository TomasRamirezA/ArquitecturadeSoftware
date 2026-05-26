package edu.eci.arsw.immortals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class JUnitInvariantTest {
    @Test
    public void testInvariant() throws InterruptedException {
        int n = 10;
        int health = 100;
        int damage = 10;
        ImmortalManager manager = new ImmortalManager(n, "ordered", health, damage);

        long initialSum = manager.totalHealth();
        assertEquals(n * (long) health, initialSum);

        manager.start();
        Thread.sleep(1000); // Let them fight

        manager.pause();
        Thread.sleep(100); // Wait for sync

        long finalSum = manager.totalHealth();
        assertEquals(initialSum, finalSum, "Invariant broken! Health lost: " + (initialSum - finalSum));

        manager.stop();
    }
}
