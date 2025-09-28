package com.dodamdodam.dodamdodam.quiz.dto;

import com.dodamdodam.dodamdodam.quiz.entity.QuestionType;
import lombok.Builder;
import lombok.Data;
import java.util.List;

public class QuizDto {

    // 퀴즈 조회 시 응답
    @Data
    @Builder
    public static class QuizResponseDto {
        private Long quizId;
        private String quizTitle;
        private List<QuestionResponseDto> questions;
    }

    // 퀴즈 조회 시 질문 형식 (정답 제외)
    @Data
    @Builder
    public static class QuestionResponseDto {
        private Long questionId;
        private String questionText;
        private QuestionType questionType;
        private List<String> options;
    }

    // 퀴즈 제출 시 요청
    @Data
    public static class QuizSubmitRequestDto {
        private Long quizId;
        private List<AnswerSubmitDto> answers;
    }

    // 사용자가 제출한 개별 답안
    @Data
    public static class AnswerSubmitDto {
        private Long questionId;
        private String submittedAnswer;
    }

    // 채점 결과 응답
    @Data
    @Builder
    public static class QuizGradeResponseDto {
        private Long quizId;
        private int totalQuestions;
        private int correctAnswers;
        private double score;
        private List<GradedAnswerDto> results;
    }

    // 채점된 개별 답안 결과
    @Data
    @Builder
    public static class GradedAnswerDto {
        private Long questionId;
        private String submittedAnswer;
        private String correctAnswer;
        private boolean isCorrect;
    }
}