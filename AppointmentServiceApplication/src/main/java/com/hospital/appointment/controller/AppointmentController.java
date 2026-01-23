/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.controller;

/**
 *
 * @author mduhijod
 */

import com.hospital.appointment.dto.*;
import com.hospital.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Book new appointment
     * POST /appointments/book
     */
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<AppointmentDTO>> bookAppointment(
            @Valid @RequestBody BookAppointmentDTO bookAppointmentDTO) {
        
        log.info("API: Book appointment for PIN: {}", bookAppointmentDTO.getPinNumber());
        AppointmentDTO appointment = appointmentService.bookAppointment(bookAppointmentDTO);
        
        ApiResponse<AppointmentDTO> response = ApiResponse.success(
            "Appointment booked successfully. Token Number: " + appointment.getTokenNumber(),
            appointment
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get appointment by ID
     * GET /appointments/{appointmentId}
     */
    @GetMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<AppointmentDTO>> getAppointmentById(
            @PathVariable String appointmentId) {
        
        log.info("API: Get appointment - {}", appointmentId);
        AppointmentDTO appointment = appointmentService.getAppointmentById(appointmentId);
        
        ApiResponse<AppointmentDTO> response = ApiResponse.success(
            "Appointment found",
            appointment
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get patient appointments
     * GET /appointments/patient/{pinNumber}
     */
    @GetMapping("/patient/{pinNumber}")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getPatientAppointments(
            @PathVariable String pinNumber) {
        
        log.info("API: Get appointments for patient: {}", pinNumber);
        List<AppointmentDTO> appointments = appointmentService.getPatientAppointments(pinNumber);
        
        String message = appointments.size() + " appointment(s) found";
        ApiResponse<List<AppointmentDTO>> response = ApiResponse.success(message, appointments);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get doctor appointments by date
     * GET /appointments/doctor/{doctorId}/date/{date}
     */
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<ApiResponse<List<AppointmentSummaryDTO>>> getDoctorAppointments(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        log.info("API: Get appointments for doctor: {} on {}", doctorId, date);
        List<AppointmentSummaryDTO> appointments = appointmentService.getDoctorAppointments(doctorId, date);
        
        String message = appointments.size() + " appointment(s) found";
        ApiResponse<List<AppointmentSummaryDTO>> response = ApiResponse.success(message, appointments);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get today's appointments
     * GET /appointments/today
     */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<AppointmentSummaryDTO>>> getTodaysAppointments() {
        log.info("API: Get today's appointments");
        List<AppointmentSummaryDTO> appointments = appointmentService.getTodaysAppointments();
        
        String message = appointments.size() + " appointment(s) found for today";
        ApiResponse<List<AppointmentSummaryDTO>> response = ApiResponse.success(message, appointments);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get upcoming appointments for patient
     * GET /appointments/patient/{pinNumber}/upcoming
     */
    @GetMapping("/patient/{pinNumber}/upcoming")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getUpcomingAppointments(
            @PathVariable String pinNumber) {
        
        log.info("API: Get upcoming appointments for patient: {}", pinNumber);
        List<AppointmentDTO> appointments = appointmentService.getUpcomingAppointments(pinNumber);
        
        String message = appointments.size() + " upcoming appointment(s) found";
        ApiResponse<List<AppointmentDTO>> response = ApiResponse.success(message, appointments);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check-in appointment
     * PUT /appointments/{appointmentId}/checkin
     */
    @PutMapping("/{appointmentId}/checkin")
    public ResponseEntity<ApiResponse<AppointmentDTO>> checkInAppointment(
            @PathVariable String appointmentId) {
        
        log.info("API: Check-in appointment - {}", appointmentId);
        AppointmentDTO appointment = appointmentService.checkInAppointment(appointmentId);
        
        ApiResponse<AppointmentDTO> response = ApiResponse.success(
            "Patient checked in successfully",
            appointment
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Start consultation
     * PUT /appointments/{appointmentId}/start-consultation
     */
    @PutMapping("/{appointmentId}/start-consultation")
    public ResponseEntity<ApiResponse<AppointmentDTO>> startConsultation(
            @PathVariable String appointmentId) {
        
        log.info("API: Start consultation - {}", appointmentId);
        AppointmentDTO appointment = appointmentService.startConsultation(appointmentId);
        
        ApiResponse<AppointmentDTO> response = ApiResponse.success(
            "Consultation started",
            appointment
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Complete consultation
     * PUT /appointments/{appointmentId}/complete-consultation
     */
    @PutMapping("/{appointmentId}/complete-consultation")
    public ResponseEntity<ApiResponse<AppointmentDTO>> completeConsultation(
            @PathVariable String appointmentId) {
        
        log.info("API: Complete consultation - {}", appointmentId);
        AppointmentDTO appointment = appointmentService.completeConsultation(appointmentId);
        
        ApiResponse<AppointmentDTO> response = ApiResponse.success(
            "Consultation completed",
            appointment
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel appointment
     * POST /appointments/cancel
     */
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancelAppointment(
            @Valid @RequestBody CancelAppointmentDTO cancelDTO) {
        
        log.info("API: Cancel appointment - {}", cancelDTO.getAppointmentId());
        String message = appointmentService.cancelAppointment(cancelDTO);
        
        ApiResponse<String> response = ApiResponse.success(message, null);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Reschedule appointment
     * POST /appointments/reschedule
     */
    @PostMapping("/reschedule")
    public ResponseEntity<ApiResponse<AppointmentDTO>> rescheduleAppointment(
            @Valid @RequestBody RescheduleAppointmentDTO rescheduleDTO) {
        
        log.info("API: Reschedule appointment - {}", rescheduleDTO.getAppointmentId());
        AppointmentDTO appointment = appointmentService.rescheduleAppointment(rescheduleDTO);
        
        ApiResponse<AppointmentDTO> response = ApiResponse.success(
            "Appointment rescheduled successfully",
            appointment
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark as no-show
     * PUT /appointments/{appointmentId}/no-show
     */
    @PutMapping("/{appointmentId}/no-show")
    public ResponseEntity<ApiResponse<AppointmentDTO>> markNoShow(
            @PathVariable String appointmentId) {
        
        log.info("API: Mark no-show - {}", appointmentId);
        AppointmentDTO appointment = appointmentService.markNoShow(appointmentId);
        
        ApiResponse<AppointmentDTO> response = ApiResponse.success(
            "Appointment marked as no-show",
            appointment
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search appointments
     * GET /appointments/search?query=value
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AppointmentSummaryDTO>>> searchAppointments(
            @RequestParam String query) {
        
        log.info("API: Search appointments - Query: {}", query);
        List<AppointmentSummaryDTO> appointments = appointmentService.searchAppointments(query);
        
        String message = appointments.isEmpty() 
            ? "No appointments found" 
            : appointments.size() + " appointment(s) found";
        
        ApiResponse<List<AppointmentSummaryDTO>> response = ApiResponse.success(message, appointments);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get appointments by status
     * GET /appointments/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAppointmentsByStatus(
            @PathVariable String status) {
        
        log.info("API: Get appointments by status - {}", status);
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByStatus(status);
        
        ApiResponse<List<AppointmentDTO>> response = ApiResponse.success(
            appointments.size() + " appointment(s) with status " + status,
            appointments
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get appointments by date range
     * GET /appointments/range?startDate=2025-10-01&endDate=2025-10-31
     */
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<AppointmentSummaryDTO>>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        log.info("API: Get appointments from {} to {}", startDate, endDate);
        List<AppointmentSummaryDTO> appointments = appointmentService
            .getAppointmentsByDateRange(startDate, endDate);
        
        ApiResponse<List<AppointmentSummaryDTO>> response = ApiResponse.success(
            appointments.size() + " appointment(s) found",
            appointments
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if appointment exists
     * GET /appointments/exists/{appointmentId}
     */
    @GetMapping("/exists/{appointmentId}")
    public ResponseEntity<ApiResponse<Boolean>> checkAppointmentExists(
            @PathVariable String appointmentId) {
        
        boolean exists = appointmentService.existsByAppointmentId(appointmentId);
        
        ApiResponse<Boolean> response = ApiResponse.success(
            exists ? "Appointment exists" : "Appointment not found",
            exists
        );
        
        return ResponseEntity.ok(response);
    }
}
