package com.hospital.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RevenueReportDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalBilled;
    private BigDecimal totalCollected;
    private BigDecimal totalOutstanding;
    private BigDecimal totalDiscount;
    private BigDecimal totalTax;
    private Integer totalInvoices;
    private Integer paidInvoices;
    private Integer unpaidInvoices;
    private Integer partiallyPaidInvoices;
    private Map<String, BigDecimal> paymentModeWiseCollection;
    private Map<String, BigDecimal> invoiceTypeWiseRevenue;
    private List<InvoiceDTO> invoiceList;
}
