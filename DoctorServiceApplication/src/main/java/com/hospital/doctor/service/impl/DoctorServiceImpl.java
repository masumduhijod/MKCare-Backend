/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.service.impl;

/**
 *
 * @author mduhijod
 */

import com.hospital.doctor.dto.*;
import com.hospital.doctor.entity.Doctor;
import com.hospital.doctor.exception.DoctorAlreadyExistsException;
import com.hospital.doctor.exception.DoctorNotFoundException;
import com.hospital.doctor.repository.DoctorRepository;
import com.hospital.doctor.service.DoctorService;
import com.hospital.doctor.util.DoctorIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorIdGenerator doctorIdGenerator;
    private final ModelMapper modelMapper;

    @Override
    public DoctorDTO registerDoctor(DoctorRegistrationDTO registrationDTO) {
        log.info("Registering new doctor: {}", registrationDTO.getFirstName());

        // Check if license number already exists
        if (registrationDTO.getLicenseNumber() != null &&
            doctorRepository.existsByLicenseNumber(registrationDTO.getLicenseNumber())) {
            throw new DoctorAlreadyExistsException(
                "Doctor with license number " + registrationDTO.getLicenseNumber() + " already exists"
            );
        }

        // Generate unique Doctor ID
        String lastDoctorId = getLastDoctorIdFromDB();
        String newDoctorId = doctorIdGenerator.generateDoctorId(lastDoctorId);
        log.info("Generated Doctor ID: {}", newDoctorId);

        // Map DTO to Entity
        Doctor doctor = modelMapper.map(registrationDTO, Doctor.class);
        doctor.setDoctorId(newDoctorId);
        doctor.setStatus(Doctor.DoctorStatus.AVAILABLE);

        // Save doctor
        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor registered successfully: {}", newDoctorId);

        return mapToDTO(savedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDTO getDoctorById(String doctorId) {
        log.info("Fetching doctor by ID: {}", doctorId);
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));
        return mapToDTO(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDTO getDoctorByDbId(Long id) {
        log.info("Fetching doctor by DB ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + id));
        return mapToDTO(doctor);
    }

    @Override
    public DoctorDTO updateDoctor(String doctorId, DoctorRegistrationDTO registrationDTO) {
        log.info("Updating doctor: {}", doctorId);

        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        // Update fields
        doctor.setFirstName(registrationDTO.getFirstName());
        doctor.setLastName(registrationDTO.getLastName());
        doctor.setSpecialization(registrationDTO.getSpecialization());
        doctor.setQualification(registrationDTO.getQualification());
        doctor.setExperienceYears(registrationDTO.getExperienceYears());
        doctor.setDepartment(registrationDTO.getDepartment());
        doctor.setContactNumber(registrationDTO.getContactNumber());
        doctor.setEmail(registrationDTO.getEmail());
        doctor.setConsultationFee(registrationDTO.getConsultationFee());
        doctor.setFollowUpFee(registrationDTO.getFollowUpFee());
        doctor.setAvailableForOPD(registrationDTO.getAvailableForOPD());
        doctor.setAvailableForEmergency(registrationDTO.getAvailableForEmergency());
        doctor.setBio(registrationDTO.getBio());
        doctor.setLanguagesSpoken(registrationDTO.getLanguagesSpoken());
        doctor.setRoomNumber(registrationDTO.getRoomNumber());

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor updated successfully: {}", doctorId);

        return mapToDTO(updatedDoctor);
    }

    @Override
    public DoctorDTO updateDoctorStatus(String doctorId, String status) {
        log.info("Updating doctor status - ID: {}, Status: {}", doctorId, status);

        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        doctor.setStatus(Doctor.DoctorStatus.valueOf(status.toUpperCase()));
        
        // Update OPD availability based on status
        if (doctor.getStatus() == Doctor.DoctorStatus.AVAILABLE) {
            doctor.setAvailableForOPD(true);
        } else {
            doctor.setAvailableForOPD(false);
        }

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor status updated successfully");

        return mapToDTO(updatedDoctor);
    }

    @Override
    public DoctorDTO markOnLeave(String doctorId) {
        log.info("Marking doctor on leave: {}", doctorId);

        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        doctor.markOnLeave();
        Doctor updatedDoctor = doctorRepository.save(doctor);

        log.info("Doctor marked as on leave");
        return mapToDTO(updatedDoctor);
    }

    @Override
    public DoctorDTO markAvailable(String doctorId) {
        log.info("Marking doctor as available: {}", doctorId);

        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        doctor.markAvailable();
        Doctor updatedDoctor = doctorRepository.save(doctor);

        log.info("Doctor marked as available");
        return mapToDTO(updatedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorSummaryDTO> getAllAvailableDoctors() {
        log.info("Fetching all available doctors");
        List<Doctor> doctors = doctorRepository.findAvailableDoctors();
        return doctors.stream()
            .map(this::mapToSummaryDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorSummaryDTO> getAvailableDoctorsBySpecialization(String specialization) {
        log.info("Fetching available doctors by specialization: {}", specialization);
        List<Doctor> doctors = doctorRepository.findAvailableDoctorsBySpecialization(specialization);
        return doctors.stream()
            .map(this::mapToSummaryDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorSummaryDTO> getAvailableDoctorsByDepartment(String department) {
        log.info("Fetching available doctors by department: {}", department);
        List<Doctor> doctors = doctorRepository.findAvailableDoctorsByDepartment(department);
        return doctors.stream()
            .map(this::mapToSummaryDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        log.info("Fetching doctors by specialization: {}", specialization);
        List<Doctor> doctors = doctorRepository.findBySpecializationIgnoreCase(specialization);
        return doctors.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsByDepartment(String department) {
        log.info("Fetching doctors by department: {}", department);
        List<Doctor> doctors = doctorRepository.findByDepartmentIgnoreCase(department);
        return doctors.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorSearchDTO> searchDoctorsByName(String name) {
        log.info("Searching doctors by name: {}", name);
        List<Doctor> doctors = doctorRepository.searchByName(name);
        return doctors.stream()
            .map(this::mapToSearchDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> getAllActiveDoctors() {
        log.info("Fetching all active doctors");
        List<Doctor> doctors = doctorRepository.findAllActiveDoctors();
        return doctors.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsByStatus(String status) {
        log.info("Fetching doctors by status: {}", status);
        Doctor.DoctorStatus doctorStatus = Doctor.DoctorStatus.valueOf(status.toUpperCase());
        List<Doctor> doctors = doctorRepository.findByStatus(doctorStatus);
        return doctors.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorSummaryDTO> getEmergencyDoctors() {
        log.info("Fetching emergency doctors");
        List<Doctor> doctors = doctorRepository.findEmergencyDoctors();
        return doctors.stream()
            .map(this::mapToSummaryDTO)
            .collect(Collectors.toList());
    }

    @Override
    public String deleteDoctor(String doctorId) {
        log.info("Deleting doctor: {}", doctorId);

        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        // Soft delete - mark as INACTIVE
        doctor.setStatus(Doctor.DoctorStatus.INACTIVE);
        doctor.setAvailableForOPD(false);
        doctor.setAvailableForEmergency(false);
        doctorRepository.save(doctor);

        log.info("Doctor marked as inactive");
        return "Doctor deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalActiveDoctors() {
        return doctorRepository.countActiveDoctors();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDoctorId(String doctorId) {
        return doctorRepository.existsByDoctorId(doctorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllSpecializations() {
        return doctorRepository.findAll().stream()
            .map(Doctor::getSpecialization)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDepartments() {
        return doctorRepository.findAll().stream()
            .map(Doctor::getDepartment)
            .filter(dept -> dept != null && !dept.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    // Helper methods
    private String getLastDoctorIdFromDB() {
        List<String> doctorIds = doctorRepository.findTopByOrderByIdDesc();
        return doctorIds.isEmpty() ? null : doctorIds.get(0);
    }

    private DoctorDTO mapToDTO(Doctor doctor) {
        DoctorDTO dto = modelMapper.map(doctor, DoctorDTO.class);
        dto.setFullName(doctor.getFullName());
        // Schedules will be mapped separately if needed
        return dto;
    }

    private DoctorSummaryDTO mapToSummaryDTO(Doctor doctor) {
        DoctorSummaryDTO dto = new DoctorSummaryDTO();
        dto.setDoctorId(doctor.getDoctorId());
        dto.setFullName(doctor.getFullName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setDepartment(doctor.getDepartment());
        dto.setExperienceYears(doctor.getExperienceYears());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setStatus(doctor.getStatus().name());
        dto.setAvailableForOPD(doctor.getAvailableForOPD());
        return dto;
    }

    private DoctorSearchDTO mapToSearchDTO(Doctor doctor) {
        DoctorSearchDTO dto = new DoctorSearchDTO();
        dto.setDoctorId(doctor.getDoctorId());
        dto.setFullName(doctor.getFullName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setDepartment(doctor.getDepartment());
        dto.setContactNumber(doctor.getContactNumber());
        dto.setStatus(doctor.getStatus().name());
        return dto;
    }
}
