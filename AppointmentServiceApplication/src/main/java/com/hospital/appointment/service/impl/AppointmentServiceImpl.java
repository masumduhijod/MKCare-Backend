/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.service.impl;

/**
 *
 * @author mduhijod
 */

import com.hospital.appointment.client.*;
import com.hospital.appointment.dto.*;
import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.entity.AppointmentSlot;
import com.hospital.appointment.exception.*;
import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.appointment.repository.AppointmentSlotRepository;
import com.hospital.appointment.service.AppointmentService;
import com.hospital.appointment.util.AppointmentIdGenerator;
import com.hospital.appointment.util.TokenNumberGenerator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository slotRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;
    private final CvrServiceClient cvrServiceClient;
    private final AppointmentIdGenerator appointmentIdGenerator;
    private final TokenNumberGenerator tokenNumberGenerator;
    private final ModelMapper modelMapper;

    @Override
    public AppointmentDTO bookAppointment(BookAppointmentDTO bookDTO) {
        log.info("Booking appointment for PIN: {} with Doctor: {}", 
            bookDTO.getPinNumber(), bookDTO.getDoctorId());

        // 1. Verify patient exists
        verifyPatient(bookDTO.getPinNumber());

        // 2. Verify doctor exists
        verifyDoctor(bookDTO.getDoctorId());

        // 3. Check if patient already has appointment with same doctor on same date
        if (appointmentRepository.existsByPatientDoctorAndDate(
                bookDTO.getPinNumber(), bookDTO.getDoctorId(), bookDTO.getAppointmentDate())) {
            throw new AppointmentConflictException(
                "Patient already has an appointment with this doctor on " + bookDTO.getAppointmentDate()
            );
        }

        // 4. Find and book slot
        AppointmentSlot slot = null;
        if (bookDTO.getSlotId() != null) {
            slot = slotRepository.findById(bookDTO.getSlotId())
                .orElseThrow(() -> new SlotNotFoundException("Slot not found"));
            
            if (!slot.canBook()) {
                throw new SlotNotAvailableException("Selected slot is not available");
            }
        } else {
            // Find available slot for the time
            slot = slotRepository.findByDoctorIdAndSlotDateAndSlotTime(
                bookDTO.getDoctorId(), 
                bookDTO.getAppointmentDate(), 
                bookDTO.getAppointmentTime()
            ).orElseThrow(() -> new SlotNotFoundException(
                "No slot available for selected time"
            ));

            if (!slot.canBook()) {
                throw new SlotNotAvailableException("Slot is fully booked");
            }
        }

        // 5. Generate appointment ID
        String lastAppointmentId = getLastAppointmentIdFromDB();
        String newAppointmentId = appointmentIdGenerator.generateAppointmentId(lastAppointmentId);
        log.info("Generated Appointment ID: {}", newAppointmentId);

        // 6. Generate token number
        Integer lastToken = appointmentRepository.findMaxTokenByDoctorAndDate(
            bookDTO.getDoctorId(), bookDTO.getAppointmentDate()
        );
        int tokenNumber = tokenNumberGenerator.generateTokenNumber(lastToken);
        log.info("Generated Token Number: {}", tokenNumber);

        // 7. Get patient details
        Object patientData = getPatientDetails(bookDTO.getPinNumber());

        // 8. Create appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(newAppointmentId);
        appointment.setPinNumber(bookDTO.getPinNumber());
        appointment.setPatientId(1L); // TODO: Extract from patientData
        appointment.setDoctorId(bookDTO.getDoctorId());
        appointment.setAppointmentDate(bookDTO.getAppointmentDate());
        appointment.setAppointmentTime(bookDTO.getAppointmentTime());
        appointment.setSlotId(slot.getSlotId());
        appointment.setTokenNumber(tokenNumber);
        appointment.setAppointmentType(
            bookDTO.getAppointmentType() != null 
                ? Appointment.AppointmentType.valueOf(bookDTO.getAppointmentType().toUpperCase())
                : Appointment.AppointmentType.NEW
        );
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        appointment.setSymptoms(bookDTO.getSymptoms());
        appointment.setNotes(bookDTO.getNotes());
        appointment.setCreatedBy(bookDTO.getCreatedBy());

        // Link CVR if provided
        if (bookDTO.getCvrNumber() != null) {
            try {
                Object cvrData = cvrServiceClient.getCVRByNumber(bookDTO.getCvrNumber()).getData();
                appointment.setCvrNumber(bookDTO.getCvrNumber());
                // TODO: Set CVR ID from cvrData
            } catch (FeignException e) {
                log.warn("CVR not found, proceeding without CVR link");
            }
        }

        // 9. Save appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 10. Book the slot
        slot.book(newAppointmentId);
        slotRepository.save(slot);

        log.info("Appointment booked successfully: {}", newAppointmentId);

        return mapToDTO(savedAppointment, patientData, null);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(String appointmentId) {
        log.info("Fetching appointment: {}", appointmentId);

        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new AppointmentNotFoundException(
                "Appointment not found: " + appointmentId
            ));

        Object patientData = getPatientDetails(appointment.getPinNumber());
        Object doctorData = getDoctorDetails(appointment.getDoctorId());

        return mapToDTO(appointment, patientData, doctorData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointments(String pinNumber) {
        log.info("Fetching appointments for patient: {}", pinNumber);

        List<Appointment> appointments = appointmentRepository
            .findByPinNumberOrderByAppointmentDateDescAppointmentTimeDesc(pinNumber);

        Object patientData = getPatientDetails(pinNumber);

        return appointments.stream()
            .map(apt -> mapToDTO(apt, patientData, null))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentSummaryDTO> getDoctorAppointments(String doctorId, LocalDate date) {
        log.info("Fetching appointments for doctor: {} on {}", doctorId, date);

        List<Appointment> appointments = appointmentRepository
            .findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(doctorId, date);

        return appointments.stream()
            .map(this::mapToSummaryDTO)
            .collect(Collectors.toList());
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<AppointmentSummaryDTO> getTodaysAppointments() {
//        log.info("Fetching today's appointments");
//
//        List<Appointment> appointments = appointmentRepository
//            .findTodaysAppointments(LocalDate.now());
//
//        return appointments.stream()
//            .map(this::mapToSummaryDTO)
//            .collect(Collectors.toList());
//    }  
    
    @Override
@Transactional(readOnly = true)
public List<AppointmentSummaryDTO> getTodaysAppointments() {
    List<Appointment> appointments =
            appointmentRepository.findTodaysAppointments(LocalDate.now());

    return appointments.stream()
            .map(this::mapToSummaryDTO)
            .collect(Collectors.toList());
}


    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getUpcomingAppointments(String pinNumber) {
        log.info("Fetching upcoming appointments for patient: {}", pinNumber);

        List<Appointment> appointments = appointmentRepository
            .findUpcomingAppointmentsByPatient(pinNumber, LocalDate.now());

        Object patientData = getPatientDetails(pinNumber);

        return appointments.stream()
            .map(apt -> mapToDTO(apt, patientData, null))
            .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO checkInAppointment(String appointmentId) {
        log.info("Checking in appointment: {}", appointmentId);

        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new AppointmentNotFoundException(
                "Appointment not found: " + appointmentId
            ));

        appointment.checkIn();
        Appointment updated = appointmentRepository.save(appointment);

        log.info("Appointment checked in successfully");

        Object patientData = getPatientDetails(appointment.getPinNumber());
        return mapToDTO(updated, patientData, null);
    }

    @Override
    public AppointmentDTO startConsultation(String appointmentId) {
        log.info("Starting consultation: {}", appointmentId);

        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new AppointmentNotFoundException(
                "Appointment not found: " + appointmentId
            ));

        appointment.startConsultation();
        Appointment updated = appointmentRepository.save(appointment);

        log.info("Consultation started");

        Object patientData = getPatientDetails(appointment.getPinNumber());
        return mapToDTO(updated, patientData, null);
    }

    @Override
    public AppointmentDTO completeConsultation(String appointmentId) {
        log.info("Completing consultation: {}", appointmentId);

        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new AppointmentNotFoundException(
                "Appointment not found: " + appointmentId
            ));

        appointment.completeConsultation();
        Appointment updated = appointmentRepository.save(appointment);

        log.info("Consultation completed");

        Object patientData = getPatientDetails(appointment.getPinNumber());
        return mapToDTO(updated, patientData, null);
    }

    @Override
    public String cancelAppointment(CancelAppointmentDTO cancelDTO) {
        log.info("Cancelling appointment: {}", cancelDTO.getAppointmentId());

        Appointment appointment = appointmentRepository.findByAppointmentId(
            cancelDTO.getAppointmentId()
        ).orElseThrow(() -> new AppointmentNotFoundException(
            "Appointment not found: " + cancelDTO.getAppointmentId()
        ));

        // Release slot
        if (appointment.getSlotId() != null) {
            slotRepository.findById(appointment.getSlotId()).ifPresent(slot -> {
                slot.release();
                slotRepository.save(slot);
            });
        }

        appointment.cancel(cancelDTO.getReason(), cancelDTO.getCancelledBy());
        appointmentRepository.save(appointment);

        log.info("Appointment cancelled successfully");
        return "Appointment cancelled successfully";
    }

    // Helper methods
    private String getLastAppointmentIdFromDB() {
        List<String> ids = appointmentRepository.findTopByOrderByIdDesc();
        return ids.isEmpty() ? null : ids.get(0);
    }

    private void verifyPatient(String pinNumber) {
        try {
            Object response = patientServiceClient.checkPINExists(pinNumber).getData();
            if (response == null || !(Boolean) response) {
                throw new PatientNotFoundException("Patient not found: " + pinNumber);
            }
        } catch (FeignException e) {
            throw new PatientNotFoundException("Unable to verify patient");
        }
    }

    private void verifyDoctor(String doctorId) {
        try {
            Object response = doctorServiceClient.checkDoctorExists(doctorId).getData();
            if (response == null || !(Boolean) response) {
                throw new DoctorNotFoundException("Doctor not found: " + doctorId);
            }
        } catch (FeignException e) {
            throw new DoctorNotFoundException("Unable to verify doctor");
        }
    }

    private Object getPatientDetails(String pinNumber) {
        try {
            return patientServiceClient.getPatientByPIN(pinNumber).getData();
        } catch (Exception e) {
            log.warn("Could not fetch patient details");
            return null;
        }
    }

    private Object getDoctorDetails(String doctorId) {
        try {
            return doctorServiceClient.getDoctorById(doctorId).getData();
        } catch (Exception e) {
            log.warn("Could not fetch doctor details");
            return null;
        }
    }

    private AppointmentDTO mapToDTO(Appointment apt, Object patientData, Object doctorData) {
        AppointmentDTO dto = modelMapper.map(apt, AppointmentDTO.class);
        // TODO: Map patient and doctor data properly
        dto.setPatientName("Patient");
        dto.setDoctorName("Doctor");
        return dto;
    }

//    private AppointmentSummaryDTO mapToSummaryDTO(Appointment apt) {
//        AppointmentSummaryDTO dto = new AppointmentSummaryDTO();
//        dto.setAppointmentId(apt.getAppointmentId());
//        dto.setPinNumber(apt.getPinNumber());
//        dto.setAppointmentDate(apt.getAppointmentDate());
//        dto.setAppointmentTime(apt.getAppointmentTime());
//        dto.setTokenNumber(apt.getTokenNumber());
//        dto.setStatus(apt.getStatus().name());
//        dto.setAppointmentType(apt.getAppointmentType().name());
//        return dto;
//    }
    
    private AppointmentSummaryDTO mapToSummaryDTO(Appointment apt) {

    AppointmentSummaryDTO dto = new AppointmentSummaryDTO();

    dto.setAppointmentId(apt.getAppointmentId());
    dto.setTokenNumber(apt.getTokenNumber());
    dto.setPinNumber(apt.getPinNumber());
    dto.setDoctorId(apt.getDoctorId());
    dto.setAppointmentDate(apt.getAppointmentDate());
    dto.setAppointmentTime(apt.getAppointmentTime());
    dto.setStatus(apt.getStatus().name());

    // ===============================
    // ✅ FETCH CVR USING APPOINTMENT ID
    // ===============================
    try {
        ApiResponse<CvrDTO> response =
                cvrServiceClient.getCVRByAppointmentId(apt.getAppointmentId());

        if (response != null && response.isSuccess() && response.getData() != null) {
            dto.setCvrNumber(response.getData().getCvrNumber());
        } else {
            dto.setCvrNumber(null);
        }
    } catch (Exception ex) {
        // CVR not created yet
        dto.setCvrNumber(null);
    }

    return dto;
}


    // Remaining methods...
    @Override
    public AppointmentDTO rescheduleAppointment(RescheduleAppointmentDTO rescheduleDTO) {
        // TODO: Implement reschedule logic
        return null;
    }

    @Override
    public AppointmentDTO markNoShow(String appointmentId) {
        // TODO: Implement no-show logic
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByStatus(String status) {
        log.info("Fetching appointments by status: {}", status);
        try {
            Appointment.AppointmentStatus appointmentStatus =
                    Appointment.AppointmentStatus.valueOf(status.toUpperCase());
            List<Appointment> appointments =
                    appointmentRepository.findByStatusOrderByAppointmentDateDescAppointmentTimeDesc(appointmentStatus);
            return appointments.stream()
                    .map(apt -> mapToDTO(apt, null, null))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid appointment status: {}", status);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentSummaryDTO> searchAppointments(String searchTerm) {
        log.info("Searching appointments for term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Appointment> appointments = appointmentRepository.searchAppointments(searchTerm.trim());
        return appointments.stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentSummaryDTO> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching appointments from {} to {}", startDate, endDate);
        List<Appointment> appointments = appointmentRepository.findByDateRange(startDate, endDate);
        return appointments.stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAppointmentId(String appointmentId) {
        return appointmentRepository.existsByAppointmentId(appointmentId);
    }
}