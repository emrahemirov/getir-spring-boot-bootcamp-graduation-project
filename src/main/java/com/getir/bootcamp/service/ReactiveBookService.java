package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.mapper.BookMapper;
import com.getir.bootcamp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ReactiveBookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Flux<BookResponse> getAllBooks() {
        return Flux.defer(() -> Flux.fromIterable(bookRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(bookMapper::bookEntityToBookResponse);
    }
}
