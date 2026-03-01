package com.XCLONE.KhataBackend.DTO.customer;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CustomerResponseDTO {

    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;

    private BigDecimal totalPurchase;
    private BigDecimal pendingAmount;

    private Instant createdAt;
    private Instant updatedAt;
}