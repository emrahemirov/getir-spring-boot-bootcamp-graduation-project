package com.getir.bootcamp.controller;

import com.getir.bootcamp.dto.CommonResponse;
import com.getir.bootcamp.dto.request.CirculationRequest;
import com.getir.bootcamp.dto.response.CirculationResponse;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.service.CirculationService;
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

    private CirculationService circulationService;

    @PreAuthorize("hasAuthority('ROLE_PATRON')")
    @PostMapping
    public ResponseEntity<CommonResponse<CirculationResponse>> borrowBook(
            @Valid @RequestBody CirculationRequest request,
            @AuthenticationPrincipal User currentUser) {
        CirculationResponse response = circulationService.borrowBook(currentUser.getId(), request);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @PreAuthorize("hasAuthority('ROLE_PATRON')")
    @PostMapping("/{circulationId}/return")
    public ResponseEntity<CommonResponse<CirculationResponse>> returnBook(
            @PathVariable Long circulationId) {
        CirculationResponse response = circulationService.returnBook(circulationId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_PATRON', 'ROLE_LIBRARIAN')")
    @GetMapping("/history")
    public ResponseEntity<CommonResponse<List<CirculationResponse>>> getMyHistory(
            @AuthenticationPrincipal User currentUser) {
        List<CirculationResponse> history = circulationService.getUserHistory(currentUser.getId());
        return ResponseEntity.ok(CommonResponse.ok(history));
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @GetMapping("/history/all")
    public ResponseEntity<CommonResponse<List<CirculationResponse>>> getAllHistory() {
        List<CirculationResponse> allHistory = circulationService.getAllCirculations();
        return ResponseEntity.ok(CommonResponse.ok(allHistory));
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @GetMapping("/overdue")
    public ResponseEntity<CommonResponse<List<CirculationResponse>>> getOverdueBooks() {
        List<CirculationResponse> overdue = circulationService.getOverdueBooks();
        return ResponseEntity.ok(CommonResponse.ok(overdue));
    }
}

