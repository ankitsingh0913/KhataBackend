package com.XCLONE.KhataBackend.Controller;

import com.XCLONE.KhataBackend.DTO.product.ProductRequestDTO;
import com.XCLONE.KhataBackend.DTO.product.ProductResponseDTO;
import com.XCLONE.KhataBackend.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO dto) {

        UUID userId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(productService.create(dto, userId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {

        UUID userId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(productService.getAll(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequestDTO dto) {

        UUID userId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(productService.update(id, dto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        UUID userId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        productService.delete(id, userId);

        return ResponseEntity.ok("Product deleted");
    }
}
