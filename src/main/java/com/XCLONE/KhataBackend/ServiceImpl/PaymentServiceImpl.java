package com.XCLONE.KhataBackend.ServiceImpl;

import com.XCLONE.KhataBackend.DTO.payment.PaymentRequestDTO;
import com.XCLONE.KhataBackend.DTO.payment.PaymentResponseDTO;
import com.XCLONE.KhataBackend.Entity.Customer;
import com.XCLONE.KhataBackend.Entity.Payment;
import com.XCLONE.KhataBackend.Repository.CustomerRepository;
import com.XCLONE.KhataBackend.Repository.PaymentRepository;
import com.XCLONE.KhataBackend.Service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    @Override
    public PaymentResponseDTO recordPayment(PaymentRequestDTO request, UUID userId) {

        Customer customer = customerRepository
                .findByIdAndUserId(request.getCustomerId(), userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (request.getAmount().compareTo(customer.getPendingAmount()) > 0) {
            throw new RuntimeException("Payment exceeds pending amount");
        }

        customer.setPendingAmount(
                customer.getPendingAmount().subtract(request.getAmount())
        );

        customerRepository.save(customer);

        Payment payment = Payment.builder()
                .userId(userId)
                .customerId(customer.getId())
                .billId(request.getBillId())
                .amount(request.getAmount())
                .paymentType(request.getPaymentType())
                .notes(request.getNotes())
                .createdAt(Instant.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        return PaymentResponseDTO.builder()
                .id(saved.getId())
                .customerId(saved.getCustomerId())
                .billId(saved.getBillId())
                .amount(saved.getAmount())
                .paymentType(saved.getPaymentType())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}