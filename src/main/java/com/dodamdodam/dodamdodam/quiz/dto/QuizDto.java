package com.dodamdodam.dodamdodam.quiz.dto;

import com.dodamdodam.dodamdodam.quiz.entity.QuestionType;
import lombok.Builder;
import lombok.Data;
import java.util.List;

public class QuizDto {

    // --- 퀴즈 조회 DTO ---
    @Data @Builder
    public static class QuizResponseDto {
        private Long quizId;
        private String quizTitle;
        private List<QuestionResponseDto> questions;
    }

    @Data @Builder
    public static class QuestionResponseDto {
        private Long questionId;
        private String questionText;
        private QuestionType questionType;
        private List<OptionDto> options;
    }

    @Data @Builder
    public static class OptionDto {
        private Long optionId;
        private String optionText;
    }

    @Data
    public static class QuizSubmitRequestDto {
        private Long questionId;
        private Long selectedOptionId; // ✅ String -> Long
    }

    @Data @Builder
    public static class QuizGradeResponseDto {
        private Long questionId;
        private Long selectedOptionId;
        private boolean isCorrect;
        private String message;
    }
}