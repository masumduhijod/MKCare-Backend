package com.hospital.opd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.opd.dto.ConsultationDTO;
import com.hospital.opd.dto.CreateConsultationDTO;
import com.hospital.opd.service.ConsultationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ConsultationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultationService consultationService;

    private ObjectMapper objectMapper;
    private ConsultationDTO mockConsultation;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockConsultation = new ConsultationDTO();
        mockConsultation.setConsultationId("CNS0000000001");
        mockConsultation.setDoctorId("DOC1001");
        mockConsultation.setPinNumber("PIN100001");
        mockConsultation.setDiagnosis("Common Cold");
    }

    @Test
    @DisplayName("POST /opd/consultations/create - Create Consultation")
    void testCreateConsultation() throws Exception {
        CreateConsultationDTO createDTO = new CreateConsultationDTO();
        createDTO.setDoctorId("DOC1001");
        createDTO.setPinNumber("PIN100001");
        createDTO.setDiagnosis("Common Cold");

        Mockito.when(consultationService.createConsultation(any(CreateConsultationDTO.class))).thenReturn(mockConsultation);

        mockMvc.perform(post("/opd/consultations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.consultationId").value("CNS0000000001"))
                .andExpect(jsonPath("$.data.diagnosis").value("Common Cold"));
    }

    @Test
    @DisplayName("GET /opd/consultations/{consultationId} - Get Consultation by ID")
    void testGetConsultation() throws Exception {
        Mockito.when(consultationService.getConsultationById("CNS0000000001")).thenReturn(mockConsultation);

        mockMvc.perform(get("/opd/consultations/CNS0000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.consultationId").value("CNS0000000001"));
    }
}
