package com.XCLONE.KhataBackend.Exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class ApiErrorResponse {

    private boolean success;
    private String message;
    private Map<String, String> errors;
    private Instant timestamp;
}

