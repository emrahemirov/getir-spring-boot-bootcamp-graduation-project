package com.getir.bootcamp.controller;

import com.getir.bootcamp.dto.CommonResponse;
import com.getir.bootcamp.dto.request.CirculationRequest;
import com.getir.bootcamp.dto.response.CirculationResponse;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.service.CirculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/circulations")
@RequiredArgsConstructor
public class CirculationController {

    private final CirculationService circulationService;

    @Operation(
            summary = "Borrow a book",
            description = "Allows a patron to borrow a book. Requires ROLE_PATRON.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_PATRON')")
    @PostMapping
    public ResponseEntity<CommonResponse<CirculationResponse>> borrowBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Borrow request details",
                    content = @Content(schema = @Schema(implementation = CirculationRequest.class))
            )
            @Valid @RequestBody CirculationRequest request,
            @AuthenticationPrincipal User currentUser) {

        CirculationResponse response = circulationService.borrowBook(currentUser.getId(), request);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(
            summary = "Return a borrowed book",
            description = "Allows a patron to return a borrowed book. Requires ROLE_PATRON.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "404", description = "Circulation record not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_PATRON')")
    @PostMapping("/{circulationId}/return")
    public ResponseEntity<CommonResponse<CirculationResponse>> returnBook(
            @PathVariable Long circulationId) {

        CirculationResponse response = circulationService.returnBook(circulationId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(
            summary = "Get my borrowing history",
            description = "Retrieves borrowing history for the authenticated user. Accessible by patrons and librarians.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_PATRON')")
    @GetMapping("/history")
    public ResponseEntity<CommonResponse<List<CirculationResponse>>> getMyHistory(
            @AuthenticationPrincipal User currentUser) {

        List<CirculationResponse> history = circulationService.getUserHistory(currentUser.getId());
        return ResponseEntity.ok(CommonResponse.ok(history));
    }

    @Operation(
            summary = "Get all borrowing history",
            description = "Retrieves the full borrowing history of all users. Requires librarian role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @GetMapping("/history/all")
    public ResponseEntity<CommonResponse<List<CirculationResponse>>> getAllHistory() {
        List<CirculationResponse> allHistory = circulationService.getAllCirculations();
        return ResponseEntity.ok(CommonResponse.ok(allHistory));
    }

    @Operation(
            summary = "Get overdue books",
            description = "Retrieves a list of all overdue books. Requires librarian role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @GetMapping("/overdue")
    public ResponseEntity<CommonResponse<List<CirculationResponse>>> getOverdueBooks() {
        List<CirculationResponse> overdue = circulationService.getOverdueBooks();
        return ResponseEntity.ok(CommonResponse.ok(overdue));
    }

    //TODO generate report for overdue books
}
