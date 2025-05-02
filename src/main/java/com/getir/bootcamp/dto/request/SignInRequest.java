package com.getir.bootcamp.dto.request;

public record SignInRequest(
        String username,
        String password
) {
}