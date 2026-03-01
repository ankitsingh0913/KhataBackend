package com.XCLONE.KhataBackend.ServiceImpl;

import com.XCLONE.KhataBackend.DTO.product.ProductRequestDTO;
import com.XCLONE.KhataBackend.DTO.product.ProductResponseDTO;
import com.XCLONE.KhataBackend.Entity.Product;
import com.XCLONE.KhataBackend.Repository.ProductRepository;
import com.XCLONE.KhataBackend.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponseDTO create(ProductRequestDTO dto, UUID userId) {

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .userId(userId)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public List<ProductResponseDTO> getAll(UUID userId) {
        return productRepository.findByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponseDTO update(UUID id, ProductRequestDTO dto, UUID userId) {

        Product product = productRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setUpdatedAt(Instant.now());

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public void delete(UUID id, UUID userId) {

        Product product = productRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsActive(false);
        product.setUpdatedAt(Instant.now());

        productRepository.save(product);
    }

    private ProductResponseDTO mapToResponse(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}