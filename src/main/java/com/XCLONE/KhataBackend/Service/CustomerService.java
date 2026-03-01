package com.XCLONE.KhataBackend.Service;

import com.XCLONE.KhataBackend.DTO.customer.CustomerRequestDTO;
import com.XCLONE.KhataBackend.DTO.customer.CustomerResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    CustomerResponseDTO create(CustomerRequestDTO dto, UUID userId);

    List<CustomerResponseDTO> getAll(UUID userId);

    CustomerResponseDTO getById(UUID id, UUID userId);

    CustomerResponseDTO update(UUID id, CustomerRequestDTO dto, UUID userId);

    void delete(UUID id, UUID userId);
}