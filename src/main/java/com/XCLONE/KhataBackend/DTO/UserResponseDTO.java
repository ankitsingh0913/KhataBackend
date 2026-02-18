package com.XCLONE.KhataBackend.DTO;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private boolean isActive;
    private Instant createdAt;
}
