/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.repository;

import com.hospital.opd.entity.Prescription;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mduhijod
 */
// ========== PrescriptionRepository ==========
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    
    Optional<Prescription> findByPrescriptionId(String prescriptionId);
    
    @Query("SELECT p.prescriptionId FROM Prescription p ORDER BY p.id DESC")
    List<String> findTopByOrderByIdDesc();
    
    List<Prescription> findByPinNumberOrderByPrescriptionDateDesc(String pinNumber);
    
    Optional<Prescription> findByConsultationId(String consultationId);
    
    List<Prescription> findByDoctorIdAndPrescriptionDateBetween(String doctorId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.status = 'ACTIVE' AND p.expiryDate < :today")
    List<Prescription> findExpiredPrescriptions(@Param("today") LocalDate today);
}
