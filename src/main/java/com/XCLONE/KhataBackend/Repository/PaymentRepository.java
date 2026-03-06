package com.XCLONE.KhataBackend.Repository;

import com.XCLONE.KhataBackend.Entity.Payment;
import com.XCLONE.KhataBackend.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findCustomerId(UUID customerId);
}
