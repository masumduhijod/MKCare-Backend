/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.repository;

/**
 *
 * @author mduhijod
 */
import com.hospital.billing.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.*;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByPinNumberOrderByInvoiceDateDesc(String pinNumber);
    List<Invoice> findByInvoiceDateBetween(LocalDate start, LocalDate end);
    List<Invoice> findByPaymentStatus(Invoice.PaymentStatus status);
    
    @Query("SELECT i.invoiceNumber FROM Invoice i ORDER BY i.id DESC")
    List<String> findTopByOrderByIdDesc();
}
