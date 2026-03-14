package com.XCLONE.KhataBackend.DTO.product;

import com.XCLONE.KhataBackend.enums.UnitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    @NotBlank
    private String name;

    private String category;

    private String barcode;

    private String description;

    @NotNull
    @Positive
    private BigDecimal purchasePrice;

    @NotNull
    @Positive
    private BigDecimal sellingPrice;

    @PositiveOrZero
    private Integer stock;

    @NotNull
    private UnitType unit;

    @Positive
    private Integer lowStockAlert;
}
