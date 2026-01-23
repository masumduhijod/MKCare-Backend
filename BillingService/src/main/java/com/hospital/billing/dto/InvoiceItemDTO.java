/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class InvoiceItemDTO {
    @NotBlank
    private String itemName;
    private String description;
    @NotNull
    private Integer quantity;
    @NotNull
    private BigDecimal unitPrice;
    private String itemType;
}
