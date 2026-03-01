package com.XCLONE.KhataBackend.Controller;

import com.XCLONE.KhataBackend.DTO.user.UserRequestDTO;
import com.XCLONE.KhataBackend.DTO.user.UserResponseDTO;
import com.XCLONE.KhataBackend.DTO.auth.GoogleLoginRequest;
import com.XCLONE.KhataBackend.DTO.auth.LoginRequestDTO;
import com.XCLONE.KhataBackend.DTO.auth.LoginResponseDTO;
import com.XCLONE.KhataBackend.DTO.auth.RefreshRequest;
import com.XCLONE.KhataBackend.Service.GoogleAuthService;
import com.XCLONE.KhataBackend.Service.RefreshTokenService;
import com.XCLONE.KhataBackend.Service.UserService;
import com.XCLONE.KhataBackend.Utils.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDto) {

        return ResponseEntity.ok(userService.login(requestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        UUID userId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        refreshTokenService.delete(userId);

        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody RefreshRequest request) {

        return ResponseEntity.ok(
                refreshTokenService.refresh(request.getRefreshToken())
        );
    }



    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> createUser(@Valid  @RequestBody UserRequestDTO requestDto) {

        UserResponseDTO response = userService.createUser(requestDto);

        return ResponseEntity
                .created(URI.create("/api/v1/auth/" + response.getId()))
                .body(response);
    }

        //GOOGLE AUTH
    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> googleLogin(
            @RequestBody GoogleLoginRequest request) throws Exception {

        GoogleIdToken.Payload payload =
                googleAuthService.verify(request.getIdToken());

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        if (name == null || name.isBlank()) {
            name = email.split("@")[0];
        }
        LoginResponseDTO response =
                userService.handleGoogleLogin(email, name);

        return ResponseEntity.ok(response);
    }
}
