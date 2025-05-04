package com.getir.bootcamp.dto.request;


public record UserRequest(
        String username,
        String firstName,
        String lastName
) {
}