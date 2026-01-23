/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.repository;

/**
 *
 * @author mduhijod
 */

import com.hospital.cvr.entity.CaseVisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CvrRepository extends JpaRepository<CaseVisitRecord, Long> {

    /**
     * Find CVR by CVR number
     */
    Optional<CaseVisitRecord> findByCvrNumber(String cvrNumber);

    /**
     * Check if CVR exists
     */
    boolean existsByCvrNumber(String cvrNumber);

    /**
     * Get last CVR number for date-based generation
     */
    @Query("SELECT c.cvrNumber FROM CaseVisitRecord c ORDER BY c.cvrId DESC")
    List<String> findTopByOrderByCvrIdDesc();

    /**
     * Get all CVRs for a patient by PIN
     */
    List<CaseVisitRecord> findByPinNumberOrderByVisitDateDesc(String pinNumber);

    /**
     * Get CVRs by patient ID
     */
    List<CaseVisitRecord> findByPatientIdOrderByVisitDateDesc(Long patientId);

    /**
     * Get CVRs by date
     */
    List<CaseVisitRecord> findByVisitDateOrderByVisitTimeAsc(LocalDate visitDate);

    /**
     * Get CVRs by date and doctor
     */
    List<CaseVisitRecord> findByVisitDateAndDoctorIdOrderByVisitTimeAsc(
        LocalDate visitDate, String doctorId
    );

    /**
     * Get CVRs by status
     */
    List<CaseVisitRecord> findByStatusOrderByVisitDateDesc(CaseVisitRecord.CvrStatus status);

    /**
     * Count total visits for a patient
     */
    @Query("SELECT COUNT(c) FROM CaseVisitRecord c WHERE c.pinNumber = :pinNumber")
    long countByPinNumber(@Param("pinNumber") String pinNumber);

    /**
     * Get last visit for a patient
     */
    @Query("SELECT c FROM CaseVisitRecord c WHERE c.pinNumber = :pinNumber " +
           "ORDER BY c.visitDate DESC, c.visitTime DESC")
    List<CaseVisitRecord> findLastVisitByPinNumber(@Param("pinNumber") String pinNumber);

    /**
     * Get today's CVRs
     */
    @Query("SELECT c FROM CaseVisitRecord c WHERE c.visitDate = :today " +
           "ORDER BY c.createdAt DESC")
    List<CaseVisitRecord> findTodaysCVRs(@Param("today") LocalDate today);

    /**
     * Get CVRs by date range
     */
    @Query("SELECT c FROM CaseVisitRecord c WHERE c.visitDate BETWEEN :startDate AND :endDate " +
           "ORDER BY c.visitDate DESC, c.visitTime DESC")
    List<CaseVisitRecord> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count CVRs for today
     */
    @Query("SELECT COUNT(c) FROM CaseVisitRecord c WHERE c.visitDate = :today")
    long countTodaysCVRs(@Param("today") LocalDate today);

    /**
     * Get recent CVRs (limit)
     */
    @Query("SELECT c FROM CaseVisitRecord c ORDER BY c.createdAt DESC")
    List<CaseVisitRecord> findRecentCVRs();

    /**
     * Search CVRs by patient name or CVR number
     */
    @Query("SELECT c FROM CaseVisitRecord c WHERE " +
           "c.cvrNumber LIKE %:searchTerm% OR c.pinNumber LIKE %:searchTerm%")
    List<CaseVisitRecord> searchCVRs(@Param("searchTerm") String searchTerm);
    
    /**
 * Get latest CVR by appointment ID
 */
Optional<CaseVisitRecord> findTopByAppointmentIdOrderByCreatedAtDesc(String appointmentId);

}
