package com.XCLONE.KhataBackend.Repository;

import com.XCLONE.KhataBackend.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByUserIdAndIsActiveTrue(UUID userId);

    Optional<Product> findByIdAndUserId(UUID id, UUID userId);

    List<Product> findByUserIdAndNameContainingIgnoreCase(UUID userId, String name);

    int countByUserId(UUID userId);

    @Query("""
        SELECT COUNT(p)
        FROM Product p
        WHERE p.userId = :userId
        AND p.stock <= p.lowStockAlert
    """)
    int countLowStock(UUID userId);
}