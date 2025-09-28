package com.dodamdodam.dodamdodam.recorders.repository;

import com.dodamdodam.dodamdodam.recorders.entity.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReadingRepository extends JpaRepository<Reading, Long> {
    List<Reading> findByUserIdAndReadDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(DISTINCT rl.readBookId) FROM Reading rl WHERE rl.userId = :userId AND rl.isBookCompletedForThisLog = true AND rl.readDate BETWEEN :startDate AND :endDate")
    Integer countCompletedBooksInMonth(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(rl.readDurationMinutes) FROM Reading rl WHERE rl.userId = :userId AND rl.readDate BETWEEN :startDate AND :endDate")
    Integer sumReadDurationMinutesInMonth(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Optional<Reading> findFirstByUserIdOrderByReadDateAsc(Long userId);
}
