package com.dodamdodam.dodamdodam.quiz.repository;

import com.dodamdodam.dodamdodam.quiz.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    // 나중에 통계 기능이 필요하면 여기에 커스텀 쿼리 메서드 추가
    // 예: List<QuizSubmission> findByUser(User user);
}