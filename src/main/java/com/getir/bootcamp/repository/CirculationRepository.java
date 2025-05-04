package com.getir.bootcamp.repository;

import com.getir.bootcamp.entity.Circulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CirculationRepository extends JpaRepository<Circulation, Long> {

    @Query("SELECT c FROM Circulation c JOIN FETCH c.book JOIN FETCH c.user WHERE c.user.id = :userId")
    List<Circulation> findByUserIdWithBookAndUser(Long userId);

    @Query("SELECT c FROM Circulation c JOIN FETCH c.book JOIN FETCH c.user WHERE c.returnDate IS NULL AND c.dueDate < :date")
    List<Circulation> findOverdueWithBookAndUser(LocalDate date);

    @Query("SELECT c FROM Circulation c JOIN FETCH c.book JOIN FETCH c.user")
    List<Circulation> findAllWithBookAndUser();

    @Query("SELECT COUNT(c) = 0 FROM Circulation c WHERE c.book.id = :bookId AND c.returnDate IS NULL")
    boolean isBookAvailable(@Param("bookId") Long bookId);
}
