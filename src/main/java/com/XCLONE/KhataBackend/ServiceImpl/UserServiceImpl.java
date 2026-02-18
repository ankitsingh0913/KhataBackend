package com.XCLONE.KhataBackend.ServiceImpl;

import com.XCLONE.KhataBackend.DTO.UserRequestDTO;
import com.XCLONE.KhataBackend.DTO.UserResponseDTO;
import com.XCLONE.KhataBackend.DTO.auth.LoginRequestDTO;
import com.XCLONE.KhataBackend.DTO.auth.LoginResponseDTO;
import com.XCLONE.KhataBackend.Entity.User;
import com.XCLONE.KhataBackend.Repository.UserRepository;
import com.XCLONE.KhataBackend.Service.RefreshTokenService;
import com.XCLONE.KhataBackend.Service.UserService;
import com.XCLONE.KhataBackend.Utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    @Override
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {

        if (requestDTO.getEmail() != null && userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (requestDTO.getPhone() != null && userRepository.existsByPhone(requestDTO.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        User user = User.builder()
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .phone(requestDTO.getPhone())
                .password(passwordEncoder.encode(requestDTO.getPassword())) // will encode later
                .shopName(requestDTO.getShopName())
                .build();

        User saved =  userRepository.save(user);
        return mapToResponse(saved);
    }

    @Override
    public UserResponseDTO getUserById(UUID id) {
        UUID authenticatedUserId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!id.equals(authenticatedUserId)) {
            throw new RuntimeException("Access denied");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public UserResponseDTO updateUser(UUID id, UserRequestDTO requestDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(requestDTO.getFullName());
        user.setPhone(requestDTO.getPhone());

        User updated = userRepository.save(user);

        return mapToResponse(updated);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    private UserResponseDTO mapToResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDto) {

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(user.getId());
        String refreshToken = refreshTokenService.generateAndStore(user.getId());

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}