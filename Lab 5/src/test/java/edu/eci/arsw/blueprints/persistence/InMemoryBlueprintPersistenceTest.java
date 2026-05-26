package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryBlueprintPersistenceTest {

    private InMemoryBlueprintPersistence persistence;

    @BeforeEach
    public void setUp() {
        persistence = new InMemoryBlueprintPersistence();
    }

    @Test
    public void shouldSaveAndGetBlueprint() throws BlueprintPersistenceException, BlueprintNotFoundException {
        // Use a non-existing author/name
        Blueprint bp = new Blueprint("alice", "palace", List.of(new Point(10, 10)));
        persistence.saveBlueprint(bp);

        Blueprint result = persistence.getBlueprint("alice", "palace");
        assertEquals(bp, result);
    }

    @Test
    public void shouldThrowExceptionWhenSavingDuplicate() {
        // "john:house" exists by default in constructor
        Blueprint bp = new Blueprint("john", "house", List.of(new Point(10, 10)));

        assertThrows(BlueprintPersistenceException.class, () -> {
            persistence.saveBlueprint(bp);
        });
    }

    @Test
    public void shouldThrowExceptionWhenBlueprintNotFound() {
        assertThrows(BlueprintNotFoundException.class, () -> {
            persistence.getBlueprint("missing", "blueprint");
        });
    }

    @Test
    public void shouldGetBlueprintsByAuthor() throws BlueprintNotFoundException {
        // "john" has 2 blueprints by default
        Set<Blueprint> result = persistence.getBlueprintsByAuthor("john");
        assertTrue(result.size() >= 2);
    }

    @Test
    public void shouldThrowExceptionWhenAuthorNotFound() {
        assertThrows(BlueprintNotFoundException.class, () -> {
            persistence.getBlueprintsByAuthor("nobody");
        });
    }

    @Test
    public void shouldGetAllBlueprints() {
        // At least 3 sample blueprints exist
        Set<Blueprint> result = persistence.getAllBlueprints();
        assertTrue(result.size() >= 3);
    }

    @Test
    public void shouldAddPoint() throws BlueprintNotFoundException {
        // "jane:garden" exists
        Blueprint bp = persistence.getBlueprint("jane", "garden");
        int initialSize = bp.getPoints().size();

        persistence.addPoint("jane", "garden", 5, 5);

        assertEquals(initialSize + 1, bp.getPoints().size());
    }
}
