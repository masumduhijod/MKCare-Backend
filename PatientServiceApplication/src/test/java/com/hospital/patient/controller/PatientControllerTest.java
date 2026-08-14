package com.hospital.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hospital.patient.dto.PatientDTO;
import com.hospital.patient.dto.PatientRegistrationDTO;
import com.hospital.patient.service.PatientService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /patients/register - Success Test")
    void testRegisterPatient_Success() throws Exception {
        PatientRegistrationDTO regDTO = new PatientRegistrationDTO();
        regDTO.setFirstName("Rahul");
        regDTO.setLastName("Sharma");
        regDTO.setGender("MALE");
        regDTO.setContactNumber("9876543210");
        regDTO.setDateOfBirth(LocalDate.of(1995, 5, 15));

        PatientDTO mockSavedPatient = new PatientDTO();
        mockSavedPatient.setPatientId(1L);
        mockSavedPatient.setPinNumber("PIN100001");
        mockSavedPatient.setFirstName("Rahul");
        mockSavedPatient.setLastName("Sharma");
        mockSavedPatient.setGender("MALE");
        mockSavedPatient.setContactNumber("9876543210");

        Mockito.when(patientService.registerPatient(any(PatientRegistrationDTO.class))).thenReturn(mockSavedPatient);

        mockMvc.perform(post("/patients/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pinNumber").value("PIN100001"))
                .andExpect(jsonPath("$.data.firstName").value("Rahul"));
    }

    @Test
    @DisplayName("GET /patients/pin/{pinNumber} - Success Test")
    void testGetPatientByPIN_Success() throws Exception {
        String pinNumber = "PIN100001";
        PatientDTO mockPatient = new PatientDTO();
        mockPatient.setPatientId(1L);
        mockPatient.setPinNumber(pinNumber);
        mockPatient.setFirstName("Rahul");
        mockPatient.setLastName("Sharma");

        Mockito.when(patientService.getPatientByPIN(pinNumber)).thenReturn(mockPatient);

        mockMvc.perform(get("/patients/pin/" + pinNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pinNumber").value(pinNumber))
                .andExpect(jsonPath("$.data.firstName").value("Rahul"));
    }
}
