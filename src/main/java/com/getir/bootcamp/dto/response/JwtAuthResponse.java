package com.getir.bootcamp.dto.response;

public record JwtAuthResponse(
        String accessToken,
        String refreshToken
) {
}