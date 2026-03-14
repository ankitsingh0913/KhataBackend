package com.XCLONE.KhataBackend.DTO.product;

import com.XCLONE.KhataBackend.enums.UnitType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ProductResponseDTO {

    private UUID id;
    private String name;
    private String barcode;
    private String category;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Integer stock;
    private UnitType unit;
    private Integer lowStockAlert;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}