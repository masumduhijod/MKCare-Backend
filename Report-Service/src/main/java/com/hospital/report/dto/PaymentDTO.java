package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {
    private String paymentId;
    private String invoiceNumber;
    private BigDecimal amount;
    private String paymentMode;
    private String transactionId;
    private LocalDateTime paymentDate;  // handled by global FlexibleLocalDateTimeDeserializer
    private String receivedBy;
    // Enriched fields from Invoice (populated by BillingService)
    private String pinNumber;
    private String doctorId;
}