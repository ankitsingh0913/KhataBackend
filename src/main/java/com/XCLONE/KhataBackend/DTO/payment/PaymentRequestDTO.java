package com.XCLONE.KhataBackend.DTO.payment;

import com.XCLONE.KhataBackend.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequestDTO {

    @NotNull
    private UUID customerId;

    private UUID billId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull
    private PaymentType paymentType;

    private String notes;
}