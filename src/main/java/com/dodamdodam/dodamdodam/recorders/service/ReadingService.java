package com.dodamdodam.dodamdodam.recorders.service;

import com.dodamdodam.dodamdodam.recorders.dto.*;
import com.dodamdodam.dodamdodam.recorders.entity.Reading;
import com.dodamdodam.dodamdodam.recorders.repository.ReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Transactional 추가

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
@Transactional(readOnly = true) // 읽기 전용 메소드가 많으므로 클래스 레벨에 readOnly 설정
public class ReadingService {

    private final ReadingRepository readingRepository;

    public MonthlySummaryDto getMonthlySummary(Long userId, YearMonth month) {
        return getMonthlyReadingSummary(userId, month.getYear(), month.getMonthValue());
    }

    // --- [수정된 부분 시작] ---
    @Transactional // 데이터를 저장하므로 readOnly가 아닌 일반 Transactional 적용
    public CreateRecordResponseDto createRecord(CreateRecordRequestDto requestDto, Long userId) {

        // 1. 독서 시간을 먼저 계산합니다.
        Integer durationMinutes = 0;
        if (requestDto.getStartTime() != null && requestDto.getEndTime() != null) {
            // Duration.between().toMinutes()는 분 단위로만 계산하므로 ChronoUnit을 사용해 초 단위까지 고려할 수 있습니다.
            // 여기서는 분 단위로 충분하므로 toMinutes()를 유지합니다.
            durationMinutes = (int) Duration.between(requestDto.getStartTime(), requestDto.getEndTime()).toMinutes();
        }

        // 2. 빌더 패턴을 사용하여 Reading 엔티티를 생성합니다.
        Reading newRecord = Reading.builder()
                .userId(userId)
                .readDate(requestDto.getReadDate()) // DTO의 필드명과 일치시킴 (getDate -> getReadDate)
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .pagesRead(requestDto.getPagesRead())
                .thought(requestDto.getThought())
                .readDurationMinutes(durationMinutes) // 계산된 값을 빌더에 추가
                .readBookId(requestDto.getReadBookId())
                .readBookTitle(requestDto.getReadBookTitle())
                .isGoalAchieved(requestDto.getIsGoalAchieved())
                .isBookCompletedForThisLog(requestDto.getIsBookCompletedForThisLog())
                .build();

        Reading savedRecord = readingRepository.save(newRecord);

        return new CreateRecordResponseDto(savedRecord.getId(), "독서 기록이 성공적으로 생성되었습니다.");
    }
    // --- [수정된 부분 끝] ---


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