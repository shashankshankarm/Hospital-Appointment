package com.hospitalappointment.dto.auth;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        String username,
        List<String> roles
) {
}
