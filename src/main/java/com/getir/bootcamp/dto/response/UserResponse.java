package com.getir.bootcamp.dto.response;

import com.getir.bootcamp.entity.Role;

public record UserResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        Boolean canBorrow,
        Role role) {
}
