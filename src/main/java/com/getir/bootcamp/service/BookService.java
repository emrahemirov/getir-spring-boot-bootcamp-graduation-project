package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.request.BookRequest;
import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.exception.ExceptionMessages;
import com.getir.bootcamp.exception.ResourceNotFoundException;
import com.getir.bootcamp.mapper.BookMapper;
import com.getir.bootcamp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookResponse addBook(BookRequest bookRequest) {
        Book book = bookMapper.bookRequestToBookEntity(bookRequest);
        book.setIsAvailable(true);
        Book savedBook = bookRepository.save(book);
        return bookMapper.bookEntityToBookResponse(savedBook);
    }

    public BookResponse getBookById(Long id) {
        Book book = getBookEntityById(id);
        return bookMapper.bookEntityToBookResponse(book);
    }

    public Page<BookResponse> searchBooks(String keyword, Pageable pageable) {
        Page<Book> books = bookRepository.searchBooks(keyword, pageable);
        return books.map(bookMapper::bookEntityToBookResponse);
    }

    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        Book book = getBookEntityById(id);
        bookMapper.updateBookEntityFromRequest(bookRequest, book);
        Book savedBook = bookRepository.save(book);
        return bookMapper.bookEntityToBookResponse(savedBook);
    }

    public void deleteBook(Long id) {
        Book book = getBookEntityById(id);
        bookRepository.delete(book);
    }

    public Book getBookEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.BOOK_NOT_FOUND));
    }

    public void setBookAvailability(Book book, boolean isAvailable) {
        book.setIsAvailable(isAvailable);
        bookRepository.save(book);
    }
}
