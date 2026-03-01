package com.XCLONE.KhataBackend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers",
        indexes = {
                @Index(name = "idx_customer_user", columnList = "user_id")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String phone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPurchase = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal pendingAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
