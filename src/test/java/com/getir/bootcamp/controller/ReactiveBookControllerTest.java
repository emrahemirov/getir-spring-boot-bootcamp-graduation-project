package com.getir.bootcamp.controller;

import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ReactiveBookControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();

        bookRepository.saveAll(List.of(
                new Book(null, "Book A", "Author A", "ISBN-A",
                        LocalDate.of(2020, 1, 1), "Fiction", true, null),
                new Book(null, "Book B", "Author B", "ISBN-B",
                        LocalDate.of(2021, 2, 2), "Sci-Fi", false, null)
        ));
    }

    @Test
    void shouldStreamBooksAsEventStream() {
        webTestClient.get()
                .uri("/api/v1/reactive-books")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(BookResponse.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectNextMatches(book -> book.title().equals("Book A"))
                .expectNextMatches(book -> book.title().equals("Book B"))
                .expectComplete()
                .verify();
    }
}
