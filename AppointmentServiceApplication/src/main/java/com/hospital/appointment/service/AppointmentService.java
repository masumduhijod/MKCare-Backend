/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.service;

/**
 *
 * @author mduhijod
 */

import com.hospital.appointment.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    /**
     * Book new appointment
     * @param bookAppointmentDTO - Appointment booking details
     * @return Created appointment
     */
    AppointmentDTO bookAppointment(BookAppointmentDTO bookAppointmentDTO);

    /**
     * Get appointment by ID
     * @param appointmentId - Appointment ID
     * @return Appointment details
     */
    AppointmentDTO getAppointmentById(String appointmentId);

    /**
     * Get appointments by patient
     * @param pinNumber - Patient PIN
     * @return List of appointments
     */
    List<AppointmentDTO> getPatientAppointments(String pinNumber);

    /**
     * Get appointments by doctor and date
     * @param doctorId - Doctor ID
     * @param date - Appointment date
     * @return List of appointments
     */
    List<AppointmentSummaryDTO> getDoctorAppointments(String doctorId, LocalDate date);

    /**
     * Get today's appointments
     * @return List of today's appointments
     */
    List<AppointmentSummaryDTO> getTodaysAppointments();

    /**
     * Get upcoming appointments for patient
     * @param pinNumber - Patient PIN
     * @return List of upcoming appointments
     */
    List<AppointmentDTO> getUpcomingAppointments(String pinNumber);

    /**
     * Check-in appointment
     * @param appointmentId - Appointment ID
     * @return Updated appointment
     */
    AppointmentDTO checkInAppointment(String appointmentId);

    /**
     * Start consultation
     * @param appointmentId - Appointment ID
     * @return Updated appointment
     */
    AppointmentDTO startConsultation(String appointmentId);

    /**
     * Complete consultation
     * @param appointmentId - Appointment ID
     * @return Updated appointment
     */
    AppointmentDTO completeConsultation(String appointmentId);

    /**
     * Cancel appointment
     * @param cancelDTO - Cancellation details
     * @return Success message
     */
    String cancelAppointment(CancelAppointmentDTO cancelDTO);

    /**
     * Reschedule appointment
     * @param rescheduleDTO - Reschedule details
     * @return Updated appointment
     */
    AppointmentDTO rescheduleAppointment(RescheduleAppointmentDTO rescheduleDTO);

    /**
     * Mark as no-show
     * @param appointmentId - Appointment ID
     * @return Updated appointment
     */
    AppointmentDTO markNoShow(String appointmentId);

    /**
     * Get appointments by status
     * @param status - Appointment status
     * @return List of appointments
     */
    List<AppointmentDTO> getAppointmentsByStatus(String status);

    /**
     * Search appointments
     * @param searchTerm - Search term
     * @return List of matching appointments
     */
    List<AppointmentSummaryDTO> searchAppointments(String searchTerm);

    /**
     * Get appointments by date range
     * @param startDate - Start date
     * @param endDate - End date
     * @return List of appointments
     */
    List<AppointmentSummaryDTO> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Check if appointment exists
     * @param appointmentId - Appointment ID
     * @return true if exists
     */
    boolean existsByAppointmentId(String appointmentId);
}
