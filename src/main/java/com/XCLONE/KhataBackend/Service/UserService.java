package com.XCLONE.KhataBackend.Service;

import com.XCLONE.KhataBackend.DTO.user.UserRequestDTO;
import com.XCLONE.KhataBackend.DTO.user.UserUpdateRequestDTO;
import com.XCLONE.KhataBackend.DTO.user.UserResponseDTO;
import com.XCLONE.KhataBackend.DTO.auth.LoginRequestDTO;
import com.XCLONE.KhataBackend.DTO.auth.LoginResponseDTO;
import com.XCLONE.KhataBackend.Entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    UserResponseDTO createUser(UserRequestDTO requestDTO);

    UserResponseDTO getUserById(UUID id);

    Optional<User> getByEmail(String email);

    Optional<User> getByPhone(String phone);

    UserResponseDTO updateUser(UUID id, UserUpdateRequestDTO requestDTO);

    void deleteUser(UUID id);

    LoginResponseDTO login(LoginRequestDTO requestDto);

    LoginResponseDTO handleGoogleLogin(String email, String name);
}
