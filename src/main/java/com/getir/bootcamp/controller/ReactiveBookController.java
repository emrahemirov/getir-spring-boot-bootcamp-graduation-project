package com.getir.bootcamp.controller;

import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/v1/reactive-books")
@RequiredArgsConstructor
public class ReactiveBookController {

    private final BookService bookService;

    @Operation(
            summary = "Search books (reactive)",
            description = "Searches for books matching the provided keyword. Results are streamed in NDJSON format for efficient reactive consumption.",
            parameters = {
                    @Parameter(name = "keyword", description = "The keyword to search for (required)", required = true, example = "Hobbit"),
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Page size", example = "10")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stream of matching books",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_NDJSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = BookResponse.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<BookResponse> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> bookService.searchBooks(keyword, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(pageResult -> Flux.fromIterable(pageResult.getContent()));
    }
}
