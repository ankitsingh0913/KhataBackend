package com.XCLONE.KhataBackend.Repository;

import com.XCLONE.KhataBackend.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findByUserId(UUID userId);

    Optional<Customer> findByIdAndUserId(UUID id, UUID userId);

    Optional<Customer> findByUserIdAndPhone(UUID userId, String phone);

    boolean existsByUserIdAndPhone(UUID userId, String phone);

    int countByUserId(UUID userId);

    @Query("""
        SELECT new map(c.id as id, c.name as name, c.pendingAmount as pending)
        FROM Customer c
        WHERE c.userId = :userId
        ORDER BY c.pendingAmount DESC
    """)
    List<Map<String,Object>> findTopCustomers(UUID userId);
}
