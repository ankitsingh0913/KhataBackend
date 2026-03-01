package com.XCLONE.KhataBackend.ServiceImpl;

import com.XCLONE.KhataBackend.DTO.customer.CustomerRequestDTO;
import com.XCLONE.KhataBackend.DTO.customer.CustomerResponseDTO;
import com.XCLONE.KhataBackend.Entity.Customer;
import com.XCLONE.KhataBackend.Repository.CustomerRepository;
import com.XCLONE.KhataBackend.Service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponseDTO create(CustomerRequestDTO dto, UUID userId) {

        if (dto.getPhone() != null &&
                customerRepository.existsByUserIdAndPhone(userId, dto.getPhone())) {
            throw new RuntimeException("Customer with this phone already exists");
        }

        Customer customer = Customer.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .userId(userId)
                .totalPurchase(BigDecimal.ZERO)
                .pendingAmount(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return map(customerRepository.save(customer));
    }

    @Override
    public List<CustomerResponseDTO> getAll(UUID userId) {

        return customerRepository.findByUserId(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public CustomerResponseDTO getById(UUID id, UUID userId) {

        Customer customer = customerRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return map(customer);
    }

    @Override
    public CustomerResponseDTO update(UUID id,
                                      CustomerRequestDTO dto,
                                      UUID userId) {

        Customer customer = customerRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setUpdatedAt(Instant.now());

        return map(customerRepository.save(customer));
    }

    @Override
    public void delete(UUID id, UUID userId) {

        Customer customer = customerRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customerRepository.delete(customer);
    }

    private CustomerResponseDTO map(Customer customer) {

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .totalPurchase(customer.getTotalPurchase())
                .pendingAmount(customer.getPendingAmount())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
