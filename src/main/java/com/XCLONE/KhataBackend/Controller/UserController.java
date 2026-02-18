package com.XCLONE.KhataBackend.Controller;

import com.XCLONE.KhataBackend.DTO.UserRequestDTO;
import com.XCLONE.KhataBackend.DTO.UserResponseDTO;
import com.XCLONE.KhataBackend.DTO.auth.LoginRequestDTO;
import com.XCLONE.KhataBackend.DTO.auth.LoginResponseDTO;
import com.XCLONE.KhataBackend.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
}
