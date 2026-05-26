package edu.eci.arsw.parallelism.core;

import edu.eci.arsw.parallelism.core.strategies.ParallelStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
/**
 * Service that provides Pi digit calculation using configurable strategies.
 */
public class PiDigitsService {

    private final Map<String, ParallelStrategy> strategies;

    /**
     * Constructs the service with available strategies discovered by Spring.
     *
     * @param strategyList list of {@link ParallelStrategy} implementations
     */
    public PiDigitsService(List<ParallelStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ParallelStrategy::name, Function.identity()));
    }

    /**
     * Calculate hexadecimal digits of Pi using the named strategy.
     *
     * @param start zero-based position after the radix point
     * @param count number of hex digits to compute
     * @param threads number of threads to use (strategy-dependent)
     * @param strategyName name of the strategy to use
     * @return an uppercase hexadecimal string with the requested digits
     * @throws IllegalArgumentException when the named strategy is unknown
     */
    public String calculate(int start, int count, int threads, String strategyName) {
        ParallelStrategy strategy = strategies.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        }

        return strategy.calculate(start, count, threads);
    }
}
