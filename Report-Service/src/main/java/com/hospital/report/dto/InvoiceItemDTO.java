package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceItemDTO {
    private String itemName;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String itemType;
}
