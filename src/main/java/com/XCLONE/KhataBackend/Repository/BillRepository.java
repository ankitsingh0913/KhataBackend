package com.XCLONE.KhataBackend.Repository;

import com.XCLONE.KhataBackend.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    List<Bill> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Bill> findByIdAndUserId(UUID id, UUID userId);


    @Query("""
        SELECT SUM(b.total)
        FROM Bill b
        WHERE b.userId = :userId
        AND b.createdAt >= :date
    """)
    BigDecimal getSalesAfter(UUID userId, Instant date);


    @Query("""
        SELECT COUNT(b)
        FROM Bill b
        WHERE b.userId = :userId
        AND b.createdAt >= :date
    """)
    int countAfter(UUID userId, Instant date);


    @Query("""
        SELECT new map(
        b.id as id,
        b.billNumber as billNumber,
        b.total as total,
        b.createdAt as createdAt
        )
        FROM Bill b
        WHERE b.userId = :userId
        ORDER BY b.createdAt DESC
    """)
    List<Map<String,Object>> findRecentBills(UUID userId);

    @Query("""
        SELECT new map(
            FUNCTION('DATE', b.createdAt) as date,
            SUM(b.total) as total
        )
        FROM Bill b
        WHERE b.userId = :userId
        AND b.createdAt >= :startDate
        GROUP BY FUNCTION('DATE', b.createdAt)
        ORDER BY FUNCTION('DATE', b.createdAt)
    """)
    List<Map<String, Object>> getSalesLast7Days(UUID userId, Instant startDate);
}
