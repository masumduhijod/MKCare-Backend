/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.repository;

/**
 *
 * @author mduhijod
 */

import com.hospital.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Find appointment by appointment ID
     */
    Optional<Appointment> findByAppointmentId(String appointmentId);

    /**
     * Check if appointment exists
     */
    boolean existsByAppointmentId(String appointmentId);

    /**
     * Get last appointment ID for ID generation
     */
    @Query("SELECT a.appointmentId FROM Appointment a ORDER BY a.id DESC")
    List<String> findTopByOrderByIdDesc();

    /**
     * Get appointments by patient PIN
     */
    List<Appointment> findByPinNumberOrderByAppointmentDateDescAppointmentTimeDesc(String pinNumber);

    /**
     * Get appointments by doctor and date
     */
    List<Appointment> findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(
        String doctorId, LocalDate appointmentDate
    );

    /**
     * Get active appointments by doctor and date
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND a.appointmentDate = :date " +
           "AND a.status IN ('SCHEDULED', 'CHECKED_IN', 'CONSULTING') " +
           "ORDER BY a.appointmentTime")
    List<Appointment> findActiveAppointmentsByDoctorAndDate(
        @Param("doctorId") String doctorId,
        @Param("date") LocalDate date
    );

    /**
     * Get last token number for doctor on specific date
     */
    @Query("SELECT MAX(a.tokenNumber) FROM Appointment a " +
           "WHERE a.doctorId = :doctorId AND a.appointmentDate = :date")
    Integer findMaxTokenByDoctorAndDate(
        @Param("doctorId") String doctorId,
        @Param("date") LocalDate date
    );

    /**
     * Get today's appointments
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :today " +
           "ORDER BY a.appointmentTime")
    List<Appointment> findTodaysAppointments(@Param("today") LocalDate today);

    /**
     * Get appointments by date range
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<Appointment> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get appointments by status
     */
    List<Appointment> findByStatusOrderByAppointmentDateDescAppointmentTimeDesc(
        Appointment.AppointmentStatus status
    );

    /**
     * Count appointments by doctor and date
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND a.appointmentDate = :date " +
           "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')")
    long countByDoctorAndDate(
        @Param("doctorId") String doctorId,
        @Param("date") LocalDate date
    );

    /**
     * Find appointment by CVR number
     */
    Optional<Appointment> findByCvrNumber(String cvrNumber);

    /**
     * Check if patient has appointment on date with doctor
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.pinNumber = :pinNumber " +
           "AND a.doctorId = :doctorId AND a.appointmentDate = :date " +
           "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')")
    boolean existsByPatientDoctorAndDate(
        @Param("pinNumber") String pinNumber,
        @Param("doctorId") String doctorId,
        @Param("date") LocalDate date
    );

    /**
     * Get upcoming appointments for patient
     */
    @Query("SELECT a FROM Appointment a WHERE a.pinNumber = :pinNumber " +
           "AND a.appointmentDate >= :today " +
           "AND a.status IN ('SCHEDULED', 'CHECKED_IN') " +
           "ORDER BY a.appointmentDate, a.appointmentTime")
    List<Appointment> findUpcomingAppointmentsByPatient(
        @Param("pinNumber") String pinNumber,
        @Param("today") LocalDate today
    );

    /**
     * Search appointments
     */
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.appointmentId LIKE %:searchTerm% OR " +
           "a.pinNumber LIKE %:searchTerm% OR " +
           "a.cvrNumber LIKE %:searchTerm%")
    List<Appointment> searchAppointments(@Param("searchTerm") String searchTerm);
}

