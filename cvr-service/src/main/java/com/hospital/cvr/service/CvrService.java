/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.service;

/**
 *
 * @author mduhijod
 */

import com.hospital.cvr.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface CvrService {

    /**
     * Create new CVR (Case Visit Record)
     * @param createCvrDTO - CVR creation details
     * @return Created CVR details
     */
    CvrDTO createCVR(CreateCvrDTO createCvrDTO);

    /**
     * Get CVR by CVR number
     * @param cvrNumber - CVR number
     * @return CVR details
     */
    CvrDTO getCVRByNumber(String cvrNumber);

    /**
     * Get CVR by ID
     * @param cvrId - CVR ID
     * @return CVR details
     */
    CvrDTO getCVRById(Long cvrId);

    /**
     * Get all CVRs for a patient
     * @param pinNumber - Patient PIN
     * @return List of CVRs
     */
    PatientVisitHistoryDTO getPatientVisitHistory(String pinNumber);

    /**
     * Get today's CVRs
     * @return List of today's CVRs
     */
    List<CvrSummaryDTO> getTodaysCVRs();

    /**
     * Get CVRs by date
     * @param date - Visit date
     * @return List of CVRs
     */
    List<CvrSummaryDTO> getCVRsByDate(LocalDate date);

    /**
     * Get CVRs by doctor and date
     * @param doctorId - Doctor ID
     * @param date - Visit date
     * @return List of CVRs
     */
    List<CvrSummaryDTO> getCVRsByDoctorAndDate(String doctorId, LocalDate date);

    /**
     * Update CVR status
     * @param cvrNumber - CVR number
     * @param status - New status
     * @return Updated CVR
     */
    CvrDTO updateCVRStatus(String cvrNumber, String status);

    /**
     * Check-in patient
     * @param cvrNumber - CVR number
     * @return Updated CVR
     */
    CvrDTO checkInPatient(String cvrNumber);

    /**
     * Start consultation
     * @param cvrNumber - CVR number
     * @return Updated CVR
     */
    CvrDTO startConsultation(String cvrNumber);

    /**
     * Complete consultation
     * @param cvrNumber - CVR number
     * @return Updated CVR
     */
    CvrDTO completeConsultation(String cvrNumber);

    /**
     * Cancel CVR
     * @param cvrNumber - CVR number
     * @param reason - Cancellation reason
     * @return Success message
     */
    String cancelCVR(String cvrNumber, String reason);

    /**
     * Record vitals for CVR
     * @param recordVitalsDTO - Vitals data
     * @return Recorded vitals
     */
    CvrVitalsDTO recordVitals(RecordVitalsDTO recordVitalsDTO);

    /**
     * Get vitals for CVR
     * @param cvrNumber - CVR number
     * @return List of vitals
     */
    List<CvrVitalsDTO> getVitalsByCVR(String cvrNumber);

    /**
     * Get recent CVRs
     * @param limit - Number of records
     * @return List of recent CVRs
     */
    List<CvrSummaryDTO> getRecentCVRs(int limit);

    /**
     * Search CVRs
     * @param searchTerm - Search term (CVR number or PIN)
     * @return List of matching CVRs
     */
    List<CvrSummaryDTO> searchCVRs(String searchTerm);

    /**
     * Count total visits for patient
     * @param pinNumber - Patient PIN
     * @return Visit count
     */
    long countPatientVisits(String pinNumber);

    /**
     * Check if CVR exists
     * @param cvrNumber - CVR number
     * @return true if exists
     */
    boolean existsByCVRNumber(String cvrNumber);

    /**
     * Assign doctor to CVR
     * @param cvrNumber - CVR number
     * @param doctorId - Doctor ID
     * @return Updated CVR
     */
    CvrDTO assignDoctor(String cvrNumber, String doctorId);
    
    /**
 * Get CVR by appointment ID
 */
CvrDTO getCVRByAppointmentId(String appointmentId);

//vitals delete by cvr no 

String deleteVitalsByCVR(String cvrNumber);

    /**
     * Get active OP cases for follow-up selection
     */
    List<OpCaseDTO> getActiveOpCases(String pinNumber, String doctorId);

}
