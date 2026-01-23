/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.entity;

/**
 *
 * @author mduhijod
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

//@Entity
//@Table(name = "doctor_schedules", 
//       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "day_of_week"}))
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class DoctorSchedule {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "schedule_id")
//    private Long scheduleId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "doctor_id", nullable = false)
//    @JsonIgnore
//    private Doctor doctor;
//
//    @NotNull(message = "Day of week is required")
//    @Enumerated(EnumType.STRING)
//    @Column(name = "day_of_week", nullable = false)
//    private DayOfWeek dayOfWeek;
//
//    @NotNull(message = "Start time is required")
//    @Column(name = "start_time", nullable = false)
//    private LocalTime startTime;
//
//    @NotNull(message = "End time is required")
//    @Column(name = "end_time", nullable = false)
//    private LocalTime endTime;
//
//    @Column(name = "slot_duration_minutes")
//    private Integer slotDurationMinutes = 15; // Default 15 minutes per slot
//
//    @Column(name = "max_patients_per_slot")
//    private Integer maxPatientsPerSlot = 1; // Default 1 patient per slot
//
//    @Column(name = "is_active")
//    private Boolean isActive = true;
//
//    @Column(name = "break_start_time")
//    private LocalTime breakStartTime;
//
//    @Column(name = "break_end_time")
//    private LocalTime breakEndTime;
//
//    // Enum for days of week
//    public enum DayOfWeek {
//        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
//    }
//
//    // Helper method to check if schedule is active
//    @Transient
//    public boolean isActiveSchedule() {
//        return isActive != null && isActive;
//    }
//
//    // Calculate total slots
//    @Transient
//    public int getTotalSlots() {
//        if (startTime == null || endTime == null || slotDurationMinutes == null) {
//            return 0;
//        }
//        
//        long totalMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
//        
//        // Subtract break time if present
//        if (breakStartTime != null && breakEndTime != null) {
//            long breakMinutes = java.time.Duration.between(breakStartTime, breakEndTime).toMinutes();
//            totalMinutes -= breakMinutes;
//        }
//        
//        return (int) (totalMinutes / slotDurationMinutes);
//    }
//
//    // Check if time falls within schedule
//    @Transient
//    public boolean isWithinSchedule(LocalTime time) {
//        if (!isActiveSchedule()) {
//            return false;
//        }
//        
//        // Check if time is within main schedule
//        boolean withinMain = !time.isBefore(startTime) && !time.isAfter(endTime);
//        
//        if (!withinMain) {
//            return false;
//        }
//        
//        // Check if time falls in break
//        if (breakStartTime != null && breakEndTime != null) {
//            boolean duringBreak = !time.isBefore(breakStartTime) && !time.isAfter(breakEndTime);
//            return !duringBreak;
//        }
//        
//        return true;
//    }
//
//    // Validation
//    @PrePersist
//    @PreUpdate
//    private void validate() {
//        if (endTime.isBefore(startTime)) {
//            throw new IllegalStateException("End time cannot be before start time");
//        }
//        
//        if (breakStartTime != null && breakEndTime != null) {
//            if (breakEndTime.isBefore(breakStartTime)) {
//                throw new IllegalStateException("Break end time cannot be before break start time");
//            }
//            
//            if (breakStartTime.isBefore(startTime) || breakEndTime.isAfter(endTime)) {
//                throw new IllegalStateException("Break time must be within schedule time");
//            }
//        }
//    }
//}   

@Entity
@Table(name = "doctor_schedules", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "schedule_date"})) // CHANGED
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonIgnore
    private Doctor doctor;
    
    // *** MAIN CHANGE: Replace dayOfWeek with scheduleDate ***
//    @NotNull(message = "Schedule date is required")
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;  // CHANGED from DayOfWeek dayOfWeek
    
//    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
//    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "slot_duration_minutes")
    private Integer slotDurationMinutes = 15;
    
    @Column(name = "max_patients_per_slot")
    private Integer maxPatientsPerSlot = 1;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "break_start_time")
    private LocalTime breakStartTime;
    
    @Column(name = "break_end_time")
    private LocalTime breakEndTime;
    
    // *** REMOVED: DayOfWeek enum - no longer needed ***
    
    // *** NEW: Get day name from date ***
    @Transient
    public String getDayName() {
        if (scheduleDate == null) return null;
        return scheduleDate.getDayOfWeek().name();
    }
    
    // Helper method to check if schedule is active
    @Transient
    public boolean isActiveSchedule() {
        return isActive != null && isActive;
    }
    
    // Calculate total slots
    @Transient
    public int getTotalSlots() {
        if (startTime == null || endTime == null || slotDurationMinutes == null) {
            return 0;
        }
        
        long totalMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        
        if (breakStartTime != null && breakEndTime != null) {
            long breakMinutes = java.time.Duration.between(breakStartTime, breakEndTime).toMinutes();
            totalMinutes -= breakMinutes;
        }
        
        return (int) (totalMinutes / slotDurationMinutes);
    }
    
    // Check if time falls within schedule
    @Transient
    public boolean isWithinSchedule(LocalTime time) {
        if (!isActiveSchedule()) {
            return false;
        }
        
        boolean withinMain = !time.isBefore(startTime) && !time.isAfter(endTime);
        
        if (!withinMain) {
            return false;
        }
        
        if (breakStartTime != null && breakEndTime != null) {
            boolean duringBreak = !time.isBefore(breakStartTime) && !time.isAfter(breakEndTime);
            return !duringBreak;
        }
        
        return true;
    }
    
    // Validation
    @PrePersist
    @PreUpdate
    private void validate() {
        if (endTime.isBefore(startTime)) {
            throw new IllegalStateException("End time cannot be before start time");
        }
        
        if (breakStartTime != null && breakEndTime != null) {
            if (breakEndTime.isBefore(breakStartTime)) {
                throw new IllegalStateException("Break end time cannot be before break start time");
            }
            
            if (breakStartTime.isBefore(startTime) || breakEndTime.isAfter(endTime)) {
                throw new IllegalStateException("Break time must be within schedule time");
            }
        }
        
        // *** NEW: Validate schedule date is not in past ***
        if (scheduleDate != null && scheduleDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Schedule date cannot be in the past");
        }
    }
}