/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.entity;

/**
 *
 * @author mduhijod
 */
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// ========== Invoice Entity ==========
@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true, nullable = false, length = 20)
    private String invoiceNumber;

    @Column(name = "appointment_id", length = 20)
    private String appointmentId;

    @Column(name = "cvr_number", length = 20)
    private String cvrNumber;

//    @NotBlank
    @Column(name = "pin_number", nullable = false, length = 20)
    private String pinNumber;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "doctor_id", length = 20)
    private String doctorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false)
    private InvoiceType invoiceType = InvoiceType.OPD;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // Amounts
    @Column(name = "sub_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "outstanding_amount", precision = 10, scale = 2)
    private BigDecimal outstandingAmount = BigDecimal.ZERO;

    // Insurance
    @Column(name = "is_insurance_claim")
    private Boolean isInsuranceClaim = false;

    @Column(name = "insurance_provider")
    private String insuranceProvider;

    @Column(name = "insurance_claim_amount", precision = 10, scale = 2)
    private BigDecimal insuranceClaimAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    // Enums
    public enum InvoiceType {
        OPD, IPD, PHARMACY, LAB, EMERGENCY
    }

    public enum PaymentStatus {
        PENDING, PARTIAL, PAID, CANCELLED, REFUNDED
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (invoiceDate == null) invoiceDate = LocalDate.now();
        if (dueDate == null) dueDate = LocalDate.now();
    }

    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
    }

    public void calculateTotals() {
        subTotal = items.stream()
            .map(InvoiceItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = subTotal.multiply(discountPercentage).divide(new BigDecimal("100"));
        }

        BigDecimal afterDiscount = subTotal.subtract(discountAmount);

        if (taxPercentage.compareTo(BigDecimal.ZERO) > 0) {
            taxAmount = afterDiscount.multiply(taxPercentage).divide(new BigDecimal("100"));
        }

        totalAmount = afterDiscount.add(taxAmount);
        outstandingAmount = totalAmount.subtract(paidAmount);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        paidAmount = paidAmount.add(payment.getAmount());
        outstandingAmount = totalAmount.subtract(paidAmount);
        
        if (outstandingAmount.compareTo(BigDecimal.ZERO) == 0) {
            paymentStatus = PaymentStatus.PAID;
            paidAt = LocalDateTime.now();
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = PaymentStatus.PARTIAL;
        }
    }
}