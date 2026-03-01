package com.XCLONE.KhataBackend.Controller;

import com.XCLONE.KhataBackend.DTO.customer.CustomerRequestDTO;
import com.XCLONE.KhataBackend.DTO.customer.CustomerResponseDTO;
import com.XCLONE.KhataBackend.Service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(
            @Valid @RequestBody CustomerRequestDTO dto) {

        return ResponseEntity.ok(
                customerService.create(dto, getCurrentUserId()));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {

        return ResponseEntity.ok(
                customerService.getAll(getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                customerService.getById(id, getCurrentUserId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequestDTO dto) {

        return ResponseEntity.ok(
                customerService.update(id, dto, getCurrentUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        customerService.delete(id, getCurrentUserId());
        return ResponseEntity.ok("Customer deleted");
    }
}