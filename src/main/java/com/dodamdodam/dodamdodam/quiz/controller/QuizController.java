package com.dodamdodam.dodamdodam.quiz.controller;

import com.dodamdodam.dodamdodam.jwt.service.CustomUserDetails;
import com.dodamdodam.dodamdodam.quiz.dto.QuizDto;
import com.dodamdodam.dodamdodam.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /**
     * 퀴즈 제출 및 채점 API
     * @AuthenticationPrincipal 어노테이션을 사용하여 인증된 사용자의 정보를 받아옵니다.
     * 이를 통해 NullPointerException을 방지하고 코드를 더 안전하게 만듭니다.
     */
    @Override
    @PostMapping("/submit")
    public ResponseEntity<QuizDto.QuizGradeResponseDto> submitQuiz(
            @RequestBody QuizDto.QuizSubmitRequestDto submitRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // @AuthenticationPrincipal을 사용하면 userDetails가 null이 되는 경우가 없으므로
        // 안전하게 사용자 ID를 가져올 수 있습니다.
        Long userId = userDetails.getUserId();

        QuizDto.QuizGradeResponseDto grade = quizService.gradeQuiz(submitRequest, userId);
        return ResponseEntity.ok(grade);
    }
}