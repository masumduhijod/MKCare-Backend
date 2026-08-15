package com.hospital.cvr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hospital.cvr.dto.CreateCvrDTO;
import com.hospital.cvr.dto.CvrDTO;
import com.hospital.cvr.dto.CvrSummaryDTO;
import com.hospital.cvr.service.CvrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CvrControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CvrService cvrService;

    private ObjectMapper objectMapper;
    private CvrDTO mockCvr;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockCvr = new CvrDTO();
        mockCvr.setCvrNumber("CVR2026000001");
        mockCvr.setPinNumber("PIN100001");
        mockCvr.setDoctorId("DOC1001");
        mockCvr.setPatientName("Rahul Sharma");
        mockCvr.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("POST /cvr/create - Create CVR Success")
    void testCreateCVR() throws Exception {
        CreateCvrDTO createDTO = new CreateCvrDTO();
        createDTO.setPinNumber("PIN100001");
        createDTO.setDoctorId("DOC1001");
        createDTO.setVisitType("OPD");

        Mockito.when(cvrService.createCVR(any(CreateCvrDTO.class))).thenReturn(mockCvr);

        mockMvc.perform(post("/cvr/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cvrNumber").value("CVR2026000001"))
                .andExpect(jsonPath("$.data.pinNumber").value("PIN100001"));
    }

    @Test
    @DisplayName("GET /cvr/{cvrNumber} - Get CVR by Number")
    void testGetCVRByNumber() throws Exception {
        Mockito.when(cvrService.getCVRByNumber("CVR2026000001")).thenReturn(mockCvr);

        mockMvc.perform(get("/cvr/CVR2026000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cvrNumber").value("CVR2026000001"));
    }

    @Test
    @DisplayName("GET /cvr/today - Get Today's CVRs")
    void testGetTodaysCVRs() throws Exception {
        CvrSummaryDTO summary = new CvrSummaryDTO();
        summary.setCvrNumber("CVR2026000001");
        summary.setPinNumber("PIN100001");
        summary.setPatientName("Rahul Sharma");

        Mockito.when(cvrService.getTodaysCVRs()).thenReturn(Collections.singletonList(summary));

        mockMvc.perform(get("/cvr/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].cvrNumber").value("CVR2026000001"));
    }
}
