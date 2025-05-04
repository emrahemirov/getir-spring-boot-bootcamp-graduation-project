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
    private BookRepository bookRepository;
    private BookMapper bookMapper;

    public BookResponse addBook(BookRequest bookRequest) {
        Book book = bookMapper.toEntity(bookRequest);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.BOOK_NOT_FOUND));
        return bookMapper.toResponse(book);
    }

    public Page<BookResponse> searchBooks(String keyword, Pageable pageable) {
        Page<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCaseOrGenreContainingIgnoreCase(
                keyword, keyword, keyword, keyword, pageable
        );
        return books.map(bookMapper::toResponse);
    }

    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.BOOK_NOT_FOUND));
        bookMapper.updateBookFromRequest(bookRequest, book);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.BOOK_NOT_FOUND));
        bookRepository.delete(book);
    }
}
