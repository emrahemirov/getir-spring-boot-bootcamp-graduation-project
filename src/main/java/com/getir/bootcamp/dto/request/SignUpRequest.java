package com.getir.bootcamp.dto.request;

public record SignUpRequest(
        String firstName,
        String lastName,
        String username,
        String password
) {
}