package com.dodamdodam.dodamdodam.quiz.controller;

import com.dodamdodam.dodamdodam.quiz.dto.QuizDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Quiz API", description = "독서 퀴즈 관련 API")
public interface QuizDocsController {

    @Operation(summary = "책에 대한 퀴즈 가져오기")
    ResponseEntity<QuizDto.QuizResponseDto> getQuizByBook(@PathVariable Long bookId);

    @Operation(summary = "퀴즈 답안 제출 및 채점")
    ResponseEntity<QuizDto.QuizGradeResponseDto> submitQuiz(
            @RequestBody QuizDto.QuizSubmitRequestDto submitRequest,
            Authentication authentication
    );
}