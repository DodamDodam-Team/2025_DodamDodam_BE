package com.dodamdodam.dodamdodam.quiz.repository;

import com.dodamdodam.dodamdodam.login.entity.User;
import com.dodamdodam.dodamdodam.quiz.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByUser(User user);
}