package com.getir.bootcamp.repository;

import com.getir.bootcamp.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCaseOrGenreContainingIgnoreCase(
            String title, String author, String isbn, String genre, Pageable pageable
    );
}
