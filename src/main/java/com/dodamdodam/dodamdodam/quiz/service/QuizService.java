package com.dodamdodam.dodamdodam.quiz.service;

import com.dodamdodam.dodamdodam.quiz.dto.QuizDto;
import com.dodamdodam.dodamdodam.quiz.entity.Question;
import com.dodamdodam.dodamdodam.quiz.entity.Quiz;
import com.dodamdodam.dodamdodam.quiz.repository.QuizRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper;

    public QuizDto.QuizResponseDto getQuizForBook(Long bookId) {
        Quiz quiz = quizRepository.findByBookId(bookId)
                .orElseThrow(() -> new EntityNotFoundException("해당 책에 대한 퀴즈를 찾을 수 없습니다. bookId: " + bookId));

        List<QuizDto.QuestionResponseDto> questionDtos = quiz.getQuestions().stream()
                .map(this::convertQuestionToDto)
                .collect(Collectors.toList());

        return QuizDto.QuizResponseDto.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .questions(questionDtos)
                .build();
    }

    @Transactional
    public QuizDto.QuizGradeResponseDto gradeQuiz(QuizDto.QuizSubmitRequestDto submitRequest, Long userId) {
        Quiz quiz = quizRepository.findById(submitRequest.getQuizId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈를 찾을 수 없습니다. quizId: " + submitRequest.getQuizId()));

        Map<Long, String> correctAnswers = quiz.getQuestions().stream()
                .collect(Collectors.toMap(Question::getId, Question::getCorrectAnswer));

        int correctCount = 0;
        List<QuizDto.GradedAnswerDto> results = new ArrayList<>();

        for (QuizDto.AnswerSubmitDto submittedAnswer : submitRequest.getAnswers()) {
            Long questionId = submittedAnswer.getQuestionId();
            String correctAnswer = correctAnswers.get(questionId);
            boolean isCorrect = correctAnswer != null && correctAnswer.equals(submittedAnswer.getSubmittedAnswer());

            if (isCorrect) {
                correctCount++;
            }

            results.add(QuizDto.GradedAnswerDto.builder()
                    .questionId(questionId)
                    .submittedAnswer(submittedAnswer.getSubmittedAnswer())
                    .correctAnswer(correctAnswer)
                    .isCorrect(isCorrect)
                    .build());
        }


        double score = (double) correctCount / quiz.getQuestions().size() * 100;

        return QuizDto.QuizGradeResponseDto.builder()
                .quizId(quiz.getId())
                .totalQuestions(quiz.getQuestions().size())
                .correctAnswers(correctCount)
                .score(score)
                .results(results)
                .build();
    }

    @SneakyThrows
    private QuizDto.QuestionResponseDto convertQuestionToDto(Question question) {
        List<String> options = objectMapper.readValue(question.getOptions(), new TypeReference<>() {});
        return QuizDto.QuestionResponseDto.builder()
                .questionId(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .options(options)
                .build();
    }
}