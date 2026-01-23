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

import com.hospital.doctor.dto.AvailabilityDTO;
import com.hospital.doctor.dto.DoctorScheduleDTO;
import java.time.LocalDate;

import java.util.List;

//public interface DoctorScheduleService {
//
//    /**
//     * Add schedule for doctor
//     * @param doctorId - Doctor ID
//     * @param scheduleDTO - Schedule details
//     * @return Created schedule
//     */
//    DoctorScheduleDTO addSchedule(String doctorId, DoctorScheduleDTO scheduleDTO);
//
//    /**
//     * Update schedule
//     * @param scheduleId - Schedule ID
//     * @param scheduleDTO - Updated schedule
//     * @return Updated schedule
//     */
//    DoctorScheduleDTO updateSchedule(Long scheduleId, DoctorScheduleDTO scheduleDTO);
//
//    /**
//     * Delete schedule
//     * @param scheduleId - Schedule ID
//     * @return Success message
//     */
//    String deleteSchedule(Long scheduleId);
//
//    /**
//     * Get doctor schedules
//     * @param doctorId - Doctor ID
//     * @return List of schedules
//     */
//    List<DoctorScheduleDTO> getDoctorSchedules(String doctorId);
//
//    /**
//     * Get active schedules for doctor
//     * @param doctorId - Doctor ID
//     * @return List of active schedules
//     */
//    List<DoctorScheduleDTO> getActiveDoctorSchedules(String doctorId);
//
//    /**
//     * Get schedule by doctor and day
//     * @param doctorId - Doctor ID
//     * @param dayOfWeek - Day name
//     * @return Schedule details
//     */
//    DoctorScheduleDTO getScheduleByDoctorAndDay(String doctorId, String dayOfWeek);
//
//    /**
//     * Check doctor availability for specific day
//     * @param doctorId - Doctor ID
//     * @param dayOfWeek - Day name
//     * @return Availability details
//     */
//    AvailabilityDTO checkAvailability(String doctorId, String dayOfWeek);
//
//    /**
//     * Get all schedules for a specific day
//     * @param dayOfWeek - Day name
//     * @return List of schedules
//     */
//    List<DoctorScheduleDTO> getSchedulesByDay(String dayOfWeek);
//
//    /**
//     * Enable/Disable schedule
//     * @param scheduleId - Schedule ID
//     * @param isActive - Active status
//     * @return Updated schedule
//     */
//    DoctorScheduleDTO toggleScheduleStatus(Long scheduleId, boolean isActive);
//}


public interface DoctorScheduleService {
    
    /**
     * Add schedule for doctor on specific date
     */
    DoctorScheduleDTO addSchedule(String doctorId, DoctorScheduleDTO scheduleDTO);
    
    /**
     * Update schedule
     */
    DoctorScheduleDTO updateSchedule(Long scheduleId, DoctorScheduleDTO scheduleDTO);
    
    /**
     * Delete schedule
     */
    String deleteSchedule(Long scheduleId);
    
    /**
     * Get all schedules for doctor
     */
    List<DoctorScheduleDTO> getDoctorSchedules(String doctorId);
    
    /**
     * Get active schedules for doctor
     */
    List<DoctorScheduleDTO> getActiveDoctorSchedules(String doctorId);
    
    /**
     * *** CHANGED: Get schedule by doctor and DATE (not day) ***
     */
    DoctorScheduleDTO getScheduleByDoctorAndDate(String doctorId, LocalDate scheduleDate);
    
    /**
     * *** CHANGED: Check availability by DATE ***
     */
    AvailabilityDTO checkAvailability(String doctorId, LocalDate scheduleDate);
    
    /**
     * *** NEW: Get schedules in date range ***
     */
    List<DoctorScheduleDTO> getSchedulesByDateRange(
        String doctorId, LocalDate startDate, LocalDate endDate
    );
    
    /**
     * *** NEW: Get upcoming schedules ***
     */
    List<DoctorScheduleDTO> getUpcomingSchedules(String doctorId, int days);
    
    /**
     * Toggle schedule status
     */
    DoctorScheduleDTO toggleScheduleStatus(Long scheduleId, boolean isActive);
    
    /**
     * *** NEW: Cleanup old schedules ***
     */
    int cleanupOldSchedules(int daysBack);
    
    /**
     * *** REMOVED: getSchedulesByDay - no longer needed ***
     */
}