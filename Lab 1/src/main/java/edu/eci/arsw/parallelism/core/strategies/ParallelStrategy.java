package edu.eci.arsw.parallelism.core.strategies;

/**
 * Strategy interface used to compute hexadecimal digits of Pi.
 */
public interface ParallelStrategy {

    /**
     * Calculate the requested hexadecimal digits of Pi.
     *
     * @param start zero-based position after the radix point
     * @param count number of hex digits to compute
     * @param threads number of threads to use (strategy-dependent)
     * @return an uppercase hexadecimal string with the requested digits
     */
    String calculate(int start, int count, int threads);

    /**
     * Name of the strategy used to select it from the service registry.
     *
     * @return a short name identifying the strategy
     */
    String name();
}
