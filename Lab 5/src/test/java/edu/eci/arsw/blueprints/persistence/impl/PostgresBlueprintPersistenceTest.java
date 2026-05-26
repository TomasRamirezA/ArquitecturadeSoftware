package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostgresBlueprintPersistenceTest {

        @Mock
        private JdbcTemplate jdbcTemplate;

        @InjectMocks
        private PostgresBlueprintPersistence persistence;

        @Test
        public void shouldSaveBlueprint() throws BlueprintPersistenceException {
                Blueprint bp = new Blueprint("john", "testbp", List.of(new Point(10, 10)));
                when(jdbcTemplate.update(anyString(), (Object) any(), (Object) any())).thenReturn(1);
                when(jdbcTemplate.update(anyString(), (Object) any(), (Object) any(), (Object) any(), (Object) any(),
                                (Object) any())).thenReturn(1);

                persistence.saveBlueprint(bp);
                verify(jdbcTemplate, atLeastOnce()).update(anyString(), (Object) any(), (Object) any());
        }

        @Test
        public void shouldHandleDatabaseExceptionInSave() {
                Blueprint bp = new Blueprint("john", "testbp", List.of(new Point(10, 10)));
                when(jdbcTemplate.update(anyString(), (Object) any(), (Object) any()))
                                .thenThrow(new RuntimeException("DB error"));

                assertThrows(BlueprintPersistenceException.class, () -> {
                        persistence.saveBlueprint(bp);
                });
        }

        @Test
        public void shouldGetBlueprint() throws BlueprintNotFoundException {
                Blueprint bpMock = new Blueprint("john", "testbp", Collections.emptyList());
                when(jdbcTemplate.query(anyString(), any(RowMapper.class), (Object) any(), (Object) any()))
                                .thenReturn(List.of(bpMock))
                                .thenReturn(List.of(new Point(10, 10)));

                Blueprint result = persistence.getBlueprint("john", "testbp");
                assertNotNull(result);
                assertEquals("testbp", result.getName());
        }

    @Test
    public void shouldThrowNotFoundInGet() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(), any()))
                .thenReturn(Collections.emptyList());
        
        assertThrows(BlueprintNotFoundException.class, () -> {
            persistence.getBlueprint("none", "none");
        });
    }

        @Test
        public void shouldGetBlueprintsByAuthor() throws BlueprintNotFoundException {
                Blueprint bpMock = new Blueprint("john", "testbp", Collections.emptyList());
                when(jdbcTemplate.query(anyString(), any(RowMapper.class), (Object) any()))
                                .thenReturn(List.of(bpMock));

                when(jdbcTemplate.query(anyString(), any(RowMapper.class), (Object) any(), (Object) any()))
                                .thenReturn(List.of(bpMock))
                                .thenReturn(Collections.emptyList());

                Set<Blueprint> result = persistence.getBlueprintsByAuthor("john");
                assertEquals(1, result.size());
        }

    @Test
    public void shouldThrowNotFoundInGetByAuthor() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
                .thenReturn(Collections.emptyList());
        
        assertThrows(BlueprintNotFoundException.class, () -> {
            persistence.getBlueprintsByAuthor("none");
        });
    }

        @Test
        public void shouldGetAllBlueprints() {
                Blueprint bpMock = new Blueprint("john", "testbp", Collections.emptyList());
                when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                                .thenReturn(List.of(bpMock));

                when(jdbcTemplate.query(anyString(), any(RowMapper.class), (Object) any(), (Object) any()))
                                .thenReturn(List.of(bpMock))
                                .thenReturn(Collections.emptyList());

                Set<Blueprint> result = persistence.getAllBlueprints();
                assertFalse(result.isEmpty());
        }

        @Test
        public void shouldAddPoint() throws BlueprintNotFoundException {
                Blueprint bpMock = new Blueprint("john", "testbp", Collections.emptyList());
                when(jdbcTemplate.query(anyString(), any(RowMapper.class), (Object) any(), (Object) any()))
                                .thenReturn(List.of(bpMock))
                                .thenReturn(Collections.emptyList());

                when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), (Object) any(), (Object) any()))
                                .thenReturn(0);

                persistence.addPoint("john", "testbp", 5, 5);
                verify(jdbcTemplate, atLeastOnce()).update(anyString(), (Object) any(), (Object) any(), (Object) any(),
                                (Object) any(), (Object) any());
        }
}
