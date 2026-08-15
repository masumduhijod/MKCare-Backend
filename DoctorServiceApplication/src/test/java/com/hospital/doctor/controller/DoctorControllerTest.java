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

    @Test
    @DisplayName("GET /doctors/active - Get Active Doctors")
    void testGetActiveDoctors() throws Exception {
        DoctorDTO mockDoctor = new DoctorDTO();
        mockDoctor.setDoctorId("DOC1001");
        mockDoctor.setFirstName("Dr. Rajesh");
        mockDoctor.setLastName("Verma");

        Mockito.when(doctorService.getAllActiveDoctors()).thenReturn(java.util.Collections.singletonList(mockDoctor));

        mockMvc.perform(get("/doctors/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].doctorId").value("DOC1001"));
    }

    @Test
    @DisplayName("GET /doctors/departments - Get All Departments")
    void testGetAllDepartments() throws Exception {
        Mockito.when(doctorService.getAllDepartments()).thenReturn(java.util.Arrays.asList("OPD", "Cardiology"));

        mockMvc.perform(get("/doctors/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0]").value("OPD"));
    }

    @Test
    @DisplayName("GET /doctors/available/department/{department} - Get Available Doctors by Department")
    void testGetAvailableDoctorsByDepartment() throws Exception {
        com.hospital.doctor.dto.DoctorSummaryDTO mockSummary = new com.hospital.doctor.dto.DoctorSummaryDTO();
        mockSummary.setDoctorId("DOC1001");
        mockSummary.setDepartment("OPD");

        Mockito.when(doctorService.getAvailableDoctorsByDepartment("OPD")).thenReturn(java.util.Collections.singletonList(mockSummary));

        mockMvc.perform(get("/doctors/available/department/OPD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].doctorId").value("DOC1001"));
    }

    @Test
    @DisplayName("GET /doctors/count - Get Total Active Doctor Count")
    void testGetDoctorCount() throws Exception {
        Mockito.when(doctorService.getTotalActiveDoctors()).thenReturn(1L);

        mockMvc.perform(get("/doctors/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(1));
    }
}
