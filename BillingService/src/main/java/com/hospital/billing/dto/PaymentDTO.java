/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.dto;

//import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String paymentId;
    private String invoiceNumber;
    // @NotNull
    private BigDecimal amount;
    // @NotBlank
    private String paymentMode;
    private String transactionId;
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;
    private String receivedBy;
    // Enriched fields from Invoice
    private String pinNumber;
    private String doctorId;
     private String paymentStatus; // PAID / PENDING

}
