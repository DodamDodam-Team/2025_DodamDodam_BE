package com.dodamdodam.dodamdodam.recorders.controlller;

import com.dodamdodam.dodamdodam.login.controller.UserDocsController;
import com.dodamdodam.dodamdodam.recorders.dto.CreateRecordRequestDto;
import com.dodamdodam.dodamdodam.recorders.dto.CreateRecordResponseDto;
import com.dodamdodam.dodamdodam.recorders.dto.MonthlySummaryDto;
import com.dodamdodam.dodamdodam.recorders.service.ReadingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReadingController implements ReadingDocsController {

    private final ReadingService readingService;

    @Override
    @GetMapping("/summary/monthly/users/{userId}")
    public ResponseEntity<MonthlySummaryDto> getMonthlySummary(
            @PathVariable Long userId,
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

        log.info("월별 독서 요약 조회 요청 - userId: {}, month: {}", userId, month);
        MonthlySummaryDto responseDto = readingService.getMonthlySummary(userId, month);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    @PostMapping("/records/users/{userId}")
    public ResponseEntity<CreateRecordResponseDto> createRecord(
            @RequestBody CreateRecordRequestDto requestDto,
            @PathVariable Long userId) {

        log.info("독서 기록 생성 요청: userId={}, date={}", userId, requestDto.getDate());

        CreateRecordResponseDto responseDto = readingService.createRecord(requestDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}