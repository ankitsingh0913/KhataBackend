package com.XCLONE.KhataBackend.DTO.bill;

import com.XCLONE.KhataBackend.DTO.billItem.BillItemResponseDTO;
import com.XCLONE.KhataBackend.enums.BillStatus;
import com.XCLONE.KhataBackend.enums.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BillResponseDTO {

    private UUID id;
    private String billNumber;

    private UUID customerId;
    private String customerName;
    private String customerPhone;

    private List<BillItemResponseDTO> items;

    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private BigDecimal paidAmount;

    private PaymentType paymentType;
    private BillStatus status;

    private String notes;

    private Instant createdAt;
    private Instant updatedAt;
}