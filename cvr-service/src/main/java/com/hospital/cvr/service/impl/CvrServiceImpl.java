/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.service.impl;

/**
 *
 * @author mduhijod
 */

import com.hospital.cvr.client.PatientServiceClient;
import com.hospital.cvr.dto.*;
import com.hospital.cvr.entity.CaseVisitRecord;
import com.hospital.cvr.entity.CvrVitals;
import com.hospital.cvr.exception.CvrNotFoundException;
import com.hospital.cvr.exception.InvalidCvrStatusException;
import com.hospital.cvr.exception.PatientNotFoundException;
import com.hospital.cvr.repository.CvrRepository;
import com.hospital.cvr.repository.CvrVitalsRepository;
import com.hospital.cvr.service.CvrService;
import com.hospital.cvr.util.CvrNumberGenerator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CvrServiceImpl implements CvrService {

    private final CvrRepository cvrRepository;
    private final CvrVitalsRepository cvrVitalsRepository;
    private final PatientServiceClient patientServiceClient;
    private final CvrNumberGenerator cvrNumberGenerator;
    private final ModelMapper modelMapper;

//    @Override
//    public CvrDTO createCVR(CreateCvrDTO createCvrDTO) {
//        log.info("Creating new CVR for PIN: {}", createCvrDTO.getPinNumber());
//
//        // Verify patient exists using Feign Client
//        ApiResponse<Boolean> patientResponse;
//        try {
//            patientResponse = patientServiceClient
//                .checkPINExists(createCvrDTO.getPinNumber());
//            
//            if (!patientResponse.isSuccess() || 
//                !(Boolean) patientResponse.getData()) {
//                throw new PatientNotFoundException(
//                    "Patient not found with PIN: " + createCvrDTO.getPinNumber()
//                );
//            }
//        } catch (FeignException e) {
//            log.error("Error calling Patient Service: {}", e.getMessage());
//            throw new PatientNotFoundException(
//                "Unable to verify patient. Patient Service unavailable."
//            );
//        }
//
//        // Get patient details
//        Object patientData = patientServiceClient
//            .getPatientByPIN(createCvrDTO.getPinNumber()).getData();
//
//        // Generate CVR number
//        LocalDate visitDate = createCvrDTO.getVisitDate() != null 
//            ? createCvrDTO.getVisitDate() 
//            : LocalDate.now();
//        
//        String lastCvr = getLastCVRFromDB();
//        String newCvrNumber = cvrNumberGenerator.generateCVR(lastCvr, visitDate);
//        log.info("Generated CVR: {}", newCvrNumber);
//
//        // Create CVR entity
//        CaseVisitRecord cvr = new CaseVisitRecord();
//        cvr.setCvrNumber(newCvrNumber);
//        cvr.setPinNumber(createCvrDTO.getPinNumber());
//        cvr.setVisitDate(visitDate);
//        cvr.setVisitTime(createCvrDTO.getVisitTime() != null 
//            ? createCvrDTO.getVisitTime() 
//            : java.time.LocalTime.now());
//        cvr.setVisitType(CaseVisitRecord.VisitType.valueOf(
//            createCvrDTO.getVisitType().toUpperCase()
//        ));
//        cvr.setChiefComplaint(createCvrDTO.getChiefComplaint());
//        cvr.setSymptoms(createCvrDTO.getSymptoms());
//        cvr.setDepartment(createCvrDTO.getDepartment());
//        cvr.setDoctorId(createCvrDTO.getDoctorId());
//        cvr.setCreatedBy(createCvrDTO.getCreatedBy());
//        cvr.setStatus(CaseVisitRecord.CvrStatus.REGISTERED);
//
//        // Extract patient ID from patient data
//        // Note: This is simplified - in production, properly deserialize
//        cvr.setPatientId(1L); // TODO: Extract from patientData
//
//        // Save CVR
//        CaseVisitRecord savedCvr = cvrRepository.save(cvr);
//        log.info("CVR created successfully: {}", newCvrNumber);
//
//        return mapToDTO(savedCvr, patientData);
//    }

    
    @Override
    public CvrDTO createCVR(CreateCvrDTO createCvrDTO) {
        log.info("========== CREATING PRE-VISIT CVR ==========");
        log.info("PIN: {}, Type: {}", createCvrDTO.getPinNumber(), createCvrDTO.getVisitType());
        
        // Verify patient exists
        verifyPatient(createCvrDTO.getPinNumber());

        // Get patient details
        Object patientData = getPatientDetails(createCvrDTO.getPinNumber());

        // Generate CVR number (using today's date for CVR generation)
        LocalDate generationDate = LocalDate.now();
        String lastCvr = getLastCVRFromDB();
        String newCvrNumber = cvrNumberGenerator.generateCVR(lastCvr, generationDate);
        log.info("Generated CVR: {}", newCvrNumber);

        // Create CVR entity
        CaseVisitRecord cvr = new CaseVisitRecord();
        cvr.setCvrNumber(newCvrNumber);
        cvr.setPinNumber(createCvrDTO.getPinNumber());
        cvr.setPatientId(1L); // TODO: Extract from patientData
        
        // *** STORE APPOINTMENT DETAILS (if provided) ***
        cvr.setAppointmentId(createCvrDTO.getAppointmentId());
        cvr.setAppointmentDate(createCvrDTO.getAppointmentDate());
        cvr.setAppointmentTime(createCvrDTO.getAppointmentTime());
        
        // *** DO NOT SET VISIT DATE/TIME - Keep NULL ***
        cvr.setVisitDate(null);
        cvr.setVisitTime(null);
        
        cvr.setVisitType(CaseVisitRecord.VisitType.valueOf(
            createCvrDTO.getVisitType().toUpperCase()
        ));
        cvr.setChiefComplaint(createCvrDTO.getChiefComplaint());
        cvr.setSymptoms(createCvrDTO.getSymptoms());
        cvr.setDepartment(createCvrDTO.getDepartment());
        cvr.setDoctorId(createCvrDTO.getDoctorId());
        cvr.setCreatedBy(createCvrDTO.getCreatedBy());
        
        // Set status based on whether appointment exists
        if (createCvrDTO.getAppointmentId() != null) {
            cvr.setStatus(CaseVisitRecord.CvrStatus.APPOINTMENT_SCHEDULED);
            log.info("Status: APPOINTMENT_SCHEDULED (awaiting patient check-in)");
        } else {
            cvr.setStatus(CaseVisitRecord.CvrStatus.REGISTERED);
            log.info("Status: REGISTERED (walk-in patient)");
        }

        // Save CVR
        CaseVisitRecord savedCvr = cvrRepository.save(cvr);
        
        log.info("========== CVR CREATED (Pre-Visit) ==========");
        log.info("CVR Number: {}", savedCvr.getCvrNumber());
        log.info("Appointment Date: {}", savedCvr.getAppointmentDate());
        log.info("Visit Date: {} (NULL = patient not arrived yet)", savedCvr.getVisitDate());
        log.info("Status: {}", savedCvr.getStatus());

        return mapToDTO(savedCvr, patientData);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public CvrDTO getCVRByNumber(String cvrNumber) {
//        log.info("Fetching CVR: {}", cvrNumber);
//        
//        CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
//            .orElseThrow(() -> new CvrNotFoundException(
//                "CVR not found: " + cvrNumber
//            ));
//
//        // Get patient details
//        Object patientData = getPatientDetails(cvr.getPinNumber());
//
//        return mapToDTO(cvr, patientData);
//    }

    @Override
    @Transactional(readOnly = true)
    public CvrDTO getCVRById(Long cvrId) {
        log.info("Fetching CVR by ID: {}", cvrId);
        
        CaseVisitRecord cvr = cvrRepository.findById(cvrId)
            .orElseThrow(() -> new CvrNotFoundException(
                "CVR not found with ID: " + cvrId
            ));

        Object patientData = getPatientDetails(cvr.getPinNumber());
        return mapToDTO(cvr, patientData);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientVisitHistoryDTO getPatientVisitHistory(String pinNumber) {
        log.info("Fetching visit history for PIN: {}", pinNumber);

        // Get all CVRs for patient
        List<CaseVisitRecord> cvrs = cvrRepository
            .findByPinNumberOrderByVisitDateDesc(pinNumber);

        if (cvrs.isEmpty()) {
            throw new CvrNotFoundException(
                "No visits found for patient: " + pinNumber
            );
        }

        // Get patient details
        Object patientData = getPatientDetails(pinNumber);

        PatientVisitHistoryDTO history = new PatientVisitHistoryDTO();
        history.setPinNumber(pinNumber);
        // history.setPatientName(patientData.getFullName()); // TODO
        history.setTotalVisits(cvrs.size());
        
        CaseVisitRecord lastVisit = cvrs.get(0);
        history.setLastVisitDate(lastVisit.getVisitDate());
        history.setLastCvrNumber(lastVisit.getCvrNumber());

        List<CvrSummaryDTO> recentVisits = cvrs.stream()
            .limit(10)
            .map(this::mapToSummaryDTO)
            .collect(Collectors.toList());
        history.setRecentVisits(recentVisits);

        return history;
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<CvrSummaryDTO> getTodaysCVRs() {
//        log.info("Fetching today's CVRs");
//        
//        List<CaseVisitRecord> cvrs = cvrRepository
//            .findTodaysCVRs(LocalDate.now());
//
//        return cvrs.stream()
//            .map(this::mapToSummaryDTO)
//            .collect(Collectors.toList());
//    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<CvrSummaryDTO> getCVRsByDate(LocalDate date) {
//        log.info("Fetching CVRs for date: {}", date);
//        
//        List<CaseVisitRecord> cvrs = cvrRepository
//            .findByVisitDateOrderByVisitTimeAsc(date);
//
//        return cvrs.stream()
//            .map(this::mapToSummaryDTO)
//            .collect(Collectors.toList());
//    }

    @Override
    @Transactional(readOnly = true)
    public List<CvrSummaryDTO> getCVRsByDoctorAndDate(String doctorId, LocalDate date) {
        log.info("Fetching CVRs for doctor: {} on date: {}", doctorId, date);
        
        List<CaseVisitRecord> cvrs = cvrRepository
            .findByVisitDateAndDoctorIdOrderByVisitTimeAsc(date, doctorId);

        return cvrs.stream()
            .map(this::mapToSummaryDTO)
            .collect(Collectors.toList());
    }

//    @Override
//    public CvrDTO checkInPatient(String cvrNumber) {
//        log.info("Checking in patient - CVR: {}", cvrNumber);
//        
//        CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
//            .orElseThrow(() -> new CvrNotFoundException(
//                "CVR not found: " + cvrNumber
//            ));
//
//        cvr.checkIn();
//        CaseVisitRecord updatedCvr = cvrRepository.save(cvr);
//        
//        log.info("Patient checked in successfully");
//        Object patientData = getPatientDetails(cvr.getPinNumber());
//        return mapToDTO(updatedCvr, patientData);
//    }

    // Helper methods
//    private String getLastCVRFromDB() {
//        List<String> cvrs = cvrRepository.findTopByOrderByCvrIdDesc();
//        return cvrs.isEmpty() ? null : cvrs.get(0);
//    }

//    private Object getPatientDetails(String pinNumber) {
//        try {
//            return patientServiceClient.getPatientByPIN(pinNumber).getData();
//        } catch (FeignException e) {
//            log.warn("Could not fetch patient details: {}", e.getMessage());
//            return null;
//        }
//    }

//    private CvrDTO mapToDTO(CaseVisitRecord cvr, Object patientData) {
//        CvrDTO dto = modelMapper.map(cvr, CvrDTO.class);
//        
//        // Map patient details if available
//        if (patientData != null) {
//            // TODO: Properly map patient data
//            dto.setPatientName("Patient Name"); // Temporary
//        }
//        
//        // Map vitals
//        List<CvrVitalsDTO> vitalsDTO = cvr.getVitals().stream()
//            .map(v -> modelMapper.map(v, CvrVitalsDTO.class))
//            .collect(Collectors.toList());
//        dto.setVitals(vitalsDTO);
//        
//        return dto;
//    }

//    private CvrSummaryDTO mapToSummaryDTO(CaseVisitRecord cvr) {
//        CvrSummaryDTO dto = new CvrSummaryDTO();
//        dto.setCvrNumber(cvr.getCvrNumber());
//        dto.setPinNumber(cvr.getPinNumber());
//        dto.setVisitDate(cvr.getVisitDate());
//        dto.setVisitTime(cvr.getVisitTime());
//        dto.setVisitType(cvr.getVisitType().name());
//        dto.setChiefComplaint(cvr.getChiefComplaint());
//        dto.setStatus(cvr.getStatus().name());
//        // TODO: Get patient and doctor names
//        return dto;
//    }


//    @Override
//    public long countPatientVisits(String pinNumber) {
//        return cvrRepository.countByPinNumber(pinNumber);
//    }
//
//    @Override
//    public boolean existsByCVRNumber(String cvrNumber) {
//        return cvrRepository.existsByCvrNumber(cvrNumber);
//    }

    
    
    
    // Add these methods to CvrServiceImpl.java

@Override
public CvrDTO updateCVRStatus(String cvrNumber, String status) {
    log.info("Updating CVR status - CVR: {}, Status: {}", cvrNumber, status);
    
    CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
        .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));
    
    cvr.setStatus(CaseVisitRecord.CvrStatus.valueOf(status.toUpperCase()));
    CaseVisitRecord updatedCvr = cvrRepository.save(cvr);
    
    log.info("CVR status updated successfully");
    Object patientData = getPatientDetails(cvr.getPinNumber());
    return mapToDTO(updatedCvr, patientData);
}

//@Override
//public CvrDTO startConsultation(String cvrNumber) {
//    log.info("Starting consultation - CVR: {}", cvrNumber);
//    
//    CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
//        .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));
//    
//    cvr.startConsultation();
//    CaseVisitRecord updatedCvr = cvrRepository.save(cvr);
//    
//    log.info("Consultation started successfully");
//    Object patientData = getPatientDetails(cvr.getPinNumber());
//    return mapToDTO(updatedCvr, patientData);
//}

//@Override
//public CvrDTO completeConsultation(String cvrNumber) {
//    log.info("Completing consultation - CVR: {}", cvrNumber);
//    
//    CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
//        .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));
//    
//    cvr.completeConsultation();
//    CaseVisitRecord updatedCvr = cvrRepository.save(cvr);
//    
//    log.info("Consultation completed successfully");
//    Object patientData = getPatientDetails(cvr.getPinNumber());
//    return mapToDTO(updatedCvr, patientData);
//}

@Override
public String cancelCVR(String cvrNumber, String reason) {
    log.info("Cancelling CVR: {} - Reason: {}", cvrNumber, reason);
    
    CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
        .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));
    
    cvr.cancel();
    cvrRepository.save(cvr);
    
    log.info("CVR cancelled successfully");
    return "CVR cancelled successfully: " + cvrNumber;
}

@Override
public CvrVitalsDTO recordVitals(RecordVitalsDTO recordVitalsDTO) {
    log.info("Recording vitals for CVR: {}", recordVitalsDTO.getCvrNumber());
    
    CaseVisitRecord cvr = cvrRepository.findByCvrNumber(recordVitalsDTO.getCvrNumber())
        .orElseThrow(() -> new CvrNotFoundException(
            "CVR not found: " + recordVitalsDTO.getCvrNumber()
        ));
    
    // Create vitals entity
    CvrVitals vitals = new CvrVitals();
    vitals.setCaseVisitRecord(cvr);
    vitals.setRecordedBy(recordVitalsDTO.getRecordedBy());
    
    if (recordVitalsDTO.getTemperatureF() != null) {
        vitals.setTemperatureF(BigDecimal.valueOf(recordVitalsDTO.getTemperatureF()));
    }
    
    if (recordVitalsDTO.getBloodPressure() != null) {
        vitals.setBloodPressure(recordVitalsDTO.getBloodPressure());
    }
    
    vitals.setPulseRate(recordVitalsDTO.getPulseRate());
    vitals.setRespiratoryRate(recordVitalsDTO.getRespiratoryRate());
    vitals.setSpo2Percentage(recordVitalsDTO.getSpo2Percentage());
    
    if (recordVitalsDTO.getWeightKg() != null) {
        vitals.setWeightKg(BigDecimal.valueOf(recordVitalsDTO.getWeightKg()));
    }
    
    if (recordVitalsDTO.getHeightCm() != null) {
        vitals.setHeightCm(BigDecimal.valueOf(recordVitalsDTO.getHeightCm()));
    }
    
    // Save vitals
    CvrVitals savedVitals = cvrVitalsRepository.save(vitals);
    
    log.info("Vitals recorded successfully");
    return mapToVitalsDTO(savedVitals);
}

@Override
@Transactional(readOnly = true)
public List<CvrVitalsDTO> getVitalsByCVR(String cvrNumber) {
    log.info("Fetching vitals for CVR: {}", cvrNumber);
    
    CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
        .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));
    
    List<CvrVitals> vitals = cvrVitalsRepository
        .findByCaseVisitRecord_CvrIdOrderByRecordedAtDesc(cvr.getCvrId());
    
    return vitals.stream()
        .map(this::mapToVitalsDTO)
        .collect(Collectors.toList());
}

@Override
@Transactional(readOnly = true)
public List<CvrSummaryDTO> getRecentCVRs(int limit) {
    log.info("Fetching {} recent CVRs", limit);
    
    List<CaseVisitRecord> cvrs = cvrRepository.findRecentCVRs();
    
    return cvrs.stream()
        .limit(limit)
        .map(this::mapToSummaryDTO)
        .collect(Collectors.toList());
}

@Override
@Transactional(readOnly = true)
public List<CvrSummaryDTO> searchCVRs(String searchTerm) {
    log.info("Searching CVRs with term: {}", searchTerm);
    
    List<CaseVisitRecord> cvrs = cvrRepository.searchCVRs(searchTerm);
    
    return cvrs.stream()
        .map(this::mapToSummaryDTO)
        .collect(Collectors.toList());
}

@Override
public CvrDTO assignDoctor(String cvrNumber, String doctorId) {
    log.info("Assigning doctor {} to CVR: {}", doctorId, cvrNumber);
    
    CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
        .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));
    
    cvr.setDoctorId(doctorId);
    cvr.setStatus(CaseVisitRecord.CvrStatus.APPOINTMENT_SCHEDULED);
    CaseVisitRecord updatedCvr = cvrRepository.save(cvr);
    
    log.info("Doctor assigned successfully");
    Object patientData = getPatientDetails(cvr.getPinNumber());
    return mapToDTO(updatedCvr, patientData);
}

// Helper method for vitals mapping
private CvrVitalsDTO mapToVitalsDTO(CvrVitals vitals) {
    CvrVitalsDTO dto = new CvrVitalsDTO();
    dto.setVitalId(vitals.getVitalId());
    dto.setCvrId(vitals.getCaseVisitRecord().getCvrId());
    
    if (vitals.getTemperatureF() != null) {
        dto.setTemperatureF(vitals.getTemperatureF().doubleValue());
    }
    
    dto.setBloodPressureSystolic(vitals.getBloodPressureSystolic());
    dto.setBloodPressureDiastolic(vitals.getBloodPressureDiastolic());
    dto.setBloodPressure(vitals.getBloodPressure());
    dto.setPulseRate(vitals.getPulseRate());
    dto.setRespiratoryRate(vitals.getRespiratoryRate());
    dto.setSpo2Percentage(vitals.getSpo2Percentage());
    
    if (vitals.getWeightKg() != null) {
        dto.setWeightKg(vitals.getWeightKg().doubleValue());
    }
    
    if (vitals.getHeightCm() != null) {
        dto.setHeightCm(vitals.getHeightCm().doubleValue());
    }
    
    if (vitals.getBmi() != null) {
        dto.setBmi(vitals.getBmi().doubleValue());
    }
    
    dto.setRecordedAt(vitals.getRecordedAt());
    dto.setRecordedBy(vitals.getRecordedBy());
    
    return dto;
}


 /**
     * CHECK-IN PATIENT - THIS IS WHERE VISIT DATE/TIME ARE SET
     */
    @Override
    public CvrDTO checkInPatient(String cvrNumber) {
        log.info("========== CVR CHECK-IN ==========");
        log.info("CVR Number: {}", cvrNumber);
        
        CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
            .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));

        // Validate status
        if (cvr.getStatus() != CaseVisitRecord.CvrStatus.REGISTERED && 
            cvr.getStatus() != CaseVisitRecord.CvrStatus.APPOINTMENT_SCHEDULED) {
            throw new InvalidCvrStatusException(
                "CVR already checked in. Current status: " + cvr.getStatus()
            );
        }

        // *** CHECK-IN: Set visit date/time to NOW ***
        cvr.checkIn(); // This method sets visitDate, visitTime, and status
        
        CaseVisitRecord updatedCvr = cvrRepository.save(cvr);
        
        log.info("========== PATIENT CHECKED IN ==========");
        log.info("Visit Date: {} (NOW SET)", updatedCvr.getVisitDate());
        log.info("Visit Time: {} (NOW SET)", updatedCvr.getVisitTime());
        log.info("Status: {}", updatedCvr.getStatus());
        
        // Check if patient is late
        if (updatedCvr.getAppointmentDate() != null && updatedCvr.getAppointmentTime() != null) {
            LocalDate appointmentDate = updatedCvr.getAppointmentDate();
            LocalTime appointmentTime = updatedCvr.getAppointmentTime();
            LocalDate actualDate = updatedCvr.getVisitDate();
            LocalTime actualTime = updatedCvr.getVisitTime();
            
            if (actualDate.isAfter(appointmentDate) || 
                (actualDate.equals(appointmentDate) && actualTime.isAfter(appointmentTime))) {
                log.warn("Patient arrived late! Scheduled: {} {}, Actual: {} {}", 
                    appointmentDate, appointmentTime, actualDate, actualTime);
            }
        }

        Object patientData = getPatientDetails(cvr.getPinNumber());
        return mapToDTO(updatedCvr, patientData);
    }
    
    @Override
    public CvrDTO startConsultation(String cvrNumber) {
        log.info("Starting consultation - CVR: {}", cvrNumber);
        
        CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
            .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));

        if (cvr.getStatus() != CaseVisitRecord.CvrStatus.CHECKED_IN) {
            throw new InvalidCvrStatusException(
                "Patient must be checked in first. Current status: " + cvr.getStatus()
            );
        }

        cvr.startConsultation();
        CaseVisitRecord updatedCvr = cvrRepository.save(cvr);
        
        log.info("Consultation started");
        Object patientData = getPatientDetails(cvr.getPinNumber());
        return mapToDTO(updatedCvr, patientData);
    }
    
     @Override
    public CvrDTO completeConsultation(String cvrNumber) {
        log.info("Completing consultation - CVR: {}", cvrNumber);
        
        CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
            .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));

        if (cvr.getStatus() != CaseVisitRecord.CvrStatus.CONSULTING) {
            throw new InvalidCvrStatusException(
                "Consultation not started. Current status: " + cvr.getStatus()
            );
        }

        cvr.completeConsultation();
        CaseVisitRecord updatedCvr = cvrRepository.save(cvr);
        
        log.info("Consultation completed");
        Object patientData = getPatientDetails(cvr.getPinNumber());
        return mapToDTO(updatedCvr, patientData);
    }
    
    // Helper methods
    private void verifyPatient(String pinNumber) {
        try {
            ApiResponse<Boolean> response = patientServiceClient.checkPINExists(pinNumber);
            if (!response.isSuccess() || !Boolean.TRUE.equals(response.getData())) {
                throw new PatientNotFoundException("Patient not found: " + pinNumber);
            }
        } catch (FeignException e) {
            log.error("Error verifying patient: {}", e.getMessage());
            throw new PatientNotFoundException("Unable to verify patient");
        }
    }

    private Object getPatientDetails(String pinNumber) {
        try {
            return patientServiceClient.getPatientByPIN(pinNumber).getData();
        } catch (FeignException e) {
            log.warn("Could not fetch patient details: {}", e.getMessage());
            return null;
        }
    }

    private String getLastCVRFromDB() {
        List<String> cvrs = cvrRepository.findTopByOrderByCvrIdDesc();
        return cvrs.isEmpty() ? null : cvrs.get(0);
    }

    private CvrDTO mapToDTO(CaseVisitRecord cvr, Object patientData) {
        CvrDTO dto = modelMapper.map(cvr, CvrDTO.class);
        
        // Set computed fields
        dto.setHasVisited(cvr.hasVisited());
        dto.setIsPending(cvr.isPending());
        
        // Map patient details if available
        if (patientData != null) {
            // TODO: Properly map patient data
            dto.setPatientName("Patient Name");
        }
        
        return dto;
    }

    private CvrSummaryDTO mapToSummaryDTO(CaseVisitRecord cvr) {
        CvrSummaryDTO dto = new CvrSummaryDTO();
        dto.setCvrNumber(cvr.getCvrNumber());
        dto.setPinNumber(cvr.getPinNumber());
        dto.setAppointmentDate(cvr.getAppointmentDate());
        dto.setAppointmentTime(cvr.getAppointmentTime());
        dto.setVisitDate(cvr.getVisitDate());
        dto.setVisitTime(cvr.getVisitTime());
        dto.setVisitType(cvr.getVisitType().name());
        dto.setChiefComplaint(cvr.getChiefComplaint());
        dto.setStatus(cvr.getStatus().name());
        dto.setHasVisited(cvr.hasVisited());
        
        // Calculate arrival status
        if (cvr.getVisitDate() == null) {
            dto.setArrivalStatus("Not Arrived");
        } else if (cvr.getAppointmentDate() != null) {
            if (cvr.getVisitDate().isAfter(cvr.getAppointmentDate()) ||
                (cvr.getVisitDate().equals(cvr.getAppointmentDate()) && 
                 cvr.getVisitTime() != null && cvr.getAppointmentTime() != null &&
                 cvr.getVisitTime().isAfter(cvr.getAppointmentTime()))) {
                dto.setArrivalStatus("Late");
            } else {
                dto.setArrivalStatus("On Time");
            }
        } else {
            dto.setArrivalStatus("Walk-in");
        }
        
        return dto;
    }

    
     @Override
    @Transactional(readOnly = true)
    public CvrDTO getCVRByNumber(String cvrNumber) {
        CaseVisitRecord cvr = cvrRepository.findByCvrNumber(cvrNumber)
            .orElseThrow(() -> new CvrNotFoundException("CVR not found: " + cvrNumber));
        Object patientData = getPatientDetails(cvr.getPinNumber());
        return mapToDTO(cvr, patientData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CvrSummaryDTO> getTodaysCVRs() {
        List<CaseVisitRecord> cvrs = cvrRepository.findTodaysCVRs(LocalDate.now());
        return cvrs.stream().map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CvrSummaryDTO> getCVRsByDate(LocalDate date) {
        List<CaseVisitRecord> cvrs = cvrRepository.findByVisitDateOrderByVisitTimeAsc(date);
        return cvrs.stream().map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    public long countPatientVisits(String pinNumber) {
        return cvrRepository.countByPinNumber(pinNumber);
    }

    @Override
    public boolean existsByCVRNumber(String cvrNumber) {
        return cvrRepository.existsByCvrNumber(cvrNumber);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CvrDTO getCVRByAppointmentId(String appointmentId) {
        log.info("Fetching CVR by appointmentId: {}", appointmentId);

        CaseVisitRecord cvr = cvrRepository
                .findTopByAppointmentIdOrderByCreatedAtDesc(appointmentId)
                .orElseThrow(() -> new CvrNotFoundException(
                "CVR not found for appointmentId: " + appointmentId
        ));

        Object patientData = getPatientDetails(cvr.getPinNumber());
        return mapToDTO(cvr, patientData);
    }

}
