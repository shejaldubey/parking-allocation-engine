package com.smartparking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.controller.dto.ParkRequest;
import com.smartparking.model.VehicleSize;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SmartParkingApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void parkSmallVehicleReturnsAssignedSpot() throws Exception {
        ParkRequest request = new ParkRequest("TEST-SMALL-001", VehicleSize.SMALL);
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/park")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate", is("TEST-SMALL-001")))
                .andExpect(jsonPath("$.vehicleSize", is("SMALL")))
                .andExpect(jsonPath("$.spotId").exists())
                .andExpect(jsonPath("$.distanceToExit").exists());
    }
}
