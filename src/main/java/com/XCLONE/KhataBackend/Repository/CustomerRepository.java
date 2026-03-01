package com.XCLONE.KhataBackend.Repository;

import com.XCLONE.KhataBackend.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findByUserId(UUID userId);

    Optional<Customer> findByIdAndUserId(UUID id, UUID userId);

    Optional<Customer> findByUserIdAndPhone(UUID userId, String phone);

    boolean existsByUserIdAndPhone(UUID userId, String phone);
}
