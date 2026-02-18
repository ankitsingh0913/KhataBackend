package com.XCLONE.KhataBackend.Service;

import com.XCLONE.KhataBackend.DTO.UserRequestDTO;
import com.XCLONE.KhataBackend.DTO.UserResponseDTO;
import com.XCLONE.KhataBackend.DTO.auth.LoginRequestDTO;
import com.XCLONE.KhataBackend.DTO.auth.LoginResponseDTO;
import com.XCLONE.KhataBackend.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    UserResponseDTO createUser(UserRequestDTO requestDTO);

    UserResponseDTO getUserById(UUID id);

    Optional<User> getByEmail(String email);

    Optional<User> getByPhone(String phone);

    UserResponseDTO updateUser(UUID id, UserRequestDTO requestDTO);

    void deleteUser(UUID id);

    LoginResponseDTO login(LoginRequestDTO requestDto);
}
