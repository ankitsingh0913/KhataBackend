package com.XCLONE.KhataBackend.Service;

import com.XCLONE.KhataBackend.DTO.auth.LoginResponseDTO;
import com.XCLONE.KhataBackend.Utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final long REFRESH_TOKEN_TTL =
            7 * 24 * 60 * 60; // 7 days in seconds

    public String generateAndStore(UUID userId) {

        String tokenId = UUID.randomUUID().toString();
        String secret = UUID.randomUUID().toString();
        String hashedSecret = passwordEncoder.encode(secret);

        String key = "refresh:" + tokenId;

        Map<String, String> data = new HashMap<>();
        data.put("userId", userId.toString());
        data.put("secret", hashedSecret);
        data.put("revoked", "false");

        redisTemplate.opsForHash().putAll(key, data);

        redisTemplate.expire(key, Duration.ofSeconds(REFRESH_TOKEN_TTL));

        return tokenId + "." + secret;
    }

    public LoginResponseDTO refresh(String refreshToken) {

        if (refreshToken == null || !refreshToken.contains(".")) {
            throw new RuntimeException("Invalid refresh token format");
        }

        String[] parts = refreshToken.split("\\.");
        String tokenId = parts[0];
        String secret = parts[1];

        String key = "refresh:" + tokenId;

        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

        if (data.isEmpty()) {
            throw new RuntimeException("Refresh token not found (possible reuse attack)");
        }

        if ("true".equals(data.get("revoked"))) {
            throw new RuntimeException("Refresh token already revoked (reuse detected)");
        }

        String storedHashedSecret = (String) data.get("secret");

        if (!passwordEncoder.matches(secret, storedHashedSecret)) {
            throw new RuntimeException("Invalid refresh token");
        }

        UUID userId = UUID.fromString((String) data.get("userId"));

        // Rotate: revoke old
        redisTemplate.opsForHash().put(key, "revoked", "true");

        // Generate new tokens
        String newAccessToken = jwtUtil.generateToken(userId);
        String newRefreshToken = generateAndStore(userId);

        return LoginResponseDTO.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .build();
    }


    public boolean validate(UUID userId, String rawToken) {

        String key = getKey(userId);
        String storedHash = redisTemplate.opsForValue().get(key);

        if (storedHash == null) return false;

        return passwordEncoder.matches(rawToken, storedHash);
    }

    public void delete(UUID userId) {
        redisTemplate.delete(getKey(userId));
    }

    private String getKey(UUID userId) {
        return "refresh:" + userId;
    }

}
