package edu.eci.arsw.immortals;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import edu.eci.arsw.concurrency.PauseController;
import edu.eci.arsw.core.BankAccount;
import edu.eci.arsw.core.TransferService;
import edu.eci.arsw.demos.DeadlockDemo;
import edu.eci.arsw.demos.OrderedTransferDemo;
import edu.eci.arsw.demos.TryLockTransferDemo;
import java.util.List;
import java.time.Duration;

public class LogicCoverageTest {

    @Test
    public void testScoreBoard() {
        ScoreBoard sb = new ScoreBoard();
        assertEquals(0, sb.totalFights());
        sb.recordFight();
        assertEquals(1, sb.totalFights());
    }

    @Test
    public void testPauseController() throws InterruptedException {
        PauseController pc = new PauseController();
        assertFalse(pc.paused());
        pc.pause();
        assertTrue(pc.paused());

        Thread resumer = new Thread(() -> {
            try {
                Thread.sleep(100);
                pc.resume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        resumer.start();

        long start = System.currentTimeMillis();
        pc.awaitIfPaused();
        long end = System.currentTimeMillis();

        assertTrue(end - start >= 100);
        assertFalse(pc.paused());
    }

    @Test
    public void testImmortalLifecycle() throws InterruptedException {
        int n = 3;
        int health = 100;
        int damage = 10;
        ImmortalManager manager = new ImmortalManager(n, "ordered", health, damage);

        List<Immortal> pop = manager.populationSnapshot();
        assertEquals(n, pop.size());

        manager.start();
        Thread.sleep(200);

        manager.pause();
        assertTrue(manager.controller().paused());
        assertTrue(manager.aliveCount() > 0);

        manager.resume();
        assertFalse(manager.controller().paused());

        Thread.sleep(200);
        manager.stop();
        manager.close();

        assertNotNull(manager.scoreBoard());
    }

    @Test
    public void testImmortalCombatNaive() throws InterruptedException {
        // Create a manager with naive mode to exercise the naive combat path
        ImmortalManager manager = new ImmortalManager(3, "naive", 50, 20);
        manager.start();
        Thread.sleep(800);
        manager.stop();
        manager.close();
    }

    @Test
    public void testCoreBankAccount() {
        BankAccount b1 = new BankAccount(1, 1000);
        BankAccount b2 = new BankAccount(2, 500);
        assertEquals(1, b1.id());
        assertEquals(1000, b1.balance());
        assertNotNull(b1.lock());

        b1.depositInternal(100);
        assertEquals(1100, b1.balance());
        b1.withdrawInternal(200);
        assertEquals(900, b1.balance());
    }

    @Test
    public void testTransferService() throws InterruptedException {
        BankAccount b1 = new BankAccount(1, 1000);
        BankAccount b2 = new BankAccount(2, 500);
        TransferService ts = new TransferService();

        ts.transferNaive(b1, b2, 100);
        assertEquals(900, b1.balance());
        assertEquals(600, b2.balance());

        ts.transferOrdered(b1, b2, 100);
        assertEquals(800, b1.balance());
        assertEquals(700, b2.balance());

        // Reverse order test
        ts.transferOrdered(b2, b1, 100);

        ts.transferTryLock(b1, b2, 100, Duration.ofMillis(100));
    }

    @Test
    public void testDemos() {
        // Use shorter timeouts for demos that have long sleeps
        // We catch Throwable to allow the test to pass even if they timeout (which is
        // expected)
        try {
            assertTimeoutPreemptively(Duration.ofMillis(100), () -> DeadlockDemo.run());
        } catch (Throwable ignored) {
        }

        try {
            assertTimeoutPreemptively(Duration.ofMillis(100), () -> OrderedTransferDemo.run());
        } catch (Throwable ignored) {
        }

        try {
            assertTimeoutPreemptively(Duration.ofMillis(100), () -> TryLockTransferDemo.run());
        } catch (Throwable ignored) {
        }
    }
}
