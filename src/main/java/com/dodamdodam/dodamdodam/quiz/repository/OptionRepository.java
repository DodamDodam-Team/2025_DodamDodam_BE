package com.dodamdodam.dodamdodam.quiz.repository;

import com.dodamdodam.dodamdodam.quiz.entity.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Option 엔티티를 관리하는 Repository
 */
public interface OptionRepository extends JpaRepository<QuizOption, Long> {
    // 필요한 경우 Option 관련 커스텀 쿼리 메서드 추가
}