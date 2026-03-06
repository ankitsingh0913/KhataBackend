package com.XCLONE.KhataBackend.Service;

import com.XCLONE.KhataBackend.DTO.payment.PaymentRequestDTO;
import com.XCLONE.KhataBackend.DTO.payment.PaymentResponseDTO;

import java.util.UUID;

public interface PaymentService {
    PaymentResponseDTO recordPayment(PaymentRequestDTO request, UUID userId);
}
