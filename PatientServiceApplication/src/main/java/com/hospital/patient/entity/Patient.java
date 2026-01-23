/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.entity;

/**
 *
 * @author mduhijod
 */


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "pin_number", unique = true, nullable = false, length = 20)
    private String pinNumber;

//    @NotNull(message = "First name is required")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

//    @NotNull(message = "Date of birth is required")
//    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "age")
    private Integer age;

//    @NotNull(message = "Gender is required")
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

//    @NotNull(message = "Contact number is required")
//    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    @Column(name = "contact_number", nullable = false, length = 15)
    private String contactNumber;

    @Column(name = "alternate_contact", length = 15)
    private String alternateContact;

//    @Email(message = "Invalid email format")
    @Column(name = "email", length = 100)
    private String email;

//    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar number must be 12 digits")
    @Column(name = "aadhar_number", unique = true, length = 12)
    private String aadharNumber;

    // Address Details
    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pincode", length = 10)
    private String pincode;

    // Emergency Contact
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_number", length = 15)
    private String emergencyContactNumber;

    @Column(name = "emergency_contact_relation", length = 50)
    private String emergencyContactRelation;

    // Insurance Details
    @Column(name = "insurance_provider", length = 100)
    private String insuranceProvider;

    @Column(name = "insurance_id", length = 50)
    private String insuranceId;

    @Column(name = "insurance_expiry_date")
    private LocalDate insuranceExpiryDate;

    // System Fields
    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Column(name = "registered_by", length = 100)
    private String registeredBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PatientStatus status = PatientStatus.ACTIVE;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private PatientMedicalHistory medicalHistory;

    // Enums
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum PatientStatus {
        ACTIVE, INACTIVE, DECEASED
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        registrationDate = LocalDateTime.now();
        updateAge();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateAge();
    }

    // Calculate age from date of birth
    private void updateAge() {
        if (dateOfBirth != null) {
            age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        }
    }

    // Helper method to get full name
    @Transient
    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }

    // Helper method to get full address
    @Transient
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null) address.append(addressLine1).append(", ");
        if (addressLine2 != null) address.append(addressLine2).append(", ");
        if (city != null) address.append(city).append(", ");
        if (state != null) address.append(state).append(" - ");
        if (pincode != null) address.append(pincode);
        return address.toString();
    }
}
