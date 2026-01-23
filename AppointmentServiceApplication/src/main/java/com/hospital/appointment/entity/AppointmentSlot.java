/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointment_slots",
       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "slot_date", "slot_time"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long slotId;

    // *** NEW: Link to the schedule that generated this slot ***
    @Column(name = "schedule_id")
    private Long scheduleId;

//    @NotBlank(message = "Doctor ID is required")
    @Column(name = "doctor_id", nullable = false, length = 20)
    private String doctorId;

//    @NotNull(message = "Slot date is required")
    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

//    @NotNull(message = "Slot time is required")
    @Column(name = "slot_time", nullable = false)
    private LocalTime slotTime;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "max_patients")
    private Integer maxPatients = 1;

    @Column(name = "booked_count")
    private Integer bookedCount = 0;

    @Column(name = "appointment_id", length = 20)
    private String appointmentId;

    // Helper Methods
    @Transient
    public boolean canBook() {
        return isAvailable && bookedCount < maxPatients;
    }

    public void book(String appointmentId) {
        this.bookedCount++;
        if (this.bookedCount >= this.maxPatients) {
            this.isAvailable = false;
        }
        // Store only last appointment ID if single patient per slot
        if (this.maxPatients == 1) {
            this.appointmentId = appointmentId;
        }
    }

    public void release() {
        this.bookedCount = Math.max(0, this.bookedCount - 1);
        if (this.bookedCount < this.maxPatients) {
            this.isAvailable = true;
        }
        if (this.bookedCount == 0) {
            this.appointmentId = null;
        }
    }

    public void markUnavailable() {
        this.isAvailable = false;
    }

    public void markAvailable() {
        if (this.bookedCount < this.maxPatients) {
            this.isAvailable = true;
        }
    }

    @Transient
    public int getAvailableCapacity() {
        return Math.max(0, maxPatients - bookedCount);
    }
}