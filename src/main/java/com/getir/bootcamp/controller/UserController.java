package com.getir.bootcamp.controller;

import com.getir.bootcamp.dto.CommonResponse;
import com.getir.bootcamp.dto.request.UserRequest;
import com.getir.bootcamp.dto.response.UserResponse;
import com.getir.bootcamp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves the details of a user by their ID. Requires librarian role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "The ID of the user", required = true, example = "42")
            },
            responses = {
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @GetMapping("/{username}")
    public ResponseEntity<CommonResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        UserResponse userResponse = userService.getUserByUsername(username);
        return ResponseEntity.ok(CommonResponse.ok(userResponse));
    }

    @Operation(
            summary = "Update user details",
            description = "Updates the details of a user by ID. Requires librarian role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "The ID of the user to update", required = true, example = "42")
            },
            responses = {
                    @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @PutMapping("/{username}")
    public ResponseEntity<CommonResponse<UserResponse>> updateUser(
            @PathVariable String username,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Updated user details",
                    content = @Content(schema = @Schema(implementation = UserRequest.class))
            )
            @Valid @RequestBody UserRequest userRequest) {

        UserResponse updatedUser = userService.updateUser(username, userRequest);
        return ResponseEntity.ok(CommonResponse.ok(updatedUser));
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by ID. Requires librarian role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "The ID of the user to delete", required = true, example = "42")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<CommonResponse<Void>> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok(CommonResponse.noContent());
    }

    @Operation(
            summary = "Set user role to librarian",
            description = "Promotes a user to librarian role. Requires librarian role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "The ID of the user to promote", required = true, example = "42")
            },
            responses = {
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @PatchMapping("/{username}/set-librarian")
    public ResponseEntity<CommonResponse<UserResponse>> setUserRoleLibrarian(@PathVariable String username) {
        UserResponse updatedUser = userService.setUserRoleLibrarian(username);
        return ResponseEntity.ok(CommonResponse.ok(updatedUser));
    }
}
