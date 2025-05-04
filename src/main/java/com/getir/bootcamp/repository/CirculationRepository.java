package com.getir.bootcamp.repository;

import com.getir.bootcamp.entity.Circulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CirculationRepository extends JpaRepository<Circulation, Long> {

    @Query("SELECT c FROM Circulation c JOIN FETCH c.book JOIN FETCH c.user WHERE c.id = :id")
    Optional<Circulation> findByIdWithBookAndUser(Long id);

    @Query("SELECT c FROM Circulation c JOIN FETCH c.book JOIN FETCH c.user WHERE c.user.id = :userId")
    List<Circulation> findByUserIdWithBookAndUser(Long userId);

    @Query("SELECT c FROM Circulation c JOIN FETCH c.book JOIN FETCH c.user WHERE c.returnDate IS NULL AND c.dueDate < :date")
    List<Circulation> findOverdueWithBookAndUser(LocalDate date);

    @Query("SELECT c FROM Circulation c JOIN FETCH c.book JOIN FETCH c.user")
    List<Circulation> findAllWithBookAndUser();
}
