package com.XCLONE.KhataBackend.DTO.user;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDTO {

    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @Size(min = 2, max = 150, message = "Shop name must be between 2 and 150 characters")
    private String shopName;

    @Size(max = 100, message = "UPI ID must be less than 100 characters")
    private String upiId;

}
