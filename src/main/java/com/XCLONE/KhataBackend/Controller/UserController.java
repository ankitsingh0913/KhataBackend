package com.XCLONE.KhataBackend.Controller;

import com.XCLONE.KhataBackend.DTO.user.UserResponseDTO;
import com.XCLONE.KhataBackend.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
