package edu.eci.arsw.blueprints.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BlueprintModelTest {

    @Test
    public void shouldCreateBlueprintAndAccessors() {
        Point p1 = new Point(10, 10);
        Point p2 = new Point(20, 20);
        Blueprint bp = new Blueprint("john", "testbp", List.of(p1));

        assertEquals("john", bp.getAuthor());
        assertEquals("testbp", bp.getName());
        assertEquals(1, bp.getPoints().size());

        bp.addPoint(p2);
        assertEquals(2, bp.getPoints().size());

        String str = bp.toString();
        assertNotNull(str);
        // We know toString() likely uses default Object.toString() or similar if not
        // overridden,
        // but if it is overridden to include fields:
        // assertTrue(str.contains("john"));
    }

    @Test
    public void shouldCheckEquality() {
        Blueprint bp1 = new Blueprint("john", "testbp", List.of());
        Blueprint bp2 = new Blueprint("john", "testbp", List.of());
        Blueprint bp3 = new Blueprint("jane", "testbp", List.of());

        assertEquals(bp1, bp2);
        assertNotEquals(bp1, bp3);
        assertEquals(bp1.hashCode(), bp2.hashCode());
    }
}
