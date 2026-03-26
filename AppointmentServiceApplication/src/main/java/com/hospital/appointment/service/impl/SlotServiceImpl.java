/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.service.impl;

import com.hospital.appointment.client.DoctorServiceClient;
import com.hospital.appointment.dto.AvailabilityCheckDTO;
import com.hospital.appointment.dto.DoctorScheduleDTO;
import com.hospital.appointment.dto.SlotDTO;
import com.hospital.appointment.entity.AppointmentSlot;
import com.hospital.appointment.exception.DoctorNotFoundException;
import com.hospital.appointment.exception.SlotNotFoundException;
import com.hospital.appointment.repository.AppointmentSlotRepository;
import com.hospital.appointment.service.SlotService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SlotServiceImpl implements SlotService {

    private final AppointmentSlotRepository slotRepository;
    private final DoctorServiceClient doctorServiceClient;
    private final ModelMapper modelMapper;

    @Override
    public List<SlotDTO> generateSlots(String doctorId, LocalDate date) {
        log.info("Generating/syncing slots for doctor: {} on {}", doctorId, date);

        // Check if slots already exist
        List<AppointmentSlot> existingSlots = slotRepository
            .findByDoctorIdAndSlotDateOrderBySlotTimeAsc(doctorId, date);
        
        java.util.Set<LocalTime> existingSlotTimes = existingSlots.stream()
            .map(AppointmentSlot::getSlotTime)
            .collect(Collectors.toSet());

        // *** CHANGED: Get all doctor schedules for the date ***
        List<DoctorScheduleDTO> schedules;
        try {
            schedules = doctorServiceClient
                .getSchedulesByDateRange(doctorId, date, date)
                .getData();
        } catch (FeignException e) {
            log.error("Error fetching doctor schedule: {}", e.getMessage());
            throw new DoctorNotFoundException(
                "Error retrieving doctor schedules: " + e.getMessage()
            );
        }

        if (schedules == null || schedules.isEmpty()) {
            log.warn("No schedule found for doctor {} on {}", doctorId, date);
            return existingSlots.stream().map(this::mapToDTO).collect(Collectors.toList());
        }

        List<AppointmentSlot> slotsToSave = new ArrayList<>();

        for (DoctorScheduleDTO schedule : schedules) {
            if (schedule == null || !schedule.getIsActive()) continue;

            List<AppointmentSlot> potentialSlots = generateSlotsFromSchedule(doctorId, date, schedule);
            for (AppointmentSlot slot : potentialSlots) {
                if (!existingSlotTimes.contains(slot.getSlotTime())) {
                    slotsToSave.add(slot);
                    existingSlotTimes.add(slot.getSlotTime());
                }
            }
        }

        if (slotsToSave.isEmpty()) {
            log.info("All slots already exist. Returning existing slots.");
            return existingSlots.stream().map(this::mapToDTO).collect(Collectors.toList());
        }

        // Save new slots
        List<AppointmentSlot> savedSlots = slotRepository.saveAll(slotsToSave);
        log.info("Generated {} new slots for doctor {} on {}", 
            savedSlots.size(), doctorId, date);
            
        // Combine and return all slots ordered by time
        existingSlots.addAll(savedSlots);
        existingSlots.sort((s1, s2) -> s1.getSlotTime().compareTo(s2.getSlotTime()));

        return existingSlots.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotDTO> getAvailableSlots(String doctorId, LocalDate date) {
        log.info("Fetching available slots for doctor: {} on {}", doctorId, date);
        
        List<AppointmentSlot> slots = slotRepository.findAvailableSlots(doctorId, date);
        
        return slots.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotDTO> getAllSlots(String doctorId, LocalDate date) {
        log.info("Fetching all slots for doctor: {} on {}", doctorId, date);
        
        List<AppointmentSlot> slots = slotRepository
            .findByDoctorIdAndSlotDateOrderBySlotTimeAsc(doctorId, date);
        
        return slots.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityCheckDTO checkAvailability(String doctorId, LocalDate date) {
        log.info("Checking availability for doctor: {} on {}", doctorId, date);

        long totalSlots = slotRepository.countByDoctorIdAndSlotDate(doctorId, date);
        long availableSlots = slotRepository.countAvailableSlots(doctorId, date);
        long bookedSlots = totalSlots - availableSlots;

        AvailabilityCheckDTO availability = new AvailabilityCheckDTO();
        availability.setDoctorId(doctorId);
        availability.setDate(date);
        availability.setTotalSlots((int) totalSlots);
        availability.setAvailableSlots((int) availableSlots);
        availability.setBookedSlots((int) bookedSlots);
        availability.setHasSlots(availableSlots > 0);

        // Get doctor name
        try {
            Object doctorData = doctorServiceClient.getDoctorById(doctorId).getData();
            // TODO: Set doctor name properly if DoctorDTO is available
            availability.setDoctorName("Doctor");
        } catch (Exception e) {
            log.warn("Could not fetch doctor details: {}", e.getMessage());
        }

        return availability;
    }

    @Override
    public SlotDTO bookSlot(Long slotId, String appointmentId) {
        log.info("Booking slot: {} for appointment: {}", slotId, appointmentId);

        AppointmentSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new SlotNotFoundException("Slot not found: " + slotId));

        if (!slot.canBook()) {
            throw new SlotNotFoundException("Slot is not available for booking");
        }

        slot.book(appointmentId);
        AppointmentSlot updatedSlot = slotRepository.save(slot);

        log.info("Slot booked successfully");
        return mapToDTO(updatedSlot);
    }

    @Override
    public SlotDTO releaseSlot(Long slotId) {
        log.info("Releasing slot: {}", slotId);

        AppointmentSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new SlotNotFoundException("Slot not found: " + slotId));

        slot.release();
        AppointmentSlot updatedSlot = slotRepository.save(slot);

        log.info("Slot released successfully");
        return mapToDTO(updatedSlot);
    }

    @Override
    public SlotDTO markSlotUnavailable(Long slotId) {
        log.info("Marking slot unavailable: {}", slotId);

        AppointmentSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new SlotNotFoundException("Slot not found: " + slotId));

        slot.markUnavailable();
        AppointmentSlot updatedSlot = slotRepository.save(slot);

        log.info("Slot marked as unavailable");
        return mapToDTO(updatedSlot);
    }

    // *** NEW: Generate slots from actual schedule ***
    private List<AppointmentSlot> generateSlotsFromSchedule(
            String doctorId, 
            LocalDate date, 
            DoctorScheduleDTO schedule) {
        
        List<AppointmentSlot> slots = new ArrayList<>();
        
        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();
        int slotDuration = schedule.getSlotDurationMinutes();
        int maxPatients = schedule.getMaxPatientsPerSlot();
        
        LocalTime breakStart = schedule.getBreakStartTime();
        LocalTime breakEnd = schedule.getBreakEndTime();
        
        LocalTime currentTime = startTime;
        
        log.info("Generating slots: {} to {} (duration: {}min, max: {} patients)", 
            startTime, endTime, slotDuration, maxPatients);
        
        if (breakStart != null && breakEnd != null) {
            log.info("Break time: {} to {}", breakStart, breakEnd);
        }
        
        while (currentTime.isBefore(endTime)) {
            // *** Skip break time ***
            if (breakStart != null && breakEnd != null) {
                if (!currentTime.isBefore(breakStart) && currentTime.isBefore(breakEnd)) {
                    log.debug("Skipping break time slot: {}", currentTime);
                    currentTime = currentTime.plusMinutes(slotDuration);
                    continue;
                }
            }
            
            AppointmentSlot slot = new AppointmentSlot();
            slot.setScheduleId(schedule.getScheduleId());  // *** CRITICAL: Link to schedule ***
            slot.setDoctorId(doctorId);
            slot.setSlotDate(date);
            slot.setSlotTime(currentTime);
            slot.setIsAvailable(true);
            slot.setMaxPatients(maxPatients);
            slot.setBookedCount(0);
            
            slots.add(slot);
            currentTime = currentTime.plusMinutes(slotDuration);
        }
        
        log.info("Generated {} slots (excluding break time)", slots.size());
        return slots;
    }

    // Helper method
    private SlotDTO mapToDTO(AppointmentSlot slot) {
        SlotDTO dto = modelMapper.map(slot, SlotDTO.class);
        dto.setAvailableCapacity(slot.getAvailableCapacity());
        return dto;
    }
}
