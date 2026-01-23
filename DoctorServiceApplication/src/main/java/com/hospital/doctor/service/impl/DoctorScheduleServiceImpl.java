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

import com.hospital.doctor.dto.AvailabilityDTO;
import com.hospital.doctor.dto.DoctorScheduleDTO;
import com.hospital.doctor.entity.Doctor;
import com.hospital.doctor.entity.DoctorSchedule;
import com.hospital.doctor.exception.DoctorNotFoundException;
import com.hospital.doctor.exception.ScheduleNotFoundException;
import com.hospital.doctor.exception.ScheduleConflictException;
import com.hospital.doctor.repository.DoctorRepository;
import com.hospital.doctor.repository.DoctorScheduleRepository;
import com.hospital.doctor.service.DoctorScheduleService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@Service
//@RequiredArgsConstructor
//@Slf4j
//@Transactional
//public class DoctorScheduleServiceImpl implements DoctorScheduleService {
//
//    private final DoctorScheduleRepository scheduleRepository;
//    private final DoctorRepository doctorRepository;
//    private final ModelMapper modelMapper;
//
//    @Override
//    public DoctorScheduleDTO addSchedule(String doctorId, DoctorScheduleDTO scheduleDTO) {
//        log.info("Adding schedule for doctor: {} on {}", doctorId, scheduleDTO.getDayOfWeek());
//
//        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
//            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));
//
//        // Check if schedule already exists for this day
//        DoctorSchedule.DayOfWeek day = DoctorSchedule.DayOfWeek.valueOf(
//            scheduleDTO.getDayOfWeek().toUpperCase()
//        );
//        
//        if (scheduleRepository.existsByDoctorIdAndDay(doctorId, day)) {
//            throw new ScheduleConflictException(
//                "Schedule already exists for " + scheduleDTO.getDayOfWeek()
//            );
//        }
//
//        // Create schedule
//        DoctorSchedule schedule = new DoctorSchedule();
//        schedule.setDoctor(doctor);
//        schedule.setDayOfWeek(day);
//        schedule.setStartTime(scheduleDTO.getStartTime());
//        schedule.setEndTime(scheduleDTO.getEndTime());
//        schedule.setSlotDurationMinutes(scheduleDTO.getSlotDurationMinutes() != null 
//            ? scheduleDTO.getSlotDurationMinutes() : 15);
//        schedule.setMaxPatientsPerSlot(scheduleDTO.getMaxPatientsPerSlot() != null 
//            ? scheduleDTO.getMaxPatientsPerSlot() : 1);
//        schedule.setIsActive(true);
//        schedule.setBreakStartTime(scheduleDTO.getBreakStartTime());
//        schedule.setBreakEndTime(scheduleDTO.getBreakEndTime());
//
//        DoctorSchedule savedSchedule = scheduleRepository.save(schedule);
//        log.info("Schedule added successfully");
//
//        return mapToDTO(savedSchedule);
//    }
//
//    @Override
//    public DoctorScheduleDTO updateSchedule(Long scheduleId, DoctorScheduleDTO scheduleDTO) {
//        log.info("Updating schedule: {}", scheduleId);
//
//        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
//            .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found: " + scheduleId));
//
//        schedule.setStartTime(scheduleDTO.getStartTime());
//        schedule.setEndTime(scheduleDTO.getEndTime());
//        schedule.setSlotDurationMinutes(scheduleDTO.getSlotDurationMinutes());
//        schedule.setMaxPatientsPerSlot(scheduleDTO.getMaxPatientsPerSlot());
//        schedule.setBreakStartTime(scheduleDTO.getBreakStartTime());
//        schedule.setBreakEndTime(scheduleDTO.getBreakEndTime());
//
//        DoctorSchedule updatedSchedule = scheduleRepository.save(schedule);
//        log.info("Schedule updated successfully");
//
//        return mapToDTO(updatedSchedule);
//    }
//
//    @Override
//    public String deleteSchedule(Long scheduleId) {
//        log.info("Deleting schedule: {}", scheduleId);
//
//        if (!scheduleRepository.existsById(scheduleId)) {
//            throw new ScheduleNotFoundException("Schedule not found: " + scheduleId);
//        }
//
//        scheduleRepository.deleteById(scheduleId);
//        log.info("Schedule deleted successfully");
//        return "Schedule deleted successfully";
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<DoctorScheduleDTO> getDoctorSchedules(String doctorId) {
//        log.info("Fetching schedules for doctor: {}", doctorId);
//        
//        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorId(doctorId);
//        
//        return schedules.stream()
//            .map(this::mapToDTO)
//            .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<DoctorScheduleDTO> getActiveDoctorSchedules(String doctorId) {
//        log.info("Fetching active schedules for doctor: {}", doctorId);
//        
//        List<DoctorSchedule> schedules = scheduleRepository.findActiveDoctorSchedules(doctorId);
//        
//        return schedules.stream()
//            .map(this::mapToDTO)
//            .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public DoctorScheduleDTO getScheduleByDoctorAndDay(String doctorId, String dayOfWeek) {
//        log.info("Fetching schedule for doctor: {} on {}", doctorId, dayOfWeek);
//        
//        DoctorSchedule.DayOfWeek day = DoctorSchedule.DayOfWeek.valueOf(dayOfWeek.toUpperCase());
//        
//        DoctorSchedule schedule = scheduleRepository.findByDoctorIdAndDay(doctorId, day)
//            .orElseThrow(() -> new ScheduleNotFoundException(
//                "No schedule found for " + dayOfWeek
//            ));
//        
//        return mapToDTO(schedule);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public AvailabilityDTO checkAvailability(String doctorId, String dayOfWeek) {
//        log.info("Checking availability for doctor: {} on {}", doctorId, dayOfWeek);
//
//        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
//                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));
//
//        AvailabilityDTO availability = new AvailabilityDTO();
//        availability.setDoctorId(doctorId);
//        availability.setDoctorName(doctor.getFullName());
//        availability.setSpecialization(doctor.getSpecialization());
//        availability.setDayOfWeek(dayOfWeek);
//
//        DoctorSchedule.DayOfWeek day = DoctorSchedule.DayOfWeek.valueOf(dayOfWeek.toUpperCase());
//
//        Optional<DoctorSchedule> optionalSchedule = scheduleRepository.findByDoctorIdAndDay(doctorId, day);
//
//        if (optionalSchedule.isPresent()) {
//            DoctorSchedule schedule = optionalSchedule.get();
//            availability.setStartTime(schedule.getStartTime());
//            availability.setEndTime(schedule.getEndTime());
//            availability.setAvailableSlots(schedule.getTotalSlots());
//            availability.setIsAvailable(schedule.isActiveSchedule() && doctor.isAvailable());
//        } else {
//            availability.setIsAvailable(false);
//            availability.setAvailableSlots(0);
//        }
//
//        return availability;
//
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<DoctorScheduleDTO> getSchedulesByDay(String dayOfWeek) {
//        log.info("Fetching all schedules for: {}", dayOfWeek);
//        
//        DoctorSchedule.DayOfWeek day = DoctorSchedule.DayOfWeek.valueOf(dayOfWeek.toUpperCase());
//        List<DoctorSchedule> schedules = scheduleRepository.findByDayOfWeekAndIsActiveTrue(day);
//        
//        return schedules.stream()
//            .map(this::mapToDTO)
//            .collect(Collectors.toList());
//    }
//
//    @Override
//    public DoctorScheduleDTO toggleScheduleStatus(Long scheduleId, boolean isActive) {
//        log.info("Toggling schedule status: {} to {}", scheduleId, isActive);
//        
//        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
//            .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found: " + scheduleId));
//        
//        schedule.setIsActive(isActive);
//        DoctorSchedule updatedSchedule = scheduleRepository.save(schedule);
//        
//        log.info("Schedule status updated");
//        return mapToDTO(updatedSchedule);
//    }
//
//    // Helper method
//    private DoctorScheduleDTO mapToDTO(DoctorSchedule schedule) {
//        DoctorScheduleDTO dto = modelMapper.map(schedule, DoctorScheduleDTO.class);
//        dto.setDoctorId(schedule.getDoctor().getDoctorId());
//        dto.setDayOfWeek(schedule.getDayOfWeek().name());
//        dto.setTotalSlots(schedule.getTotalSlots());
//        return dto;
//    }
//}
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

//    @Override
//    public DoctorScheduleDTO addSchedule(String doctorId, DoctorScheduleDTO scheduleDTO) {
//        // *** CHANGED: Log with date instead of day ***
//        log.info("Adding schedule for doctor: {} on {}", doctorId, scheduleDTO.getScheduleDate());
//
//        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
//            .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));
//
//        // *** CHANGED: Check if schedule already exists for this DATE ***
//        if (scheduleRepository.existsByDoctorIdAndDate(doctorId, scheduleDTO.getScheduleDate())) {
//            throw new ScheduleConflictException(
//                "Schedule already exists for " + scheduleDTO.getScheduleDate()
//            );
//        }
//
//        // *** CHANGED: Validate schedule date is not in past ***
//        if (scheduleDTO.getScheduleDate().isBefore(LocalDate.now())) {
//            throw new IllegalArgumentException("Schedule date cannot be in the past");
//        }
//
//        // Create schedule
//        DoctorSchedule schedule = new DoctorSchedule();
//        schedule.setDoctor(doctor);
//        schedule.setScheduleDate(scheduleDTO.getScheduleDate());  // CHANGED
//        schedule.setStartTime(scheduleDTO.getStartTime());
//        schedule.setEndTime(scheduleDTO.getEndTime());
//        schedule.setSlotDurationMinutes(scheduleDTO.getSlotDurationMinutes() != null 
//            ? scheduleDTO.getSlotDurationMinutes() : 15);
//        schedule.setMaxPatientsPerSlot(scheduleDTO.getMaxPatientsPerSlot() != null 
//            ? scheduleDTO.getMaxPatientsPerSlot() : 1);
//        schedule.setIsActive(true);
//        schedule.setBreakStartTime(scheduleDTO.getBreakStartTime());
//        schedule.setBreakEndTime(scheduleDTO.getBreakEndTime());
//
//        DoctorSchedule savedSchedule = scheduleRepository.save(schedule);
//        log.info("Schedule added successfully for date: {}", scheduleDTO.getScheduleDate());
//
//        return mapToDTO(savedSchedule);
//    }
    @Override
    public DoctorScheduleDTO addSchedule(String doctorId, DoctorScheduleDTO scheduleDTO) {
        // Log with date
        log.info("Adding schedule for doctor: {} on {}", doctorId, scheduleDTO.getScheduleDate());

        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        // Validate schedule date is not in past
        if (scheduleDTO.getScheduleDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Schedule date cannot be in the past");
        }

        // Fetch all schedules for that doctor on the same date
        List<DoctorSchedule> existingSchedules = scheduleRepository
                .findByDoctorIdAndDate1(doctorId, scheduleDTO.getScheduleDate());

        // Check for time overlap
        for (DoctorSchedule existing : existingSchedules) {
            boolean overlaps = !(scheduleDTO.getEndTime().isBefore(existing.getStartTime())
                    || scheduleDTO.getStartTime().isAfter(existing.getEndTime()));
            if (overlaps) {
                throw new ScheduleConflictException(
                        "Schedule conflicts with existing schedule from "
                        + existing.getStartTime() + " to " + existing.getEndTime()
                );
            }
        }

        // Create schedule
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctor(doctor);
        schedule.setScheduleDate(scheduleDTO.getScheduleDate());
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
        schedule.setSlotDurationMinutes(scheduleDTO.getSlotDurationMinutes() != null
                ? scheduleDTO.getSlotDurationMinutes() : 15);
        schedule.setMaxPatientsPerSlot(scheduleDTO.getMaxPatientsPerSlot() != null
                ? scheduleDTO.getMaxPatientsPerSlot() : 1);
        schedule.setIsActive(true);
        schedule.setBreakStartTime(scheduleDTO.getBreakStartTime());
        schedule.setBreakEndTime(scheduleDTO.getBreakEndTime());

        DoctorSchedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Schedule added successfully for date: {}", scheduleDTO.getScheduleDate());

        return mapToDTO(savedSchedule);
    }

    @Override
    public DoctorScheduleDTO updateSchedule(Long scheduleId, DoctorScheduleDTO scheduleDTO) {
        log.info("Updating schedule: {}", scheduleId);

        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found: " + scheduleId));

        // Update timings only (date cannot be changed)
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
        schedule.setSlotDurationMinutes(scheduleDTO.getSlotDurationMinutes());
        schedule.setMaxPatientsPerSlot(scheduleDTO.getMaxPatientsPerSlot());
        schedule.setBreakStartTime(scheduleDTO.getBreakStartTime());
        schedule.setBreakEndTime(scheduleDTO.getBreakEndTime());
        
        // *** NEW: Allow updating status ***
        if (scheduleDTO.getIsActive() != null) {
            schedule.setIsActive(scheduleDTO.getIsActive());
        }

        DoctorSchedule updatedSchedule = scheduleRepository.save(schedule);
        log.info("Schedule updated successfully");

        return mapToDTO(updatedSchedule);
    }

    @Override
    public String deleteSchedule(Long scheduleId) {
        log.info("Deleting schedule: {}", scheduleId);

        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ScheduleNotFoundException("Schedule not found: " + scheduleId);
        }

        scheduleRepository.deleteById(scheduleId);
        log.info("Schedule deleted successfully");
        return "Schedule deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleDTO> getDoctorSchedules(String doctorId) {
        log.info("Fetching schedules for doctor: {}", doctorId);
        
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        
        return schedules.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleDTO> getActiveDoctorSchedules(String doctorId) {
        log.info("Fetching active schedules for doctor: {}", doctorId);
        
        List<DoctorSchedule> schedules = scheduleRepository.findActiveDoctorSchedules(doctorId);
        
        return schedules.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // *** CHANGED: Get schedule by date instead of day ***
    @Override
    @Transactional(readOnly = true)
    public DoctorScheduleDTO getScheduleByDoctorAndDate(String doctorId, LocalDate scheduleDate) {
        log.info("Fetching schedule for doctor: {} on {}", doctorId, scheduleDate);
        
        DoctorSchedule schedule = scheduleRepository.findByDoctorIdAndDate(doctorId, scheduleDate)
            .orElseThrow(() -> new ScheduleNotFoundException(
                "No schedule found for " + scheduleDate
            ));
        
        return mapToDTO(schedule);
    }

    // *** CHANGED: Check availability by date ***
    @Override
    @Transactional(readOnly = true)
    public AvailabilityDTO checkAvailability(String doctorId, LocalDate scheduleDate) {
        log.info("Checking availability for doctor: {} on {}", doctorId, scheduleDate);

        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        AvailabilityDTO availability = new AvailabilityDTO();
        availability.setDoctorId(doctorId);
        availability.setDoctorName(doctor.getFullName());
        availability.setSpecialization(doctor.getSpecialization());
        availability.setScheduleDate(scheduleDate);  // CHANGED
        availability.setDayOfWeek(scheduleDate.getDayOfWeek().name());  // NEW

        Optional<DoctorSchedule> optionalSchedule = 
            scheduleRepository.findByDoctorIdAndDate(doctorId, scheduleDate);

        if (optionalSchedule.isPresent()) {
            DoctorSchedule schedule = optionalSchedule.get();
            availability.setStartTime(schedule.getStartTime());
            availability.setEndTime(schedule.getEndTime());
            availability.setAvailableSlots(schedule.getTotalSlots());
            availability.setIsAvailable(schedule.isActiveSchedule() && doctor.isAvailable());
        } else {
            availability.setIsAvailable(false);
            availability.setAvailableSlots(0);
        }

        return availability;
    }

    // *** NEW: Get schedules in date range ***
    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleDTO> getSchedulesByDateRange(
            String doctorId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching schedules for doctor: {} from {} to {}", 
            doctorId, startDate, endDate);
        
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorIdAndDateRange(
            doctorId, startDate, endDate
        );
        
        return schedules.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // *** NEW: Get upcoming schedules ***
    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleDTO> getUpcomingSchedules(String doctorId, int days) {
        log.info("Fetching upcoming {} days schedules for doctor: {}", days, doctorId);
        
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorIdAndDateRange(
            doctorId, today, endDate
        );
        
        return schedules.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public DoctorScheduleDTO toggleScheduleStatus(Long scheduleId, boolean isActive) {
        log.info("Toggling schedule status: {} to {}", scheduleId, isActive);
        
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found: " + scheduleId));
        
        schedule.setIsActive(isActive);
        DoctorSchedule updatedSchedule = scheduleRepository.save(schedule);
        
        log.info("Schedule status updated");
        return mapToDTO(updatedSchedule);
    }

    // *** NEW: Cleanup old schedules ***
    @Override
    @Transactional
    public int cleanupOldSchedules(int daysBack) {
        log.info("Cleaning up schedules older than {} days", daysBack);
        
        LocalDate cutoffDate = LocalDate.now().minusDays(daysBack);
        scheduleRepository.deleteSchedulesBeforeDate(cutoffDate);
        
        log.info("Old schedules cleaned up");
        return 0; // Return count if needed
    }

    // *** UPDATED: Helper method with dayName ***
    private DoctorScheduleDTO mapToDTO(DoctorSchedule schedule) {
        DoctorScheduleDTO dto = modelMapper.map(schedule, DoctorScheduleDTO.class);
        dto.setDoctorId(schedule.getDoctor().getDoctorId());
        dto.setScheduleDate(schedule.getScheduleDate());  // CHANGED
        dto.setDayName(schedule.getDayName());  // NEW
        dto.setTotalSlots(schedule.getTotalSlots());
        return dto;
    }
}