package com.hospital.opd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.opd.dto.CreateQueueDTO;
import com.hospital.opd.dto.QueueDTO;
import com.hospital.opd.service.QueueService;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueueService queueService;

    private ObjectMapper objectMapper;
    private QueueDTO mockQueue;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockQueue = new QueueDTO();
        mockQueue.setQueueId(1L);
        mockQueue.setTokenNumber(1);
        mockQueue.setDoctorId("DOC1001");
        mockQueue.setPinNumber("PIN100001");
        mockQueue.setPatientName("Rahul Sharma");
        mockQueue.setStatus("WAITING");
    }

    @Test
    @DisplayName("POST /opd/queue/add - Add to Queue")
    void testAddToQueue() throws Exception {
        CreateQueueDTO createDTO = new CreateQueueDTO();
        createDTO.setAppointmentId("APT2026000001");
        createDTO.setDoctorId("DOC1001");
        createDTO.setPinNumber("PIN100001");

        Mockito.when(queueService.addToQueue(any(CreateQueueDTO.class))).thenReturn(mockQueue);

        mockMvc.perform(post("/opd/queue/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.queueId").value(1))
                .andExpect(jsonPath("$.data.tokenNumber").value(1));
    }

    @Test
    @DisplayName("GET /opd/queue/doctor/{doctorId}/date/{date} - Get Doctor Queue")
    void testGetDoctorQueue() throws Exception {
        LocalDate today = LocalDate.now();
        Mockito.when(queueService.getDoctorQueue(eq("DOC1001"), any(LocalDate.class))).thenReturn(Collections.singletonList(mockQueue));

        mockMvc.perform(get("/opd/queue/doctor/DOC1001/date/" + today.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].doctorId").value("DOC1001"));
    }
}
