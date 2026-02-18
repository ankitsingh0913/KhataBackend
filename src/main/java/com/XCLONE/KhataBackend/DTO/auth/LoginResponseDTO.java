package com.XCLONE.KhataBackend.DTO.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;
}
