package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.request.BookRequest;
import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.exception.ExceptionMessages;
import com.getir.bootcamp.exception.ResourceNotFoundException;
import com.getir.bootcamp.mapper.BookMapper;
import com.getir.bootcamp.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private BookRequest bookRequest;
    private Book book;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {

        bookRequest = new BookRequest(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                LocalDate.of(2018, 1, 6),
                "Programming"
        );

        book = Book.builder()
                .id(1L)
                .title(bookRequest.title())
                .author(bookRequest.author())
                .isbn(bookRequest.isbn())
                .publicationDate(bookRequest.publicationDate())
                .genre(bookRequest.genre())
                .isAvailable(true)
                .build();

        bookResponse = new BookResponse(
                1L,
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationDate(),
                book.getIsAvailable(),
                book.getGenre()
        );
    }


    @Test
    void addBook_ShouldSaveAndReturnBookResponse() {
        when(bookMapper.bookRequestToBookEntity(bookRequest)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.bookEntityToBookResponse(book)).thenReturn(bookResponse);

        BookResponse result = bookService.addBook(bookRequest);

        assertNotNull(result);
        assertEquals(bookResponse.title(), result.title());
        assertEquals(bookResponse.author(), result.author());
        assertEquals(bookResponse.isbn(), result.isbn());
        verify(bookRepository).save(book);
    }

    @Test
    void getBookById_ShouldReturnBookResponse_WhenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.bookEntityToBookResponse(book)).thenReturn(bookResponse);

        BookResponse result = bookService.getBookById(1L);

        assertEquals(bookResponse.title(), result.title());
        assertEquals(bookResponse.isbn(), result.isbn());
    }

    @Test
    void getBookById_ShouldThrowException_WhenBookNotFound() {
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> bookService.getBookById(2L));

        assertEquals(ExceptionMessages.BOOK_NOT_FOUND, exception.getMessage());
    }

    @Test
    void searchBooks_ShouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.searchBooks("Effective", pageable)).thenReturn(bookPage);
        when(bookMapper.bookEntityToBookResponse(book)).thenReturn(bookResponse);

        Page<BookResponse> result = bookService.searchBooks("Effective", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Effective Java", result.getContent().getFirst().title());
    }

    @Test
    void updateBook_ShouldMapAndSaveUpdatedBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookMapper).updateBookEntityFromRequest(bookRequest, book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.bookEntityToBookResponse(book)).thenReturn(bookResponse);

        BookResponse result = bookService.updateBook(1L, bookRequest);

        assertEquals("Effective Java", result.title());
        verify(bookMapper).updateBookEntityFromRequest(bookRequest, book);
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBook_ShouldDeleteBook_WhenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_ShouldThrow_WhenBookNotFound() {
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(2L));
    }

    @Test
    void setBookAvailability_ShouldUpdateAvailabilityAndSave() {
        bookService.setBookAvailability(book, false);

        assertFalse(book.getIsAvailable());
        verify(bookRepository).save(book);
    }
}
