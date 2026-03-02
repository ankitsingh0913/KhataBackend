package com.XCLONE.KhataBackend.Repository;

import com.XCLONE.KhataBackend.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Bill> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByBillNumber(String billNumber);
}
