/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.repository;

/**
 *
 * @author mduhijod
 */

import com.hospital.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find patient by PIN number
     */
    Optional<Patient> findByPinNumber(String pinNumber);

    /**
     * Check if PIN exists
     */
    boolean existsByPinNumber(String pinNumber);

    /**
     * Find patient by contact number
     */
    Optional<Patient> findByContactNumber(String contactNumber);

    /**
     * Find patient by Aadhar number
     */
    Optional<Patient> findByAadharNumber(String aadharNumber);

    /**
     * Check if contact number exists
     */
    boolean existsByContactNumber(String contactNumber);

    /**
     * Check if Aadhar number exists
     */
    boolean existsByAadharNumber(String aadharNumber);

    /**
     * Get last generated PIN number
     */
    @Query("SELECT p.pinNumber FROM Patient p ORDER BY p.patientId DESC")
    List<String> findTopByOrderByPatientIdDesc();

    /**
     * Search patients by name (first name or last name)
     */
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Patient> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Search patients by multiple criteria
     */
    @Query("SELECT p FROM Patient p WHERE " +
           "(:pinNumber IS NULL OR p.pinNumber = :pinNumber) AND " +
           "(:contactNumber IS NULL OR p.contactNumber = :contactNumber) AND " +
           "(:email IS NULL OR LOWER(p.email) = LOWER(:email)) AND " +
           "(:aadharNumber IS NULL OR p.aadharNumber = :aadharNumber)")
    List<Patient> searchPatients(
        @Param("pinNumber") String pinNumber,
        @Param("contactNumber") String contactNumber,
        @Param("email") String email,
        @Param("aadharNumber") String aadharNumber
    );

    /**
     * Get all active patients
     */
    @Query("SELECT p FROM Patient p WHERE p.status = 'ACTIVE' ORDER BY p.registrationDate DESC")
    List<Patient> findAllActivePatients();

    /**
     * Get recently registered patients
     */
    @Query("SELECT p FROM Patient p ORDER BY p.registrationDate DESC")
    List<Patient> findRecentPatients();

    /**
     * Count total patients
     */
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.status = 'ACTIVE'")
    long countActivePatients();

    /**
     * Search patients by city
     */
    List<Patient> findByCityContainingIgnoreCase(String city);

    /**
     * Search patients by state
     */
    List<Patient> findByStateContainingIgnoreCase(String state);
}
