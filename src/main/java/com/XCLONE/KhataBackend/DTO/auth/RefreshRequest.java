package com.XCLONE.KhataBackend.DTO.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RefreshRequest {

    private String refreshToken;
}
