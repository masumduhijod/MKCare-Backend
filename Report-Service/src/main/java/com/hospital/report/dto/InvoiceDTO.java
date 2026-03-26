package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceDTO {
    private String invoiceNumber;
    private String pinNumber;
    private String patientName;
    private String appointmentId;
    private String cvrNumber;
    private String invoiceType;
    private LocalDate invoiceDate;
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
    private String paymentStatus;
    private String doctorId;
    private List<InvoiceItemDTO> items;
}
