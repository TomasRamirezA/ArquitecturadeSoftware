package edu.eci.arsw.dogsrace.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Thread-safe arrival registry.
 */
public final class ArrivalRegistry {

    private int nextPosition = 1;
    private String winner = null;
    private final List<String> arrivals = Collections.synchronizedList(new ArrayList<>());

    public synchronized ArrivalSnapshot registerArrival(String dogName) {
        Objects.requireNonNull(dogName, "dogName");
        final int position = nextPosition++;
        if (position == 1) {
            winner = dogName;
        }
        arrivals.add(dogName + " llego en la posicion " + position);
        return new ArrivalSnapshot(position, winner);
    }

    public synchronized int getNextPosition() {
        return nextPosition;
    }

    public synchronized String getWinner() {
        return winner;
    }

    public synchronized void restart() {
        nextPosition = 1;
        winner = null;
        arrivals.clear();
    }

    public List<String> getArrivals() {
        return new ArrayList<>(arrivals);
    }

    public record ArrivalSnapshot(int position, String winner) {
    }
}
