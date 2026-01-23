/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.repository;

import com.hospital.appointment.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    /**
     * Find slots by doctor and date
     */
    List<AppointmentSlot> findByDoctorIdAndSlotDateOrderBySlotTimeAsc(
        String doctorId, LocalDate slotDate
    );

    /**
     * Find available slots by doctor and date
     */
    @Query("SELECT s FROM AppointmentSlot s WHERE s.doctorId = :doctorId " +
           "AND s.slotDate = :date AND s.isAvailable = true " +
           "ORDER BY s.slotTime")
    List<AppointmentSlot> findAvailableSlots(
        @Param("doctorId") String doctorId,
        @Param("date") LocalDate date
    );

    /**
     * Find specific slot by doctor, date and time
     */
    Optional<AppointmentSlot> findByDoctorIdAndSlotDateAndSlotTime(
        String doctorId, LocalDate slotDate, LocalTime slotTime
    );

    /**
     * Check if slot exists
     */
    @Query("SELECT COUNT(s) > 0 FROM AppointmentSlot s WHERE s.doctorId = :doctorId " +
           "AND s.slotDate = :date AND s.slotTime = :time")
    boolean existsByDoctorDateAndTime(
        @Param("doctorId") String doctorId,
        @Param("date") LocalDate date,
        @Param("time") LocalTime time
    );

    /**
     * Count available slots for doctor on date
     */
    @Query("SELECT COUNT(s) FROM AppointmentSlot s WHERE s.doctorId = :doctorId " +
           "AND s.slotDate = :date AND s.isAvailable = true")
    long countAvailableSlots(
        @Param("doctorId") String doctorId,
        @Param("date") LocalDate date
    );

    /**
     * Count total slots for doctor on date
     */
    long countByDoctorIdAndSlotDate(String doctorId, LocalDate slotDate);

    /**
     * Delete old slots (cleanup)
     */
    @Modifying
    @Query("DELETE FROM AppointmentSlot s WHERE s.slotDate < :date")
    void deleteOldSlots(@Param("date") LocalDate date);

    /**
     * Get slots by date range
     */
    @Query("SELECT s FROM AppointmentSlot s WHERE s.doctorId = :doctorId " +
           "AND s.slotDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.slotDate, s.slotTime")
    List<AppointmentSlot> findSlotsByDateRange(
        @Param("doctorId") String doctorId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // *** NEW: Find all slots for a schedule ***
    @Query("SELECT s FROM AppointmentSlot s WHERE s.scheduleId = :scheduleId " +
           "ORDER BY s.slotDate, s.slotTime")
    List<AppointmentSlot> findByScheduleId(@Param("scheduleId") Long scheduleId);
    
    // *** NEW: Count slots for a schedule ***
    long countByScheduleId(Long scheduleId);
    
    // *** NEW: Delete all slots for a schedule ***
    // Note: This is handled automatically by CASCADE DELETE in FK constraint
    // But keeping this method for manual deletion if needed
    @Modifying
    @Query("DELETE FROM AppointmentSlot s WHERE s.scheduleId = :scheduleId")
    void deleteByScheduleId(@Param("scheduleId") Long scheduleId);
}