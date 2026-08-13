package com.hospital.opd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @Column(name = "generic_name")
    private String genericName;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "composition")
    private String composition;

    @Column(name = "medicine_type")
    private String medicineType; // Tablet, Capsule, Syrup, etc.

    @Column(name = "strength")
    private String strength;

    @Column(name = "unit")
    private String unit; // mg, ml, %

    @Column(name = "packaging")
    private String packaging; // Strip, Bottle, Tube

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;
}
