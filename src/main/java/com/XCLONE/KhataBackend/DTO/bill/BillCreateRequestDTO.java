package com.XCLONE.KhataBackend.DTO.bill;

import com.XCLONE.KhataBackend.DTO.billItem.BillItemRequestDTO;
import com.XCLONE.KhataBackend.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class BillCreateRequestDTO {

    private UUID customerId;

    @NotEmpty(message = "Bill must contain at least one item")
    private List<BillItemRequestDTO> items;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal tax = BigDecimal.ZERO;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    private String notes;
}