package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FiltersTest {

    @Test
    public void shouldRemoveRedundancy() {
        RedundancyFilter filter = new RedundancyFilter();
        List<Point> points = List.of(
                new Point(10, 10),
                new Point(10, 10),
                new Point(20, 20),
                new Point(20, 20),
                new Point(30, 30));
        Blueprint bp = new Blueprint("john", "test", points);

        Blueprint filtered = filter.apply(bp);
        List<Point> result = filtered.getPoints();

        assertEquals(3, result.size());
        assertEquals(10, result.get(0).x());
        assertEquals(20, result.get(1).x());
        assertEquals(30, result.get(2).x());
    }

    @Test
    public void shouldUndersample() {
        UndersamplingFilter filter = new UndersamplingFilter();
        List<Point> points = List.of(
                new Point(10, 10),
                new Point(15, 15),
                new Point(20, 20),
                new Point(25, 25));
        Blueprint bp = new Blueprint("john", "test", points);

        Blueprint filtered = filter.apply(bp);
        List<Point> result = filtered.getPoints();

        assertEquals(2, result.size());
        assertEquals(10, result.get(0).x());
        assertEquals(20, result.get(1).x());
    }
}
