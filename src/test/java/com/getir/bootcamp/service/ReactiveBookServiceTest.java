package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.mapper.BookMapper;
import com.getir.bootcamp.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactiveBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private ReactiveBookService reactiveBookService;

    @Test
    void shouldReturnAllBooksAsBookResponses() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Title A");
        book1.setAuthor("Author A");
        book1.setIsbn("111-A");
        book1.setPublicationDate(LocalDate.of(2020, 1, 1));
        book1.setGenre("Fiction");
        book1.setIsAvailable(true);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Title B");
        book2.setAuthor("Author B");
        book2.setIsbn("222-B");
        book2.setPublicationDate(LocalDate.of(2021, 2, 2));
        book2.setGenre("Science");
        book2.setIsAvailable(false);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        BookResponse response1 = new BookResponse(
                1L, "Title A", "Author A", "111-A",
                LocalDate.of(2020, 1, 1), true, "Fiction");

        BookResponse response2 = new BookResponse(
                2L, "Title B", "Author B", "222-B",
                LocalDate.of(2021, 2, 2), false, "Science");

        when(bookMapper.bookEntityToBookResponse(book1)).thenReturn(response1);
        when(bookMapper.bookEntityToBookResponse(book2)).thenReturn(response2);

        Flux<BookResponse> result = reactiveBookService.getAllBooks();

        StepVerifier.create(result)
                .expectNext(response1)
                .expectNext(response2)
                .verifyComplete();

        verify(bookRepository, times(1)).findAll();
        verify(bookMapper, times(1)).bookEntityToBookResponse(book1);
        verify(bookMapper, times(1)).bookEntityToBookResponse(book2);
    }
}
