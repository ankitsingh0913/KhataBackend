package com.XCLONE.KhataBackend.Entity;

import com.XCLONE.KhataBackend.enums.BillStatus;
import com.XCLONE.KhataBackend.enums.DeliveryChannel;
import com.XCLONE.KhataBackend.enums.DeliveryStatus;
import com.XCLONE.KhataBackend.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bills",
        indexes = {
                @Index(name = "idx_bill_user", columnList = "user_id"),
                @Index(name = "idx_bill_customer", columnList = "customer_id"),
                @Index(name = "idx_bill_created", columnList = "created_at")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String billNumber;

    @Column(nullable = false)
    private UUID userId;

    private String shopName;
    private String shopPhone;
    private String shopAddress;

    private UUID customerId;

    private String customerName;   // Snapshot
    private String customerPhone;  // Snapshot
    private String customerEmail;  // Snapshot

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal discount;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal tax;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private String receiptUrl;

    @Column
    private Instant lastDeliveryAttempt;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Enumerated(EnumType.STRING)
    private DeliveryChannel deliveryChannel;

    @Column
    private Integer deliveryAttemptCount;

    @Column(columnDefinition = "TEXT")
    private String deliveryError;
}