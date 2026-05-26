package edu.eci.arsw.parallelism.core;

import edu.eci.arsw.parallelism.core.strategies.ParallelStrategy;
import edu.eci.arsw.parallelism.core.strategies.SequentialStrategy;
import edu.eci.arsw.parallelism.core.strategies.ThreadJoinStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PiDigitsServiceTest {

    private PiDigitsService service;
    private SequentialStrategy sequentialStrategy;

    @BeforeEach
    void setUp() {
        sequentialStrategy = new SequentialStrategy();
        ThreadJoinStrategy threadJoinStrategy = new ThreadJoinStrategy();
        List<ParallelStrategy> strategies = Arrays.asList(sequentialStrategy, threadJoinStrategy);
        service = new PiDigitsService(strategies);
    }

    @Test
    void shouldReturnSameResultForSequentialAndParallel() {
        int start = 0;
        int count = 100;
        int threads = 4;

        String sequentialResult = service.calculate(start, count, 1, "sequential");
        String parallelResult = service.calculate(start, count, threads, "thread-join");

        assertEquals(sequentialResult, parallelResult, "Sequential and Parallel results should match");
    }

    @RepeatedTest(10)
    void shouldBeDeterministic() {
        int start = 10;
        int count = 50;
        int threads = 4;

        String result1 = service.calculate(start, count, threads, "thread-join");
        String result2 = service.calculate(start, count, threads, "thread-join");

        assertEquals(result1, result2, "Multiple runs should yield same result");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldNotDeadlock() {
        int start = 0;
        int count = 1000;
        int threads = 10;

        assertDoesNotThrow(() -> service.calculate(start, count, threads, "thread-join"));
    }

    @Test
    void shouldThrowExceptionForUnknownStrategy() {
        assertThrows(IllegalArgumentException.class, () ->
                service.calculate(0, 10, 1, "unknown-strategy"));
    }
}