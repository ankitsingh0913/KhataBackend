package com.XCLONE.KhataBackend.Controller;

import com.XCLONE.KhataBackend.DTO.payment.PaymentRequestDTO;
import com.XCLONE.KhataBackend.DTO.payment.PaymentResponseDTO;
import com.XCLONE.KhataBackend.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> recordPayment(
            @Valid @RequestBody PaymentRequestDTO request) {

        return ResponseEntity.ok(
                paymentService.recordPayment(request, getCurrentUserId())
        );
    }
}