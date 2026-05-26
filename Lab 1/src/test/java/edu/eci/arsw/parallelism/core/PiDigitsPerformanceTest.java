package edu.eci.arsw.parallelism.performance;

import edu.eci.arsw.parallelism.core.PiDigitsService;
import edu.eci.arsw.parallelism.core.strategies.ParallelStrategy;
import edu.eci.arsw.parallelism.core.strategies.SequentialStrategy;
import edu.eci.arsw.parallelism.core.strategies.ThreadJoinStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Performance experiments for Pi Digits calculation.
 * This "test" prints the execution times to the console for analysis.
 */
class PiDigitsPerformanceTest {

    private PiDigitsService service;
    private static final int COUNT = 20000;
    private static final int START = 0;

    @BeforeEach
    void setUp() {
        List<ParallelStrategy> strategies = Arrays.asList(new SequentialStrategy(), new ThreadJoinStrategy());
        service = new PiDigitsService(strategies);
    }

    @Test
    void measureAndPrintExecutionTimes() {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("--------------------------------------------------");
        System.out.println("    EXPERIMENTOS DE RENDIMIENTO (Start=" + START + ", Count=" + COUNT + ")");
        System.out.println("    Núcleos disponibles: " + cores);
        System.out.println("--------------------------------------------------");
        System.out.printf("| %-15s | %-10s | %-15s |\n", "Estrategia", "Hilos", "Tiempo (ms)");
        System.out.println("|-----------------|------------|-----------------|");
        measure("sequential", 1);
        measure("thread-join", 1);
        measure("thread-join", cores);
        measure("thread-join", cores * 2);
        measure("thread-join", 200);
        measure("thread-join", 500);
        System.out.println("--------------------------------------------------");
    }

    private void measure(String strategy, int threads) {
        long startTime = System.currentTimeMillis();
        service.calculate(START, COUNT, threads, strategy);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.printf("| %-15s | %-10d | %-15d |\n", strategy, threads, duration);
    }
}