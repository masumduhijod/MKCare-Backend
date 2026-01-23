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
// ========== CVR Vitals Repository ==========

import com.hospital.cvr.entity.CvrVitals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvrVitalsRepository extends JpaRepository<CvrVitals, Long> {

    /**
     * Get vitals by CVR ID
     */
    List<CvrVitals> findByCaseVisitRecord_CvrIdOrderByRecordedAtDesc(Long cvrId);

    /**
     * Get latest vitals for a CVR
     */
    @Query("SELECT v FROM CvrVitals v WHERE v.caseVisitRecord.cvrId = :cvrId " +
           "ORDER BY v.recordedAt DESC")
    List<CvrVitals> findLatestVitalsByCvrId(@Param("cvrId") Long cvrId);

    /**
     * Get vitals by CVR number
     */
    @Query("SELECT v FROM CvrVitals v WHERE v.caseVisitRecord.cvrNumber = :cvrNumber " +
           "ORDER BY v.recordedAt DESC")
    List<CvrVitals> findByCvrNumber(@Param("cvrNumber") String cvrNumber);

    /**
     * Check if vitals recorded for CVR
     */
    @Query("SELECT COUNT(v) > 0 FROM CvrVitals v WHERE v.caseVisitRecord.cvrId = :cvrId")
    boolean existsByCvrId(@Param("cvrId") Long cvrId);
}