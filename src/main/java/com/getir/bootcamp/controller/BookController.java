package com.getir.bootcamp.controller;

import com.getir.bootcamp.dto.CommonResponse;
import com.getir.bootcamp.dto.request.BookRequest;
import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private BookService bookService;

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @PostMapping
    public ResponseEntity<CommonResponse<BookResponse>> addBook(@Valid @RequestBody BookRequest bookRequest) {
        BookResponse savedBook = bookService.addBook(bookRequest);
        return ResponseEntity.ok(CommonResponse.ok(savedBook));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(CommonResponse.ok(book));
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<Page<BookResponse>>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> books = bookService.searchBooks(keyword, pageable);
        return ResponseEntity.ok(CommonResponse.ok(books));
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest bookRequest
    ) {
        BookResponse updatedBook = bookService.updateBook(id, bookRequest);
        return ResponseEntity.ok(CommonResponse.ok(updatedBook));
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(CommonResponse.noContent());
    }
}
