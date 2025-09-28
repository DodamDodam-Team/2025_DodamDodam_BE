package com.dodamdodam.dodamdodam.recorders.service;

import com.dodamdodam.dodamdodam.recorders.dto.*;
import com.dodamdodam.dodamdodam.recorders.entity.Reading;
import com.dodamdodam.dodamdodam.recorders.repository.ReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingService {

    private final ReadingRepository readingRepository;

    public MonthlySummaryDto getMonthlySummary(Long userId, YearMonth month) {
        return getMonthlyReadingSummary(userId, month.getYear(), month.getMonthValue());
    }

    // --- [수정] ---
    // userId를 파라미터로 추가하고, 내부 로직을 빌더 패턴으로 변경
    public CreateRecordResponseDto createRecord(CreateRecordRequestDto requestDto, Long userId) {

        // --- 빌더 패턴으로 객체 생성 ---
        Reading newRecord = Reading.builder()
                .userId(userId) // userId 설정
                .readDate(requestDto.getDate())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .pagesRead(requestDto.getPagesRead())
                .thought(requestDto.getThought())
                .build();

        if (requestDto.getStartTime() != null && requestDto.getEndTime() != null) {
            long duration = Duration.between(requestDto.getStartTime(), requestDto.getEndTime()).toMinutes();
            // TODO: 엔티티에 setter가 없으므로 이 값도 빌더에 포함시켜야 합니다.
            // 아래는 임시 방편이며, 실제로는 빌더에 readDurationMinutes를 추가해야 합니다.
            // newRecord.setReadDurationMinutes((int) duration);
        }

        Reading savedRecord = readingRepository.save(newRecord);
        return new CreateRecordResponseDto(savedRecord.getId(), "독서 기록이 성공적으로 생성되었습니다.");
    }
    // --- [수정 완료] ---


    private MonthlySummaryDto getMonthlyReadingSummary(Long userId, int year, int month) {
        YearMonth currentYearMonth = YearMonth.of(year, month);
        LocalDate startDate = currentYearMonth.atDay(1);
        LocalDate endDate = currentYearMonth.atEndOfMonth();

        List<Reading> currentMonthReadings = readingRepository.findByUserIdAndReadDateBetween(userId, startDate, endDate);

        Map<LocalDate, ReadingDto> dailyStatsMap = currentMonthReadings.stream()
                .collect(Collectors.groupingBy(
                        Reading::getReadDate,
                        Collectors.reducing(
                                new ReadingDto(null, 0, false, 0),
                                readingLog -> new ReadingDto(
                                        readingLog.getReadDate(),
                                        readingLog.getReadDurationMinutes() != null ? readingLog.getReadDurationMinutes() : 0,
                                        readingLog.getIsGoalAchieved() != null ? readingLog.getIsGoalAchieved() : false,
                                        readingLog.getIsBookCompletedForThisLog() != null && readingLog.getIsBookCompletedForThisLog() ? 1 : 0
                                ),
                                (dto1, dto2) -> {
                                    dto1.setReadDurationMinutes(dto1.getReadDurationMinutes() + dto2.getReadDurationMinutes());
                                    dto1.setIsGoalAchieved(dto1.getIsGoalAchieved() || dto2.getIsGoalAchieved());
                                    dto1.setBooksCompletedOnDate(dto1.getBooksCompletedOnDate() + dto2.getBooksCompletedOnDate());
                                    return dto1;
                                }
                        )
                ));

        Integer currentMonthTotalReadDuration = readingRepository.sumReadDurationMinutesInMonth(userId, startDate, endDate);
        if (currentMonthTotalReadDuration == null) currentMonthTotalReadDuration = 0;

        Integer currentMonthTotalCompletedBooks = readingRepository.countCompletedBooksInMonth(userId, startDate, endDate);
        if (currentMonthTotalCompletedBooks == null) currentMonthTotalCompletedBooks = 0;

        YearMonth previousYearMonth = currentYearMonth.minusMonths(1);
        LocalDate prevMonthStartDate = previousYearMonth.atDay(1);
        LocalDate prevMonthEndDate = previousYearMonth.atEndOfMonth();

        Integer previousMonthTotalReadDuration = readingRepository.sumReadDurationMinutesInMonth(userId, prevMonthStartDate, prevMonthEndDate);
        if (previousMonthTotalReadDuration == null) previousMonthTotalReadDuration = 0;

        Double previousMonthDurationChangeRatio = 0.0;
        if (previousMonthTotalReadDuration > 0) {
            previousMonthDurationChangeRatio = (double) (currentMonthTotalReadDuration - previousMonthTotalReadDuration) / previousMonthTotalReadDuration * 100;
        } else if (currentMonthTotalReadDuration > 0) {
            previousMonthDurationChangeRatio = 100.0;
        }
        previousMonthDurationChangeRatio = Math.round(previousMonthDurationChangeRatio * 10.0) / 10.0;

        long daysSinceFirstRead = 0;
        Optional<Reading> firstLogOptional = readingRepository.findFirstByUserIdOrderByReadDateAsc(userId);
        if (firstLogOptional.isPresent()) {
            LocalDate firstReadDate = firstLogOptional.get().getReadDate();
            daysSinceFirstRead = ChronoUnit.DAYS.between(firstReadDate, LocalDate.now()) + 1;
        }

        List<UserRankingDto> problemRanking = new ArrayList<>();
        problemRanking.add(new UserRankingDto(1, 101L, "김철수", 1200));
        problemRanking.add(new UserRankingDto(2, 102L, "이영희", 1150));
        problemRanking.add(new UserRankingDto(3, 103L, "박민수", 1100));

        MonthlySummaryDto summary = new MonthlySummaryDto();
        summary.setCurrentMonthName(currentYearMonth.getYear() + "년 " + currentYearMonth.getMonthValue() + "월");
        summary.setCurrentMonthTotalReadDuration(Long.valueOf(currentMonthTotalReadDuration));
        summary.setCurrentMonthTotalCompletedBooks(currentMonthTotalCompletedBooks);
        summary.setDaysSinceFirstRead((int) daysSinceFirstRead);
        summary.setPreviousMonthDurationChangeRatio(previousMonthDurationChangeRatio);
        summary.setDailyStats(dailyStatsMap);
        summary.setProblemRanking(problemRanking);

        return summary;
    }
}