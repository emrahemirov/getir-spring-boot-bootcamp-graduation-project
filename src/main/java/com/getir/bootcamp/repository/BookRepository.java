package com.getir.bootcamp.repository;

import com.getir.bootcamp.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
                SELECT b FROM Book b
                WHERE (b.title IS NOT NULL AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
                    OR (b.author IS NOT NULL AND LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
                    OR (b.isbn IS NOT NULL AND LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
                    OR (b.genre IS NOT NULL AND LOWER(b.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
            """)
    Page<Book> searchBooks(@Param("searchTerm") String searchTerm, Pageable pageable);

}
