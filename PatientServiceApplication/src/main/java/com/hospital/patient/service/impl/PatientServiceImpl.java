/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.service.impl;

/**
 *
 * @author mduhijod
 */

import com.hospital.patient.dto.*;
import com.hospital.patient.entity.Patient;
import com.hospital.patient.entity.PatientMedicalHistory;
import com.hospital.patient.exception.PatientAlreadyExistsException;
import com.hospital.patient.exception.PatientNotFoundException;
import com.hospital.patient.repository.PatientRepository;
import com.hospital.patient.service.PatientService;
import com.hospital.patient.util.PinGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PinGenerator pinGenerator;
    private final ModelMapper modelMapper;

    @Override
    public PatientDTO registerPatient(PatientRegistrationDTO registrationDTO) {
        log.info("Registering new patient: {}", registrationDTO.getFirstName());

        // Check if contact number already exists
        if (patientRepository.existsByContactNumber(registrationDTO.getContactNumber())) {
            throw new PatientAlreadyExistsException(
                "Patient with contact number " + registrationDTO.getContactNumber() + " already exists"
            );
        }

        // Check if Aadhar already exists
        if (registrationDTO.getAadharNumber() != null && 
            patientRepository.existsByAadharNumber(registrationDTO.getAadharNumber())) {
            throw new PatientAlreadyExistsException(
                "Patient with Aadhar number " + registrationDTO.getAadharNumber() + " already exists"
            );
        }

        // Generate unique PIN
        String lastPIN = getLastPINFromDB();
        String newPIN = pinGenerator.generatePIN(lastPIN);
        log.info("Generated PIN: {}", newPIN);

        // Map DTO to Entity
        Patient patient = modelMapper.map(registrationDTO, Patient.class);
        patient.setPinNumber(newPIN);
        patient.setStatus(Patient.PatientStatus.ACTIVE);

        // Create empty medical history
        PatientMedicalHistory medicalHistory = new PatientMedicalHistory();
        medicalHistory.setPatient(patient);
        patient.setMedicalHistory(medicalHistory);

        // Save patient
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient registered successfully with PIN: {}", newPIN);

        return mapToDTO(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO getPatientByPIN(String pinNumber) {
        log.info("Fetching patient by PIN: {}", pinNumber);
        Patient patient = patientRepository.findByPinNumber(pinNumber)
            .orElseThrow(() -> new PatientNotFoundException("Patient not found with PIN: " + pinNumber));
        return mapToDTO(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long patientId) {
        log.info("Fetching patient by ID: {}", patientId);
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + patientId));
        return mapToDTO(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO searchByContactNumber(String contactNumber) {
        log.info("Searching patient by contact: {}", contactNumber);
        Patient patient = patientRepository.findByContactNumber(contactNumber)
            .orElseThrow(() -> new PatientNotFoundException("Patient not found with contact: " + contactNumber));
        return mapToDTO(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientSearchDTO> searchByName(String name) {
        log.info("Searching patients by name: {}", name);
        List<Patient> patients = patientRepository.searchByName(name);
        return patients.stream()
            .map(this::mapToSearchDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientSearchDTO> searchPatients(String searchTerm, String searchType) {
        log.info("Searching patients - Term: {}, Type: {}", searchTerm, searchType);
        
        List<Patient> patients;
        
        switch (searchType.toUpperCase()) {
            case "PIN":
                patients = patientRepository.findByPinNumber(searchTerm)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());
                        break;
            
            case "CONTACT":
                patients = patientRepository.findByContactNumber(searchTerm)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());
                        break;
            
            case "EMAIL":
                patients = patientRepository.searchPatients(null, null, searchTerm, null);
                break;
            
            case "AADHAR":
                patients = patientRepository.findByAadharNumber(searchTerm)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());
                        break;  
            
            case "NAME":
                patients = patientRepository.searchByName(searchTerm);
                break;
            
            default:
                // Search in all fields
                patients = patientRepository.searchByName(searchTerm);
                if (patients.isEmpty()) {
                    patients = patientRepository.searchPatients(
                        searchTerm, searchTerm, searchTerm, searchTerm
                    );
                }
        }
        
        return patients.stream()
            .map(this::mapToSearchDTO)
            .collect(Collectors.toList());
    }

    @Override
    public PatientDTO updatePatient(String pinNumber, PatientRegistrationDTO registrationDTO) {
        log.info("Updating patient with PIN: {}", pinNumber);
        
        Patient patient = patientRepository.findByPinNumber(pinNumber)
            .orElseThrow(() -> new PatientNotFoundException("Patient not found with PIN: " + pinNumber));

        // Update fields (excluding PIN)
        patient.setFirstName(registrationDTO.getFirstName());
        patient.setLastName(registrationDTO.getLastName());
        patient.setDateOfBirth(registrationDTO.getDateOfBirth());
        patient.setGender(Patient.Gender.valueOf(registrationDTO.getGender()));
        patient.setBloodGroup(registrationDTO.getBloodGroup());
        patient.setAlternateContact(registrationDTO.getAlternateContact());
        patient.setEmail(registrationDTO.getEmail());
        patient.setAddressLine1(registrationDTO.getAddressLine1());
        patient.setAddressLine2(registrationDTO.getAddressLine2());
        patient.setCity(registrationDTO.getCity());
        patient.setState(registrationDTO.getState());
        patient.setPincode(registrationDTO.getPincode());
        patient.setEmergencyContactName(registrationDTO.getEmergencyContactName());
        patient.setEmergencyContactNumber(registrationDTO.getEmergencyContactNumber());
        patient.setEmergencyContactRelation(registrationDTO.getEmergencyContactRelation());
        patient.setInsuranceProvider(registrationDTO.getInsuranceProvider());
        patient.setInsuranceId(registrationDTO.getInsuranceId());
        patient.setInsuranceExpiryDate(registrationDTO.getInsuranceExpiryDate());
        patient.setRemarks(registrationDTO.getRemarks());

        Patient updatedPatient = patientRepository.save(patient);
        log.info("Patient updated successfully: {}", pinNumber);
        
        return mapToDTO(updatedPatient);
    }

    // Helper methods
    private String getLastPINFromDB() {
        List<String> pins = patientRepository.findTopByOrderByPatientIdDesc();
        return pins.isEmpty() ? null : pins.get(0);
    }

    private PatientDTO mapToDTO(Patient patient) {
        PatientDTO dto = modelMapper.map(patient, PatientDTO.class);
        dto.setFullName(patient.getFullName());
        dto.setFullAddress(patient.getFullAddress());
        return dto;
    }

    private PatientSearchDTO mapToSearchDTO(Patient patient) {
        PatientSearchDTO dto = new PatientSearchDTO();
        dto.setPatientId(patient.getPatientId());
        dto.setPinNumber(patient.getPinNumber());
        dto.setFullName(patient.getFullName());
        dto.setAge(patient.getAge());
        dto.setGender(patient.getGender().name());
        dto.setContactNumber(patient.getContactNumber());
        dto.setEmail(patient.getEmail());
        dto.setRegistrationDate(patient.getRegistrationDate().toLocalDate());
        dto.setStatus(patient.getStatus().name());
        // TODO: Add visit count and last visit from CVR service
        dto.setTotalVisits(0);
        return dto;
    }

    // Remaining methods in Part 2...




    

    @Override
    public long getTotalActivePatients() {
        return patientRepository.countActivePatients();
    }

    @Override
    public boolean existsByPIN(String pinNumber) {
        return patientRepository.existsByPinNumber(pinNumber);
    }

    @Override
    public boolean existsByContactNumber(String contactNumber) {
        return patientRepository.existsByContactNumber(contactNumber);
    }
    
    
    // Add these methods to PatientServiceImpl.java

@Override
public PatientDTO updatePatientStatus(String pinNumber, String status) {
    log.info("Updating patient status - PIN: {}, Status: {}", pinNumber, status);
    
    Patient patient = patientRepository.findByPinNumber(pinNumber)
        .orElseThrow(() -> new PatientNotFoundException("Patient not found with PIN: " + pinNumber));
    
    patient.setStatus(Patient.PatientStatus.valueOf(status.toUpperCase()));
    Patient updatedPatient = patientRepository.save(patient);
    
    log.info("Patient status updated successfully");
    return mapToDTO(updatedPatient);
}

@Override
@Transactional(readOnly = true)
public List<PatientDTO> getAllActivePatients() {
    log.info("Fetching all active patients");
    List<Patient> patients = patientRepository.findAllActivePatients();
    
    return patients.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
}

@Override
@Transactional(readOnly = true)
public List<PatientDTO> getRecentPatients(int limit) {
    log.info("Fetching {} recent patients", limit);
    List<Patient> patients = patientRepository.findRecentPatients();
    
    return patients.stream()
        .limit(limit)
        .map(this::mapToDTO)
        .collect(Collectors.toList());
}

@Override
public MedicalHistoryDTO updateMedicalHistory(String pinNumber, MedicalHistoryDTO historyDTO) {
    log.info("Updating medical history for PIN: {}", pinNumber);
    
    Patient patient = patientRepository.findByPinNumber(pinNumber)
        .orElseThrow(() -> new PatientNotFoundException("Patient not found with PIN: " + pinNumber));
    
    PatientMedicalHistory history = patient.getMedicalHistory();
    if (history == null) {
        history = new PatientMedicalHistory();
        history.setPatient(patient);
        patient.setMedicalHistory(history);
    }
    
    // Update medical history fields
    history.setAllergies(historyDTO.getAllergies());
    history.setChronicDiseases(historyDTO.getChronicDiseases());
    history.setPastSurgeries(historyDTO.getPastSurgeries());
    history.setFamilyHistory(historyDTO.getFamilyHistory());
    
    if (historyDTO.getSmokingStatus() != null) {
        history.setSmokingStatus(
            PatientMedicalHistory.SmokingStatus.valueOf(historyDTO.getSmokingStatus().toUpperCase())
        );
    }
    
    if (historyDTO.getAlcoholConsumption() != null) {
        history.setAlcoholConsumption(
            PatientMedicalHistory.AlcoholConsumption.valueOf(historyDTO.getAlcoholConsumption().toUpperCase())
        );
    }
    
    history.setBloodPressure(historyDTO.getBloodPressure());
    
    if (historyDTO.getHeightCm() != null) {
        history.setHeightCm(BigDecimal.valueOf(historyDTO.getHeightCm()));
    }
    
    if (historyDTO.getWeightKg() != null) {
        history.setWeightKg(BigDecimal.valueOf(historyDTO.getWeightKg()));
    }
    
    patientRepository.save(patient);
    log.info("Medical history updated successfully");
    
    return mapToMedicalHistoryDTO(history);
}

@Override
@Transactional(readOnly = true)
public MedicalHistoryDTO getMedicalHistory(String pinNumber) {
    log.info("Fetching medical history for PIN: {}", pinNumber);
    
    Patient patient = patientRepository.findByPinNumber(pinNumber)
        .orElseThrow(() -> new PatientNotFoundException("Patient not found with PIN: " + pinNumber));
    
    PatientMedicalHistory history = patient.getMedicalHistory();
    if (history == null) {
        history = new PatientMedicalHistory();
        history.setPatient(patient);
    }
    
    return mapToMedicalHistoryDTO(history);
}

@Override
public String deletePatient(String pinNumber) {
    log.info("Deleting patient with PIN: {}", pinNumber);
    
    Patient patient = patientRepository.findByPinNumber(pinNumber)
        .orElseThrow(() -> new PatientNotFoundException("Patient not found with PIN: " + pinNumber));
    
    // Soft delete - mark as INACTIVE
    patient.setStatus(Patient.PatientStatus.INACTIVE);
    patientRepository.save(patient);
    
    log.info("Patient deleted (marked as INACTIVE) successfully");
    return "Patient deleted successfully";
}

// Helper method for mapping medical history
private MedicalHistoryDTO mapToMedicalHistoryDTO(PatientMedicalHistory history) {
    MedicalHistoryDTO dto = new MedicalHistoryDTO();
    dto.setHistoryId(history.getHistoryId());
    dto.setPatientId(history.getPatient().getPatientId());
    dto.setAllergies(history.getAllergies());
    dto.setChronicDiseases(history.getChronicDiseases());
    dto.setPastSurgeries(history.getPastSurgeries());
    dto.setFamilyHistory(history.getFamilyHistory());
    dto.setSmokingStatus(history.getSmokingStatus() != null ? history.getSmokingStatus().name() : null);
    dto.setAlcoholConsumption(history.getAlcoholConsumption() != null ? history.getAlcoholConsumption().name() : null);
    dto.setBloodPressure(history.getBloodPressure());
    dto.setHeightCm(history.getHeightCm() != null ? history.getHeightCm().doubleValue() : null);
    dto.setWeightKg(history.getWeightKg() != null ? history.getWeightKg().doubleValue() : null);
    dto.setBmi(history.getBmi() != null ? history.getBmi().doubleValue() : null);
    dto.setLastUpdated(history.getLastUpdated());
    return dto;
}
}
