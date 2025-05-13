package com.getir.bootcamp.controller;

import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.service.ReactiveBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/reactive-books")
@RequiredArgsConstructor
public class ReactiveBookController {

    private final ReactiveBookService reactiveBookService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookResponse> getBooks() {
        return reactiveBookService.getAllBooks()
                .delayElements(Duration.ofMillis(500));
    }

}
