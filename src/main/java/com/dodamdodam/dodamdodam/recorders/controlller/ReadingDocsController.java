package com.dodamdodam.dodamdodam.recorders.controlller;

import com.dodamdodam.dodamdodam.recorders.dto.CreateRecordRequestDto;
import com.dodamdodam.dodamdodam.recorders.dto.CreateRecordResponseDto;
import com.dodamdodam.dodamdodam.recorders.dto.MonthlySummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;

@Tag(name = "Reading API", description = "독서 기록 및 요약 조회 API")
public interface ReadingDocsController {

    @Operation(summary = "월별 독서 요약 조회", description = "특정 사용자의 월별 독서 기록과 통계를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "인증 실패 (토큰 없음 또는 유효하지 않은 토큰)")
    })
    ResponseEntity<MonthlySummaryDto> getMonthlySummary(
            @Parameter(description = "사용자 ID", required = true, example = "1") Long userId,
            @Parameter(description = "조회할 연월 (YYYY-MM 형식)", required = true, example = "2025-09") YearMonth month
    );


    @Operation(summary = "독서 기록 생성", description = "새로운 독서 기록을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "기록 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "인증 실패 (토큰 없음 또는 유효하지 않은 토큰)")
    })
    ResponseEntity<CreateRecordResponseDto> createRecord(
            @RequestBody CreateRecordRequestDto requestDto,
            @Parameter(description = "사용자 ID", required = true, example = "1") Long userId
    );
}