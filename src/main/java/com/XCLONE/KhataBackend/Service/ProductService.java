package com.XCLONE.KhataBackend.Service;

import com.XCLONE.KhataBackend.DTO.product.ProductRequestDTO;
import com.XCLONE.KhataBackend.DTO.product.ProductResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponseDTO create(ProductRequestDTO dto, UUID userId);

    List<ProductResponseDTO> getAll(UUID userId);

    ProductResponseDTO update(UUID id, ProductRequestDTO dto, UUID userId);

    void delete(UUID id, UUID userId);
}
