package com.XCLONE.KhataBackend.DTO.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequestDTO {

    @NotBlank(message = "Customer name is required")
    private String name;

    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;
}
