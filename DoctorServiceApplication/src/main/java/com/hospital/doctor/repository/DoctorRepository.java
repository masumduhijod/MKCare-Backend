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

import com.hospital.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find doctor by doctor ID
     */
    Optional<Doctor> findByDoctorId(String doctorId);

    /**
     * Check if doctor exists by doctor ID
     */
    boolean existsByDoctorId(String doctorId);

    /**
     * Check if license number exists
     */
    boolean existsByLicenseNumber(String licenseNumber);

    /**
     * Find doctor by contact number
     */
    Optional<Doctor> findByContactNumber(String contactNumber);

    /**
     * Get last doctor ID for ID generation
     */
    @Query("SELECT d.doctorId FROM Doctor d ORDER BY d.id DESC")
    List<String> findTopByOrderByIdDesc();

    /**
     * Find doctors by specialization
     */
    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    /**
     * Find doctors by department
     */
    List<Doctor> findByDepartmentIgnoreCase(String department);

    /**
     * Find available doctors
     */
    @Query("SELECT d FROM Doctor d WHERE d.status = 'AVAILABLE' AND d.availableForOPD = true " +
           "ORDER BY d.firstName")
    List<Doctor> findAvailableDoctors();

    /**
     * Find available doctors by specialization
     */
    @Query("SELECT d FROM Doctor d WHERE d.status = 'AVAILABLE' AND d.availableForOPD = true " +
           "AND LOWER(d.specialization) = LOWER(:specialization)")
    List<Doctor> findAvailableDoctorsBySpecialization(@Param("specialization") String specialization);

    /**
     * Find available doctors by department
     */
    @Query("SELECT d FROM Doctor d WHERE d.status = 'AVAILABLE' AND d.availableForOPD = true " +
           "AND LOWER(d.department) = LOWER(:department)")
    List<Doctor> findAvailableDoctorsByDepartment(@Param("department") String department);

    /**
     * Search doctors by name
     */
    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Doctor> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Get all active doctors
     */
    @Query("SELECT d FROM Doctor d WHERE d.status != 'INACTIVE' ORDER BY d.firstName")
    List<Doctor> findAllActiveDoctors();

    /**
     * Count total active doctors
     */
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.status != 'INACTIVE'")
    long countActiveDoctors();

    /**
     * Get doctors by status
     */
    List<Doctor> findByStatus(Doctor.DoctorStatus status);

    /**
     * Find doctors available for emergency
     */
    @Query("SELECT d FROM Doctor d WHERE d.availableForEmergency = true " +
           "AND d.status = 'AVAILABLE'")
    List<Doctor> findEmergencyDoctors();
}
