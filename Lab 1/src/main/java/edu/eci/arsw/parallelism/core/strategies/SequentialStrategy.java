package edu.eci.arsw.parallelism.core.strategies;

import edu.eci.arsw.parallelism.core.PiDigits;
import org.springframework.stereotype.Component;

/**
 * Simple sequential strategy implementation that delegates to {@link PiDigits}.
 */
@Component
public class SequentialStrategy implements ParallelStrategy {

    /**
     * Calculate digits sequentially using the {@link PiDigits} helper.
     */
    @Override
    public String calculate(int start, int count, int threads) {
        return PiDigits.getDigitsHex(start, count);
    }

    /**
     * Name identifying this strategy implementation.
     */
    @Override
    public String name() {
        return "sequential";
    }
}
