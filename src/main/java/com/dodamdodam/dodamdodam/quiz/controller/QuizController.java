package com.dodamdodam.dodamdodam.quiz.controller;

import com.dodamdodam.dodamdodam.jwt.service.CustomUserDetails;
import com.dodamdodam.dodamdodam.quiz.dto.QuizDto;
import com.dodamdodam.dodamdodam.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController implements QuizDocsController {

    private final QuizService quizService;

    @Override
    @GetMapping("/book/{bookId}")
    public ResponseEntity<QuizDto.QuizResponseDto> getQuizByBook(@PathVariable Long bookId) {
        QuizDto.QuizResponseDto quiz = quizService.getQuizForBook(bookId);
        return ResponseEntity.ok(quiz);
    }

    @Override
    @PostMapping("/submit")
    public ResponseEntity<QuizDto.QuizGradeResponseDto> submitQuiz(
            @RequestBody QuizDto.QuizSubmitRequestDto submitRequest,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        QuizDto.QuizGradeResponseDto grade = quizService.gradeQuiz(submitRequest, userId);
        return ResponseEntity.ok(grade);
    }
}