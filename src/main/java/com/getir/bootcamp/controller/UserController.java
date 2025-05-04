package com.getir.bootcamp.controller;

import com.getir.bootcamp.dto.CommonResponse;
import com.getir.bootcamp.dto.request.UserRequest;
import com.getir.bootcamp.dto.response.UserResponse;
import com.getir.bootcamp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private UserService userService;

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(CommonResponse.ok(userResponse));
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(CommonResponse.ok(updatedUser));
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(CommonResponse.noContent());
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @PatchMapping("/{id}/set-librarian")
    public ResponseEntity<CommonResponse<UserResponse>> setUserRoleLibrarian(@PathVariable Long id) {
        UserResponse updatedUser = userService.setUserRoleLibrarian(id);
        return ResponseEntity.ok(CommonResponse.ok(updatedUser));
    }
}
