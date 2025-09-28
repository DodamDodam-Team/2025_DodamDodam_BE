package com.dodamdodam.dodamdodam.recorders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryDto {
    private String currentMonthName;
    private Long currentMonthTotalReadDuration;
    private Integer currentMonthTotalCompletedBooks;
    private Integer daysSinceFirstRead;
    private Double previousMonthDurationChangeRatio;

    private Map<LocalDate, ReadingDto> dailyStats;

    private List<UserRankingDto> problemRanking;
}