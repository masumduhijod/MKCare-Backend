/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.repository;

/**
 *
 * @author mduhijod
 */
// ========== Doctor Schedule Repository ==========

import com.hospital.doctor.entity.Doctor;
import com.hospital.doctor.entity.DoctorSchedule;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
//public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
//
//    /**
//     * Find schedules by doctor
//     */
//    List<DoctorSchedule> findByDoctor(Doctor doctor);
//
//    /**
//     * Find schedules by doctor ID
//     */
//    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId")
//    List<DoctorSchedule> findByDoctorId(@Param("doctorId") String doctorId);
//
//    /**
//     * Find active schedules by doctor ID
//     */
//    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId " +
//           "AND s.isActive = true")
//    List<DoctorSchedule> findActiveDoctorSchedules(@Param("doctorId") String doctorId);
//
//    /**
//     * Find schedule by doctor and day
//     */
//    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId " +
//           "AND s.dayOfWeek = :dayOfWeek")
//    Optional<DoctorSchedule> findByDoctorIdAndDay(
//        @Param("doctorId") String doctorId,
//        @Param("dayOfWeek") DoctorSchedule.DayOfWeek dayOfWeek
//    );
//
//    /**
//     * Check if schedule exists for doctor on specific day
//     */
//    @Query("SELECT COUNT(s) > 0 FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId " +
//           "AND s.dayOfWeek = :dayOfWeek")
//    boolean existsByDoctorIdAndDay(
//        @Param("doctorId") String doctorId,
//        @Param("dayOfWeek") DoctorSchedule.DayOfWeek dayOfWeek
//    );
//
//    /**
//     * Find all schedules for a specific day
//     */
//    List<DoctorSchedule> findByDayOfWeekAndIsActiveTrue(DoctorSchedule.DayOfWeek dayOfWeek);
//
//    /**
//     * Delete schedules by doctor ID
//     */
//    @Query("DELETE FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId")
//    void deleteByDoctorId(@Param("doctorId") String doctorId);
//}


@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    
    /**
     * Find schedules by doctor
     */
    List<DoctorSchedule> findByDoctor(Doctor doctor);
    
    /**
     * Find schedules by doctor ID - ordered by date
     */
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId " +
           "ORDER BY s.scheduleDate ASC")
    List<DoctorSchedule> findByDoctorId(@Param("doctorId") String doctorId);
    
    /**
     * Find active schedules by doctor ID
     */
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId " +
           "AND s.isActive = true ORDER BY s.scheduleDate ASC")
    List<DoctorSchedule> findActiveDoctorSchedules(@Param("doctorId") String doctorId);
    
    // *** CHANGED: Find schedule by doctor and DATE (not day) ***
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId " +
           "AND s.scheduleDate = :scheduleDate")
    Optional<DoctorSchedule> findByDoctorIdAndDate(
        @Param("doctorId") String doctorId,
        @Param("scheduleDate") LocalDate scheduleDate
    );
    
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId "
            + "AND s.scheduleDate = :scheduleDate")
    List<DoctorSchedule> findByDoctorIdAndDate1(
            @Param("doctorId") String doctorId,
            @Param("scheduleDate") LocalDate scheduleDate
    );

    
    // *** CHANGED: Check if schedule exists for doctor on specific DATE ***
    @Query("SELECT COUNT(s) > 0 FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId " +
           "AND s.scheduleDate = :scheduleDate")
    boolean existsByDoctorIdAndDate(
        @Param("doctorId") String doctorId,
        @Param("scheduleDate") LocalDate scheduleDate
    );
    
    // *** NEW: Find schedules between date range ***
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId " +
           "AND s.scheduleDate BETWEEN :startDate AND :endDate " +
           "AND s.isActive = true ORDER BY s.scheduleDate ASC")
    List<DoctorSchedule> findByDoctorIdAndDateRange(
        @Param("doctorId") String doctorId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // *** NEW: Find schedules on or after a date ***
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId " +
           "AND s.scheduleDate >= :fromDate " +
           "AND s.isActive = true ORDER BY s.scheduleDate ASC")
    List<DoctorSchedule> findUpcomingSchedules(
        @Param("doctorId") String doctorId,
        @Param("fromDate") LocalDate fromDate
    );
    
    // *** REMOVED: findByDayOfWeekAndIsActiveTrue - no longer needed ***
    
    /**
     * Delete schedules by doctor ID
     */
    @Query("DELETE FROM DoctorSchedule s WHERE s.doctor.doctorId = :doctorId")
    void deleteByDoctorId(@Param("doctorId") String doctorId);
    
    // *** NEW: Delete old/past schedules ***
    @Query("DELETE FROM DoctorSchedule s WHERE s.scheduleDate < :date")
    void deleteSchedulesBeforeDate(@Param("date") LocalDate date);
}