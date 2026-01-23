/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.service;

/**
 *
 * @author mduhijod
 */

import com.hospital.doctor.dto.*;

import java.util.List;

public interface DoctorService {

    /**
     * Register new doctor
     * @param registrationDTO - Doctor registration details
     * @return Created doctor details
     */
    DoctorDTO registerDoctor(DoctorRegistrationDTO registrationDTO);

    /**
     * Get doctor by doctor ID
     * @param doctorId - Doctor ID
     * @return Doctor details
     */
    DoctorDTO getDoctorById(String doctorId);

    /**
     * Get doctor by database ID
     * @param id - Database ID
     * @return Doctor details
     */
    DoctorDTO getDoctorByDbId(Long id);

    /**
     * Update doctor details
     * @param doctorId - Doctor ID
     * @param registrationDTO - Updated details
     * @return Updated doctor
     */
    DoctorDTO updateDoctor(String doctorId, DoctorRegistrationDTO registrationDTO);

    /**
     * Update doctor status
     * @param doctorId - Doctor ID
     * @param status - New status
     * @return Updated doctor
     */
    DoctorDTO updateDoctorStatus(String doctorId, String status);

    /**
     * Mark doctor as on leave
     * @param doctorId - Doctor ID
     * @return Updated doctor
     */
    DoctorDTO markOnLeave(String doctorId);

    /**
     * Mark doctor as available
     * @param doctorId - Doctor ID
     * @return Updated doctor
     */
    DoctorDTO markAvailable(String doctorId);

    /**
     * Get all available doctors
     * @return List of available doctors
     */
    List<DoctorSummaryDTO> getAllAvailableDoctors();

    /**
     * Get available doctors by specialization
     * @param specialization - Specialization name
     * @return List of doctors
     */
    List<DoctorSummaryDTO> getAvailableDoctorsBySpecialization(String specialization);

    /**
     * Get available doctors by department
     * @param department - Department name
     * @return List of doctors
     */
    List<DoctorSummaryDTO> getAvailableDoctorsByDepartment(String department);

    /**
     * Get all doctors by specialization
     * @param specialization - Specialization
     * @return List of doctors
     */
    List<DoctorDTO> getDoctorsBySpecialization(String specialization);

    /**
     * Get all doctors by department
     * @param department - Department
     * @return List of doctors
     */
    List<DoctorDTO> getDoctorsByDepartment(String department);

    /**
     * Search doctors by name
     * @param name - Search term
     * @return List of matching doctors
     */
    List<DoctorSearchDTO> searchDoctorsByName(String name);

    /**
     * Get all active doctors
     * @return List of active doctors
     */
    List<DoctorDTO> getAllActiveDoctors();

    /**
     * Get doctors by status
     * @param status - Doctor status
     * @return List of doctors
     */
    List<DoctorDTO> getDoctorsByStatus(String status);

    /**
     * Get emergency doctors
     * @return List of doctors available for emergency
     */
    List<DoctorSummaryDTO> getEmergencyDoctors();

    /**
     * Delete doctor (soft delete - mark as inactive)
     * @param doctorId - Doctor ID
     * @return Success message
     */
    String deleteDoctor(String doctorId);

    /**
     * Count total active doctors
     * @return Count
     */
    long getTotalActiveDoctors();

    /**
     * Check if doctor exists
     * @param doctorId - Doctor ID
     * @return true if exists
     */
    boolean existsByDoctorId(String doctorId);

    /**
     * Get all specializations
     * @return List of unique specializations
     */
    List<String> getAllSpecializations();

    /**
     * Get all departments
     * @return List of unique departments
     */
    List<String> getAllDepartments();
}
