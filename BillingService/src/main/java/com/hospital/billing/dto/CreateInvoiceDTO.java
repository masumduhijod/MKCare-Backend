/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.dto;

import java.math.BigDecimal;
import java.util.List;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotEmpty;
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
public class CreateInvoiceDTO {
//    @NotBlank
    private String pinNumber;
    private String appointmentId;
    private String cvrNumber;
    private String doctorId;
//    @NotBlank
    private String invoiceType;
    private BigDecimal discountPercentage;
    private BigDecimal taxPercentage;
    private Boolean isInsuranceClaim;
    private String insuranceProvider;
//    @NotEmpty
    private List<InvoiceItemDTO> items;
    private String createdBy;
}
