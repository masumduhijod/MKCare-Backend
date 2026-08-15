package com.hospital.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hospital.appointment.dto.AppointmentDTO;
import com.hospital.appointment.dto.AppointmentSummaryDTO;
import com.hospital.appointment.dto.BookAppointmentDTO;
import com.hospital.appointment.service.AppointmentService;
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
import java.time.LocalTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private ObjectMapper objectMapper;
    private AppointmentDTO mockAppointment;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockAppointment = new AppointmentDTO();
        mockAppointment.setAppointmentId("APT2026000001");
        mockAppointment.setTokenNumber(1);
        mockAppointment.setPinNumber("PIN100001");
        mockAppointment.setPatientName("Rahul Sharma");
        mockAppointment.setDoctorId("DOC1001");
        mockAppointment.setDoctorName("Dr. Rajesh Verma");
        mockAppointment.setAppointmentDate(LocalDate.now());
        mockAppointment.setAppointmentTime(LocalTime.of(10, 0));
        mockAppointment.setStatus("SCHEDULED");
    }

    @Test
    @DisplayName("POST /appointments/book - Book Appointment Success")
    void testBookAppointment_Success() throws Exception {
        BookAppointmentDTO bookDTO = new BookAppointmentDTO();
        bookDTO.setPinNumber("PIN100001");
        bookDTO.setDoctorId("DOC1001");
        bookDTO.setAppointmentDate(LocalDate.now());
        bookDTO.setAppointmentTime(LocalTime.of(10, 0));
        bookDTO.setAppointmentType("Consultation");

        Mockito.when(appointmentService.bookAppointment(any(BookAppointmentDTO.class))).thenReturn(mockAppointment);

        mockMvc.perform(post("/appointments/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.appointmentId").value("APT2026000001"))
                .andExpect(jsonPath("$.data.pinNumber").value("PIN100001"));
    }

    @Test
    @DisplayName("GET /appointments/{appointmentId} - Get Appointment by ID")
    void testGetAppointmentById_Success() throws Exception {
        Mockito.when(appointmentService.getAppointmentById("APT2026000001")).thenReturn(mockAppointment);

        mockMvc.perform(get("/appointments/APT2026000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.appointmentId").value("APT2026000001"));
    }

    @Test
    @DisplayName("GET /appointments/today - Get Today's Appointments")
    void testGetTodaysAppointments() throws Exception {
        AppointmentSummaryDTO summary = new AppointmentSummaryDTO();
        summary.setAppointmentId("APT2026000001");
        summary.setPinNumber("PIN100001");
        summary.setPatientName("Rahul Sharma");
        summary.setDoctorId("DOC1001");

        Mockito.when(appointmentService.getTodaysAppointments()).thenReturn(Collections.singletonList(summary));

        mockMvc.perform(get("/appointments/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].appointmentId").value("APT2026000001"));
    }

    @Test
    @DisplayName("PUT /appointments/{appointmentId}/checkin - Check-in Patient")
    void testCheckInAppointment() throws Exception {
        mockAppointment.setStatus("CHECKED_IN");
        Mockito.when(appointmentService.checkInAppointment("APT2026000001")).thenReturn(mockAppointment);

        mockMvc.perform(put("/appointments/APT2026000001/checkin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CHECKED_IN"));
    }
}
