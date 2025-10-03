package com.dodamdodam.dodamdodam.quiz.service;

import com.dodamdodam.dodamdodam.login.entity.User;
import com.dodamdodam.dodamdodam.login.repository.UserRepository;
import com.dodamdodam.dodamdodam.quiz.dto.QuizDto;
import com.dodamdodam.dodamdodam.quiz.entity.Question;
import com.dodamdodam.dodamdodam.quiz.entity.Quiz;
import com.dodamdodam.dodamdodam.quiz.entity.QuizOption;
import com.dodamdodam.dodamdodam.quiz.entity.QuizSubmission;
import com.dodamdodam.dodamdodam.quiz.repository.QuestionRepository;
import com.dodamdodam.dodamdodam.quiz.repository.QuizOptionRepository;
import com.dodamdodam.dodamdodam.quiz.repository.QuizRepository;
import com.dodamdodam.dodamdodam.quiz.repository.QuizSubmissionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final UserRepository userRepository;
    private final QuizSubmissionRepository submissionRepository;

    // 퀴즈 조회 (ObjectMapper 제거, 더 효율적으로 변경)
    public QuizDto.QuizResponseDto getQuizForBook(Long bookId) {
        Quiz quiz = quizRepository.findByBookId(bookId)
                .orElseThrow(() -> new EntityNotFoundException("해당 책에 대한 퀴즈를 찾을 수 없습니다."));

        List<QuizDto.QuestionResponseDto> questionDtos = quiz.getQuestions().stream()
                .map(question -> QuizDto.QuestionResponseDto.builder()
                        .questionId(question.getId())
                        .questionText(question.getQuestionText())
                        .questionType(question.getQuestionType())
                        .options(question.getOptions().stream()
                                .map(option -> QuizDto.OptionDto.builder()
                                        .optionId(option.getId())
                                        .optionText(option.getOptionText())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return QuizDto.QuizResponseDto.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .questions(questionDtos)
                .build();
    }

    // 채점 및 제출 기록 저장 (훨씬 안전하고 강력하게 변경)
    @Transactional
    public QuizDto.QuizGradeResponseDto submitAnswer(QuizDto.QuizSubmitRequestDto request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));
        Question question = questionRepository.findById(request.getQuestionId()).orElseThrow(() -> new EntityNotFoundException("문제를 찾을 수 없습니다."));
        QuizOption selectedOption = quizOptionRepository.findById(request.getSelectedOptionId()).orElseThrow(() -> new EntityNotFoundException("보기를 찾을 수 없습니다."));

        // ✅ ID로 안전하게 정답 확인
        boolean isCorrect = selectedOption.isCorrect();

        // ✅ 사용자 응답 기록을 DB에 저장!
        QuizSubmission submission = QuizSubmission.builder()
                .user(user)
                .question(question)
                .selectedOption(selectedOption)
                .isCorrect(isCorrect)
                .submittedAt(LocalDateTime.now())
                .build();
        submissionRepository.save(submission);

        return QuizDto.QuizGradeResponseDto.builder()
                .questionId(question.getId())
                .selectedOptionId(selectedOption.getId())
                .isCorrect(isCorrect)
                .message(isCorrect ? "정답입니다!" : "아쉬워요, 다음엔 맞출 수 있을 거예요!")
                .build();
    }
}