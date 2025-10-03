package com.dodamdodam.dodamdodam.quiz.repository;

import com.dodamdodam.dodamdodam.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // 책 ID로 퀴즈를 찾는 메서드
    Optional<Quiz> findByBookId(Long bookId);
}