package com.hospital.doctor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.doctor.dto.DoctorDTO;
import com.hospital.doctor.dto.DoctorRegistrationDTO;
import com.hospital.doctor.service.DoctorService;
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
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /doctors/register - Doctor Registration Test")
    void testRegisterDoctor_Success() throws Exception {
        DoctorRegistrationDTO regDTO = new DoctorRegistrationDTO();
        regDTO.setFirstName("Dr. Ramesh");
        regDTO.setLastName("Kulkarni");
        regDTO.setSpecialization("Cardiology");

        DoctorDTO mockDoctor = new DoctorDTO();
        mockDoctor.setDoctorId("DOC1001");
        mockDoctor.setFirstName("Dr. Ramesh");
        mockDoctor.setLastName("Kulkarni");
        mockDoctor.setSpecialization("Cardiology");

        Mockito.when(doctorService.registerDoctor(any(DoctorRegistrationDTO.class))).thenReturn(mockDoctor);

        mockMvc.perform(post("/doctors/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.doctorId").value("DOC1001"))
                .andExpect(jsonPath("$.data.specialization").value("Cardiology"));
    }

    @Test
    @DisplayName("GET /doctors/{doctorId} - Get Doctor Test")
    void testGetDoctorById_Success() throws Exception {
        String doctorId = "DOC1001";
        DoctorDTO mockDoctor = new DoctorDTO();
        mockDoctor.setDoctorId(doctorId);
        mockDoctor.setFirstName("Dr. Ramesh");
        mockDoctor.setSpecialization("Cardiology");

        Mockito.when(doctorService.getDoctorById(doctorId)).thenReturn(mockDoctor);

        mockMvc.perform(get("/doctors/" + doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.doctorId").value(doctorId))
                .andExpect(jsonPath("$.data.firstName").value("Dr. Ramesh"));
    }
}
