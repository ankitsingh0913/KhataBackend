package com.XCLONE.KhataBackend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_user_phone", columnNames = "phone")
        }
)
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(nullable = false)
        private String fullName;

        @Column(nullable = true)
        private String email;

        @Column(nullable = true)
        private String phone;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false)
        private String shopName;

        @Column(nullable = false)
        private boolean isActive;

        @Column(nullable = false, updatable = false)
        private Instant createdAt;

        @Column(nullable = false)
        private Instant updatedAt;

        @PrePersist
        public void onCreate() {
                this.createdAt = Instant.now();

                this.updatedAt = Instant.now();
                this.isActive = true;
        }

        @PreUpdate
        public void onUpdate() {
                this.updatedAt = Instant.now();
        }

}
