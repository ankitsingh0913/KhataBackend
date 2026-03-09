package com.XCLONE.KhataBackend.Repository;

import com.XCLONE.KhataBackend.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByCustomerId(UUID customerId);
    List<Payment> findByUserId(UUID userId);
}
