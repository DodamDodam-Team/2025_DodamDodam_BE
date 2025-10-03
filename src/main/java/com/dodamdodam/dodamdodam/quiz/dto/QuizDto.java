package com.dodamdodam.dodamdodam.quiz.dto;

import com.dodamdodam.dodamdodam.quiz.entity.QuestionType;
import lombok.Builder;
import lombok.Data;
import java.util.List;

public class QuizDto {

    // --- 퀴즈 조회 DTO ---
    @Data
    @Builder
    public static class QuizResponseDto {
        private Long quizId;
        private String quizTitle;
        private List<QuestionResponseDto> questions;
    }

    @Data
    @Builder
    public static class QuestionResponseDto {
        private Long questionId;
        private String questionText;
        private QuestionType questionType;
        private List<OptionDto> options;
    }

    @Data
    @Builder
    public static class OptionDto {
        private Long optionId;
        private String optionText;
    }

    // --- 퀴즈 제출 및 채점 DTO ---
    @Data
    public static class QuizSubmitRequestDto { // ✅ 컨트롤러에서 찾지 못하는 클래스가 바로 이 클래스입니다.
        private Long questionId;
        private Long selectedOptionId;
    }

    @Data
    @Builder
    public static class QuizGradeResponseDto {
        private Long questionId;
        private Long selectedOptionId;
        private boolean isCorrect;
        private String message;
    }
}