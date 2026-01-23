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
        
        invoice.addPayment(payment);
        invoiceRepository.save(invoice);
        paymentRepository.save(payment);
        
        log.info("Payment processed: {}", paymentId);
        
        PaymentDTO response = new PaymentDTO();
        response.setPaymentId(paymentId);
        response.setInvoiceNumber(invoiceNumber);
        response.setAmount(dto.getAmount());
        response.setPaymentMode(dto.getPaymentMode());
        response.setPaymentDate(payment.getPaymentDate());
        return response;
    }
    
    public List<PaymentDTO> getInvoicePayments(String invoiceNumber) {
        return paymentRepository.findByInvoiceNumber(invoiceNumber)
            .stream().map(p -> {
                PaymentDTO dto = new PaymentDTO();
                dto.setPaymentId(p.getPaymentId());
                dto.setAmount(p.getAmount());
                dto.setPaymentMode(p.getPaymentMode().name());
                dto.setPaymentDate(p.getPaymentDate());
                return dto;
            }).collect(Collectors.toList());
    }
    
    private String generatePaymentId() {
        List<String> ids = paymentRepository.findTopByOrderByIdDesc();
        int nextNum = ids.isEmpty() ? 1 : Integer.parseInt(ids.get(0).substring(3)) + 1;
        return String.format("PAY%010d", nextNum);
    }
}
