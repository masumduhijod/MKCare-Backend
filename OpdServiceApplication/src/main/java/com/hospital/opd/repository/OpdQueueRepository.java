/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.repository;

/**
 *
 * @author mduhijod
 */

import com.hospital.opd.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// ========== OpdQueueRepository ==========
@Repository
public interface OpdQueueRepository extends JpaRepository<OpdQueue, Long> {
    
    List<OpdQueue> findByDoctorIdAndQueueDateOrderByTokenNumberAsc(String doctorId, LocalDate queueDate);
    
    @Query("SELECT q FROM OpdQueue q WHERE q.doctorId = :doctorId AND q.queueDate = :date " +
           "AND q.status IN ('WAITING', 'IN_CONSULTATION') ORDER BY q.tokenNumber")
    List<OpdQueue> findActiveQueueByDoctorAndDate(@Param("doctorId") String doctorId, @Param("date") LocalDate date);
    
    Optional<OpdQueue> findByAppointmentId(String appointmentId);
    
    Optional<OpdQueue> findByCvrNumber(String cvrNumber);
    
    @Query("SELECT q FROM OpdQueue q WHERE q.queueDate = :date AND q.status = 'WAITING' ORDER BY q.tokenNumber")
    List<OpdQueue> findWaitingQueueByDate(@Param("date") LocalDate date);
    
    long countByDoctorIdAndQueueDateAndStatus(String doctorId, LocalDate queueDate, OpdQueue.QueueStatus status);
}
