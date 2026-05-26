package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.filters.BlueprintsFilter;
import edu.eci.arsw.blueprints.filters.IdentityFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistence;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BlueprintsServicesTest {

    private BlueprintsServices services;
    private BlueprintPersistence persistence;
    private BlueprintsFilter filter;

    @BeforeEach
    public void setUp() {
        persistence = Mockito.mock(BlueprintPersistence.class);
        filter = new IdentityFilter();
        services = new BlueprintsServices(persistence, filter);
    }

    @Test
    public void shouldAddNewBlueprint() throws BlueprintPersistenceException {
        Blueprint bp = new Blueprint("john", "testbp", List.of(new Point(1, 1)));
        services.addNewBlueprint(bp);
        verify(persistence, times(1)).saveBlueprint(bp);
    }

    @Test
    public void shouldGetAllBlueprints() {
        Set<Blueprint> mockSet = new HashSet<>();
        mockSet.add(new Blueprint("john", "testbp", List.of()));
        when(persistence.getAllBlueprints()).thenReturn(mockSet);

        Set<Blueprint> result = services.getAllBlueprints();
        assertEquals(1, result.size());
    }

    @Test
    public void shouldGetBlueprintsByAuthor() throws BlueprintNotFoundException {
        Set<Blueprint> mockSet = new HashSet<>();
        mockSet.add(new Blueprint("john", "testbp", List.of()));
        when(persistence.getBlueprintsByAuthor("john")).thenReturn(mockSet);

        Set<Blueprint> result = services.getBlueprintsByAuthor("john");
        assertEquals(1, result.size());
    }

    @Test
    public void shouldGetBlueprint() throws BlueprintNotFoundException {
        Blueprint bp = new Blueprint("john", "testbp", List.of());
        when(persistence.getBlueprint("john", "testbp")).thenReturn(bp);

        Blueprint result = services.getBlueprint("john", "testbp");
        assertNotNull(result);
        assertEquals("testbp", result.getName());
    }

    @Test
    public void shouldAddPoint() throws BlueprintNotFoundException {
        services.addPoint("john", "testbp", 10, 10);
        verify(persistence, times(1)).addPoint("john", "testbp", 10, 10);
    }
}
