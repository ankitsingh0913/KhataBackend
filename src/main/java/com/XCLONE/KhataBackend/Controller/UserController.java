package com.XCLONE.KhataBackend.Controller;

import com.XCLONE.KhataBackend.DTO.user.UserRequestDTO;
import com.XCLONE.KhataBackend.DTO.user.UserUpdateRequestDTO;
import com.XCLONE.KhataBackend.DTO.user.UserResponseDTO;
import jakarta.validation.Valid;
import com.XCLONE.KhataBackend.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable UUID id) {

        UserResponseDTO response = userService.getUserById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        UUID uuid = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserResponseDTO response = userService.getUserById(uuid);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Valid @RequestBody UserUpdateRequestDTO requestDTO) {

        UUID uuid = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserResponseDTO response = userService.updateUser(uuid, requestDTO);

        return ResponseEntity.ok(response);
    }
}
