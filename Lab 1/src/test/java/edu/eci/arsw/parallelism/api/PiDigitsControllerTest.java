package edu.eci.arsw.parallelism.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PiDigitsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnDigits() throws Exception {
        mockMvc.perform(get("/api/v1/pi/digits")
                        .param("start", "0")
                        .param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value(0))
                .andExpect(jsonPath("$.count").value(5))
                .andExpect(jsonPath("$.digits").value("243F6"));
    }

    @Test
    void shouldReturnDigitsWithThreadJoinStrategy() throws Exception {
        mockMvc.perform(get("/api/v1/pi/digits")
                        .param("start", "0")
                        .param("count", "5")
                        .param("threads", "2")
                        .param("strategy", "thread-join"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.digits").value("243F6"));
    }

    @Test
    void shouldReturnDigitsZeroCount() throws Exception {
        mockMvc.perform(get("/api/v1/pi/digits")
                        .param("start", "0")
                        .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value(0))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.digits").value("2"));
    }

    @Test
    void shouldReturnBadRequestForNegativeStart() throws Exception {
        mockMvc.perform(get("/api/v1/pi/digits")
                        .param("start", "-1")
                        .param("count", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForNegativeCount() throws Exception {
        mockMvc.perform(get("/api/v1/pi/digits")
                        .param("start", "0")
                        .param("count", "-1"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldReturnBadRequestForInvalidThreads() throws Exception {
        mockMvc.perform(get("/api/v1/pi/digits")
                        .param("start", "0")
                        .param("count", "5")
                        .param("threads", "0"))
                .andExpect(status().isBadRequest());
    }
}