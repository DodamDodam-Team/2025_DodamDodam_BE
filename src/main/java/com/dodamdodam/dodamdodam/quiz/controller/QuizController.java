package com.dodamdodam.dodamdodam.quiz.controller;

import com.dodamdodam.dodamdodam.jwt.service.CustomUserDetails;
import com.dodamdodam.dodamdodam.quiz.dto.QuizDto;
import com.dodamdodam.dodamdodam.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
@Tag(name = "Quiz API", description = "독서 퀴즈 관련 API")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/book/{bookId}")
    @Operation(summary = "책에 대한 퀴즈 가져오기")
    public ResponseEntity<QuizDto.QuizResponseDto> getQuizByBook(@PathVariable Long bookId) {
        QuizDto.QuizResponseDto quiz = quizService.getQuizForBook(bookId);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/submit")
    @Operation(summary = "퀴즈 답안 제출 및 채점")
    public ResponseEntity<QuizDto.QuizGradeResponseDto> submitQuiz(
            @RequestBody QuizDto.QuizSubmitRequestDto submitRequest,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        QuizDto.QuizGradeResponseDto grade = quizService.gradeQuiz(submitRequest, userId);
        return ResponseEntity.ok(grade);
    }
}