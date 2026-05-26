package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(BlueprintsAPIController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BlueprintsAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlueprintsServices services;

    @Test
    public void shouldGetAllBlueprints() throws Exception {
        Blueprint bp1 = new Blueprint("john", "house", List.of(new Point(10, 10)));
        Blueprint bp2 = new Blueprint("jane", "garden", List.of(new Point(20, 20)));
        when(services.getAllBlueprints()).thenReturn(new HashSet<>(Arrays.asList(bp1, bp2)));

        mockMvc.perform(get("/api/v1/blueprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    public void shouldGetBlueprintsByAuthor() throws Exception {
        Blueprint bp1 = new Blueprint("john", "house", List.of(new Point(10, 10)));
        when(services.getBlueprintsByAuthor("john")).thenReturn(new HashSet<>(List.of(bp1)));

        mockMvc.perform(get("/api/v1/blueprints/john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].author").value("john"));
    }

    @Test
    public void shouldGetBlueprintByAuthorAndName() throws Exception {
        Blueprint bp1 = new Blueprint("john", "house", List.of(new Point(10, 10)));
        when(services.getBlueprint("john", "house")).thenReturn(bp1);

        mockMvc.perform(get("/api/v1/blueprints/john/house"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("house"));
    }

    @Test
    public void shouldCreateBlueprint() throws Exception {
        String json = "{\"author\":\"john\",\"name\":\"newbp\",\"points\":[{\"x\":1,\"y\":1}]}";

        mockMvc.perform(post("/api/v1/blueprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));
    }

    @Test
    public void shouldAddPoint() throws Exception {
        String json = "{\"x\":5,\"y\":5}";

        mockMvc.perform(put("/api/v1/blueprints/john/house/points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.code").value(202));
    }

    @Test
    public void shouldReturn404WhenBlueprintNotFound() throws Exception {
        when(services.getBlueprint("none", "none")).thenThrow(new BlueprintNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/blueprints/none/none"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn404WhenAuthorNotFound() throws Exception {
        when(services.getBlueprintsByAuthor("none")).thenThrow(new BlueprintNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/blueprints/none"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn400OnBlueprintPersistenceException() throws Exception {
        doThrow(new BlueprintPersistenceException("Error"))
                .when(services).addNewBlueprint(any());

        String json = "{\"author\":\"john\",\"name\":\"newbp\",\"points\":[{\"x\":1,\"y\":1}]}";
        mockMvc.perform(post("/api/v1/blueprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
