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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "doctor_id", unique = true, nullable = false, length = 20)
    private String doctorId;

    @Column(name = "user_id")
    private Long userId; // Link to user service

    // Personal Information
//    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

//    @NotBlank(message = "Specialization is required")
    @Column(name = "specialization", nullable = false, length = 100)
    private String specialization;

    @Column(name = "qualification", length = 255)
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    // Department
    @Column(name = "department", length = 100)
    private String department;

    // Contact Details
//    @NotBlank(message = "Contact number is required")
//    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    @Column(name = "contact_number", nullable = false, length = 15)
    private String contactNumber;

//    @Email(message = "Invalid email format")
    @Column(name = "email", length = 100)
    private String email;

    // Professional Details
    @Column(name = "license_number", unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    // Consultation Fee
//    @NotNull(message = "Consultation fee is required")
//    @DecimalMin(value = "0.0", message = "Fee must be positive")
    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(name = "follow_up_fee", precision = 10, scale = 2)
    private BigDecimal followUpFee;

    @Column(name = "follow_up_days_limit")
    private Integer followUpDaysLimit = 7; // Default 7 days

    // Availability Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DoctorStatus status = DoctorStatus.AVAILABLE;

    @Column(name = "available_for_opd")
    private Boolean availableForOPD = true;

    @Column(name = "available_for_emergency")
    private Boolean availableForEmergency = false;

    // Additional Information
    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "languages_spoken")
    private String languagesSpoken; // Comma-separated: "English, Hindi, Marathi"

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    // System Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    // Relationships
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorSchedule> schedules = new ArrayList<>();

    // Enums
    public enum DoctorStatus {
        AVAILABLE,
        ON_LEAVE,
        BUSY,
        INACTIVE
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (followUpFee == null && consultationFee != null) {
            followUpFee = consultationFee.multiply(new BigDecimal("0.5")); // 50% of consultation fee
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods
    @Transient
    public String getFullName() {
        return "Dr. " + firstName + (lastName != null ? " " + lastName : "");
    }

    @Transient
    public boolean isAvailable() {
        return status == DoctorStatus.AVAILABLE;
    }

    public void addSchedule(DoctorSchedule schedule) {
        schedules.add(schedule);
        schedule.setDoctor(this);
    }

    public void removeSchedule(DoctorSchedule schedule) {
        schedules.remove(schedule);
        schedule.setDoctor(null);
    }

    // Mark as on leave
    public void markOnLeave() {
        this.status = DoctorStatus.ON_LEAVE;
        this.availableForOPD = false;
    }

    // Mark as available
    public void markAvailable() {
        this.status = DoctorStatus.AVAILABLE;
        this.availableForOPD = true;
    }
}
