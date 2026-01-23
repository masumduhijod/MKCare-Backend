/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.service;

/**
 *
 * @author mduhijod
 */

import com.hospital.patient.dto.*;
import com.hospital.patient.entity.Patient;

import java.util.List;

public interface PatientService {

    /**
     * Register new patient
     * @param registrationDTO - Patient registration details
     * @return PatientDTO with generated PIN
     */
    PatientDTO registerPatient(PatientRegistrationDTO registrationDTO);

    /**
     * Get patient by PIN number
     * @param pinNumber - Patient PIN
     * @return PatientDTO
     */
    PatientDTO getPatientByPIN(String pinNumber);

    /**
     * Get patient by ID
     * @param patientId - Patient ID
     * @return PatientDTO
     */
    PatientDTO getPatientById(Long patientId);

    /**
     * Search patient by contact number
     * @param contactNumber - 10-digit mobile number
     * @return PatientDTO
     */
    PatientDTO searchByContactNumber(String contactNumber);

    /**
     * Search patients by name
     * @param name - First name or last name
     * @return List of PatientSearchDTO
     */
    List<PatientSearchDTO> searchByName(String name);

    /**
     * Search patients by multiple criteria
     * @param searchTerm - Search term (PIN/Contact/Email/Aadhar)
     * @param searchType - Type of search (PIN, CONTACT, EMAIL, AADHAR, NAME)
     * @return List of PatientSearchDTO
     */
    List<PatientSearchDTO> searchPatients(String searchTerm, String searchType);

    /**
     * Update patient details
     * @param pinNumber - Patient PIN
     * @param registrationDTO - Updated details
     * @return Updated PatientDTO
     */
    PatientDTO updatePatient(String pinNumber, PatientRegistrationDTO registrationDTO);

    /**
     * Update patient status
     * @param pinNumber - Patient PIN
     * @param status - New status (ACTIVE/INACTIVE/DECEASED)
     * @return Updated PatientDTO
     */
    PatientDTO updatePatientStatus(String pinNumber, String status);

    /**
     * Get all active patients
     * @return List of PatientDTO
     */
    List<PatientDTO> getAllActivePatients();

    /**
     * Get recently registered patients
     * @param limit - Number of records
     * @return List of PatientDTO
     */
    List<PatientDTO> getRecentPatients(int limit);

    /**
     * Update patient medical history
     * @param pinNumber - Patient PIN
     * @param historyDTO - Medical history details
     * @return Updated MedicalHistoryDTO
     */
    MedicalHistoryDTO updateMedicalHistory(String pinNumber, MedicalHistoryDTO historyDTO);

    /**
     * Get patient medical history
     * @param pinNumber - Patient PIN
     * @return MedicalHistoryDTO
     */
    MedicalHistoryDTO getMedicalHistory(String pinNumber);

    /**
     * Delete patient (soft delete - mark as INACTIVE)
     * @param pinNumber - Patient PIN
     * @return Success message
     */
    String deletePatient(String pinNumber);

    /**
     * Get total active patients count
     * @return Count
     */
    long getTotalActivePatients();

    /**
     * Check if patient exists by PIN
     * @param pinNumber - Patient PIN
     * @return true if exists
     */
    boolean existsByPIN(String pinNumber);

    /**
     * Check if contact number already registered
     * @param contactNumber - Contact number
     * @return true if exists
     */
    boolean existsByContactNumber(String contactNumber);
}
