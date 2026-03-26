/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.service;

/**
 *
 * @author mduhijod
 */
import com.hospital.billing.dto.*;
import com.hospital.billing.entity.*;
import com.hospital.billing.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper modelMapper;
    
    public InvoiceDTO createInvoice(CreateInvoiceDTO dto) {
        log.info("Creating invoice for PIN: {}", dto.getPinNumber());
        System.out.println("Items size: " + dto.getItems().size());
System.out.println("DTO Data: " + dto);
        String invoiceNumber = generateInvoiceNumber();
        
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setPinNumber(dto.getPinNumber());
        invoice.setPatientId(1L); // TODO: Fetch from patient service
        invoice.setAppointmentId(dto.getAppointmentId());
        invoice.setCvrNumber(dto.getCvrNumber());
        invoice.setDoctorId(dto.getDoctorId());
        invoice.setInvoiceType(Invoice.InvoiceType.valueOf(dto.getInvoiceType()));
        invoice.setDiscountPercentage(dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : BigDecimal.ZERO);
        invoice.setTaxPercentage(dto.getTaxPercentage() != null ? dto.getTaxPercentage() : BigDecimal.ZERO);
        invoice.setIsInsuranceClaim(dto.getIsInsuranceClaim());
        invoice.setInsuranceProvider(dto.getInsuranceProvider());
        invoice.setCreatedBy(dto.getCreatedBy());
        
        for (InvoiceItemDTO itemDTO : dto.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setItemName(itemDTO.getItemName());
            item.setDescription(itemDTO.getDescription());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setItemType(itemDTO.getItemType() != null 
                ? InvoiceItem.ItemType.valueOf(itemDTO.getItemType()) 
                : InvoiceItem.ItemType.SERVICE);
            invoice.addItem(item);
        }
        
        invoice.calculateTotals();
        // ✅ IMPORTANT: initialize payment fields correctly
invoice.setPaidAmount(BigDecimal.ZERO);
invoice.setOutstandingAmount(invoice.getTotalAmount());
invoice.setPaymentStatus(Invoice.PaymentStatus.PENDING);
        Invoice saved = invoiceRepository.save(invoice);
        
        log.info("Invoice created: {}", invoiceNumber);
        return mapToDTO(saved);
    }
    
    public InvoiceDTO getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return mapToDTO(invoice);
    }
    
    public List<InvoiceDTO> getPatientInvoices(String pinNumber) {
        return invoiceRepository.findByPinNumberOrderByInvoiceDateDesc(pinNumber)
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    
    public List<InvoiceDTO> getPendingInvoices() {
        return invoiceRepository.findByPaymentStatus(Invoice.PaymentStatus.PENDING)
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    
    private String generateInvoiceNumber() {
        List<String> ids = invoiceRepository.findTopByOrderByIdDesc();
        int nextNum = ids.isEmpty() ? 1 : Integer.parseInt(ids.get(0).substring(3)) + 1;
        return String.format("INV%010d", nextNum);
    }
    
    private InvoiceDTO mapToDTO(Invoice invoice) {
        InvoiceDTO dto = modelMapper.map(invoice, InvoiceDTO.class);
        dto.setItems(invoice.getItems().stream()
            .map(i -> modelMapper.map(i, InvoiceItemDTO.class))
            .collect(Collectors.toList()));
        return dto;
    }
 public List<InvoiceDTO> getInvoicesByDoctorAndDate(String doctorId, String date) {

    List<Invoice> invoices =
            invoiceRepository.findByDoctorIdAndInvoiceDate(
                    doctorId,
                    java.time.LocalDate.parse(date)
            );

    return invoices.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
}


}

