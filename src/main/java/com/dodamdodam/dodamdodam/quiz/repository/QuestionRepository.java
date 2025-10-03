package com.dodamdodam.dodamdodam.quiz.repository;

import com.dodamdodam.dodamdodam.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 필요한 경우 Question 관련 커스텀 쿼리 메서드 추가
}