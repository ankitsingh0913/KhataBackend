package com.XCLONE.KhataBackend.Controller;

import com.XCLONE.KhataBackend.DTO.bill.BillCreateRequestDTO;
import com.XCLONE.KhataBackend.DTO.bill.BillResponseDTO;
import com.XCLONE.KhataBackend.Service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // ================= CREATE BILL =================

    @PostMapping
    public ResponseEntity<BillResponseDTO> createBill(
            @Valid @RequestBody BillCreateRequestDTO request) {

        BillResponseDTO response =
                billService.createBill(request, getCurrentUserId());

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL BILLS =================

    @GetMapping
    public ResponseEntity<List<BillResponseDTO>> getAllBills() {

        return ResponseEntity.ok(
                billService.getAllBills(getCurrentUserId()));
    }

    // ================= GET BILL BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<BillResponseDTO> getBillById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                billService.getBillById(id, getCurrentUserId()));
    }
}