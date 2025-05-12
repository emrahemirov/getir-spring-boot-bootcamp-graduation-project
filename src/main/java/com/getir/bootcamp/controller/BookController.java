package com.getir.bootcamp.controller;

import com.getir.bootcamp.dto.CommonResponse;
import com.getir.bootcamp.dto.request.BookRequest;
import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Add a new book",
            description = "Adds a new book to the library. Requires librarian role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @PostMapping
    public ResponseEntity<CommonResponse<BookResponse>> addBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Book details",
                    content = @Content(schema = @Schema(implementation = BookRequest.class))
            )
            @Valid @RequestBody BookRequest bookRequest) {
        BookResponse savedBook = bookService.addBook(bookRequest);
        return ResponseEntity.ok(CommonResponse.ok(savedBook));
    }

    @Operation(
            summary = "Get a book by ID",
            description = "Retrieves the details of a specific book by its ID.",
            responses = {
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(CommonResponse.ok(book));
    }

    @Operation(
            summary = "Update a book",
            description = "Updates an existing book’s details. Requires librarian role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized"),
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Updated book details",
                    content = @Content(schema = @Schema(implementation = BookRequest.class))
            )
            @Valid @RequestBody BookRequest bookRequest) {

        BookResponse updatedBook = bookService.updateBook(id, bookRequest);
        return ResponseEntity.ok(CommonResponse.ok(updatedBook));
    }

    @Operation(
            summary = "Delete a book",
            description = "Deletes a book by its ID. Requires librarian role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book successfully deleted"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized"),
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(CommonResponse.noContent());
    }
}
