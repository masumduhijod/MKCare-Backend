/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.service;

import com.hospital.billing.dto.PaymentDTO;
import com.hospital.billing.entity.Invoice;
import com.hospital.billing.entity.Payment;
import com.hospital.billing.repository.InvoiceRepository;
import com.hospital.billing.repository.PaymentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mduhijod
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentDTO processPayment(String invoiceNumber, PaymentDTO dto) {
        log.info("Processing payment for invoice: {}", invoiceNumber);

        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        String paymentId = generatePaymentId();

        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setInvoice(invoice);
        payment.setInvoiceNumber(invoiceNumber);
        payment.setAmount(dto.getAmount());
        payment.setPaymentMode(Payment.PaymentMode.valueOf(dto.getPaymentMode()));
        payment.setTransactionId(dto.getTransactionId());
        payment.setReceivedBy(dto.getReceivedBy());
        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
invoice.addPayment(payment);

// calculate total paid amount
double totalPaid = invoice.getPayments()
    .stream()
    .mapToDouble(p -> p.getAmount().doubleValue())
    .sum();

double totalAmount = invoice.getTotalAmount().doubleValue();

if (totalPaid >= totalAmount) {
        invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
} else {
    invoice.setPaymentStatus(Invoice.PaymentStatus.PENDING);
}

        invoiceRepository.save(invoice); // then save invoice

        log.info("Payment processed: {}", paymentId);

        PaymentDTO response = new PaymentDTO();
        response.setPaymentId(paymentId);
        response.setInvoiceNumber(invoiceNumber);
        response.setAmount(dto.getAmount());
        response.setPaymentMode(dto.getPaymentMode());
        response.setPaymentDate(payment.getPaymentDate());
        response.setTransactionId(payment.getTransactionId());
        response.setReceivedBy(payment.getReceivedBy());
        return response;
    }

    public List<PaymentDTO> getInvoicePayments(String invoiceNumber) {
        return paymentRepository.findByInvoiceNumber(invoiceNumber)
                .stream().map(p -> {
                    PaymentDTO dto = new PaymentDTO();
                    dto.setPaymentId(p.getPaymentId());
                    dto.setInvoiceNumber(p.getInvoiceNumber());
                    dto.setAmount(p.getAmount());
                    dto.setPaymentMode(p.getPaymentMode().name());
                    dto.setTransactionId(p.getTransactionId());
                    dto.setPaymentDate(p.getPaymentDate());
                    dto.setReceivedBy(p.getReceivedBy());
                    // Enrich with patient PIN from invoice
                    if (p.getInvoice() != null) {
                        dto.setPinNumber(p.getInvoice().getPinNumber());
                        dto.setDoctorId(p.getInvoice().getDoctorId());
                    }
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByDateRange(String fromDate, String toDate) {
        java.time.LocalDateTime start = java.time.LocalDate.parse(fromDate).atStartOfDay();
        java.time.LocalDateTime end = java.time.LocalDate.parse(toDate).atTime(23, 59, 59);
        return paymentRepository.findByPaymentDateRange(start, end)
                .stream().map(p -> {
                    PaymentDTO dto = new PaymentDTO();
                    dto.setPaymentId(p.getPaymentId());
                    dto.setInvoiceNumber(p.getInvoiceNumber());
                    dto.setAmount(p.getAmount());
                    dto.setPaymentMode(p.getPaymentMode().name());
                    dto.setTransactionId(p.getTransactionId());
                    dto.setPaymentDate(p.getPaymentDate());
                    dto.setReceivedBy(p.getReceivedBy());
                    if (p.getInvoice() != null) {
                        dto.setPinNumber(p.getInvoice().getPinNumber());
                        dto.setDoctorId(p.getInvoice().getDoctorId());
                    }
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByDoctorAndDateRange(String doctorId, String fromDate, String toDate) {
        java.time.LocalDateTime start = java.time.LocalDate.parse(fromDate).atStartOfDay();
        java.time.LocalDateTime end = java.time.LocalDate.parse(toDate).atTime(23, 59, 59);
        return paymentRepository.findByDoctorIdAndPaymentDateRange(doctorId, start, end)
                .stream().map(p -> {
                    PaymentDTO dto = new PaymentDTO();
                    dto.setPaymentId(p.getPaymentId());
                    dto.setInvoiceNumber(p.getInvoiceNumber());
                    dto.setAmount(p.getAmount());
                    dto.setPaymentMode(p.getPaymentMode().name());
                    dto.setTransactionId(p.getTransactionId());
                    dto.setPaymentDate(p.getPaymentDate());
                    dto.setReceivedBy(p.getReceivedBy());
                    if (p.getInvoice() != null) {
                        dto.setPinNumber(p.getInvoice().getPinNumber());
                        dto.setDoctorId(p.getInvoice().getDoctorId());
                    }
                    return dto;
                }).collect(Collectors.toList());
    }

    private String generatePaymentId() {
        List<String> ids = paymentRepository.findTopByOrderByIdDesc();
        int nextNum = 1;
        if (!ids.isEmpty()) {
            try {
                nextNum = Integer.parseInt(ids.get(0).substring(3)) + 1;
            } catch (NumberFormatException e) {
                log.warn("Failed to parse paymentId, using fallback numbering", e);
            }
        }
        long timestamp = System.currentTimeMillis() % 100000; // last 5 digits
        return String.format("PAY%010d%05d", nextNum, timestamp);
    }


public List<PaymentDTO> getPaymentHistory(
        String pin,
        String doctorId,
        String date) {

    List<Invoice> invoices = new ArrayList<>();

    // 🟢 PATIENT HISTORY
    if (pin != null && !pin.trim().isEmpty()) {
        invoices = invoiceRepository
                .findByPinNumberOrderByInvoiceDateDesc(pin);
    }

    // 🟢 DATE HISTORY (ignore doctorId completely)
   // 🟢 DOCTOR + DATE HISTORY
else if (doctorId != null && !doctorId.trim().isEmpty()
      && date != null && !date.trim().isEmpty()) {

    invoices = invoiceRepository
            .findInvoicesByDoctorAndDate(doctorId, date);

    log.info("Doctor invoices found: {}", invoices.size());
}
    // 🔵 Collect payments from invoices
    List<PaymentDTO> result = new ArrayList<>();

    for (Invoice inv : invoices) {

        if (inv.getPayments() == null) continue;

        for (Payment p : inv.getPayments()) {

            PaymentDTO dto = new PaymentDTO();
            dto.setPaymentId(p.getPaymentId());
            dto.setInvoiceNumber(p.getInvoiceNumber());
            dto.setAmount(p.getAmount());
            dto.setPaymentMode(p.getPaymentMode().name());
            dto.setTransactionId(p.getTransactionId());
            dto.setPaymentDate(p.getPaymentDate());
            dto.setReceivedBy(p.getReceivedBy());
                     dto.setPaymentStatus(inv.getPaymentStatus() != null ? inv.getPaymentStatus().name() : "UNKNOWN");

            result.add(dto);
        }
    }

    log.info("Total payments returned: {}", result.size());

    return result;
}



}
