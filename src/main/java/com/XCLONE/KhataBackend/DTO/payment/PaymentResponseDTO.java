package com.XCLONE.KhataBackend.DTO.payment;

import com.XCLONE.KhataBackend.enums.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PaymentResponseDTO {

    private UUID id;
    private UUID customerId;
    private UUID billId;
    private BigDecimal amount;
    private PaymentType paymentType;
    private Instant createdAt;
}
