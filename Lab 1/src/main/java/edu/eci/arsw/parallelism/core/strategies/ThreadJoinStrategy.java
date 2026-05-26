package edu.eci.arsw.parallelism.core.strategies;

import edu.eci.arsw.parallelism.core.PiDigits;
import org.springframework.stereotype.Component;

/**
 * Strategy that computes Pi digits by creating worker threads and joining them.
 */
@Component
public class ThreadJoinStrategy implements ParallelStrategy {
    /**
     * Calculate digits by splitting the work across threads and joining them.
     *
     * @param start zero-based position after the radix point
     * @param count number of hex digits to compute
     * @param threads requested number of threads (must be >= 1)
     * @return an uppercase hexadecimal string with the requested digits
     * @throws IllegalArgumentException when {@code threads} &lt; 1
     */
    @Override
    public String calculate(int start, int count, int threads) {
        if (threads < 1) {
            throw new IllegalArgumentException("Thread count must be at least 1");
        }
        
        if (threads == 1) {
            return PiDigits.getDigitsHex(start, count);
        }

        int numberOfThreads = Math.min(count, threads); 
        int chunkSize = count / numberOfThreads;
        int remainder = count % numberOfThreads;

        Thread[] workers = new Thread[numberOfThreads];
        String[] results = new String[numberOfThreads];

        int currentStart = start;

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;
            final int myStart = currentStart;
            final int myCount = chunkSize + (i < remainder ? 1 : 0);
            
            currentStart += myCount;

            workers[i] = new Thread(() -> {
                results[threadIndex] = PiDigits.getDigitsHex(myStart, myCount);
            }, "Pi-Worker-" + i);

            workers[i].start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Calculation interrupted", e);
            }
        }

        StringBuilder sb = new StringBuilder(count);
        for (String part : results) {
            sb.append(part);
        }

        return sb.toString();
    }

    /**
     * Name identifying this strategy implementation.
     */
    @Override
    public String name() {
        return "thread-join";
    }
}
