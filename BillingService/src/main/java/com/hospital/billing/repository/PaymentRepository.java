/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.repository;

import com.hospital.billing.entity.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mduhijod
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);

    List<Payment> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT p.paymentId FROM Payment p ORDER BY p.id DESC")
    List<String> findTopByOrderByIdDesc();

    /**
     * Fetch all payments whose paymentDate falls in the given range.
     * Uses LocalDateTime so callers should pass start-of-day and end-of-day.
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentDate >= :startDateTime AND p.paymentDate <= :endDateTime ORDER BY p.paymentDate DESC")
    List<Payment> findByPaymentDateRange(
            @org.springframework.data.repository.query.Param("startDateTime") java.time.LocalDateTime startDateTime,
            @org.springframework.data.repository.query.Param("endDateTime") java.time.LocalDateTime endDateTime);

    /**
     * Fetch all payments for a specific doctor in a date range.
     */
    @Query("SELECT p FROM Payment p WHERE p.invoice.doctorId = :doctorId AND p.paymentDate >= :startDateTime AND p.paymentDate <= :endDateTime ORDER BY p.paymentDate DESC")
    List<Payment> findByDoctorIdAndPaymentDateRange(
            @org.springframework.data.repository.query.Param("doctorId") String doctorId,
            @org.springframework.data.repository.query.Param("startDateTime") java.time.LocalDateTime startDateTime,
            @org.springframework.data.repository.query.Param("endDateTime") java.time.LocalDateTime endDateTime);
}
