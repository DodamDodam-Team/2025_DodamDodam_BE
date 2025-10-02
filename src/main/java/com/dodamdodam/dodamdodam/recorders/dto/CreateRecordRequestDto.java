package com.dodamdodam.dodamdodam.recorders.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateRecordRequestDto {

    private LocalDate readDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer pagesRead;
    private String thought;

    private Long readBookId;
    private String readBookTitle;
    private Boolean isGoalAchieved;
    private Boolean isBookCompletedForThisLog;
}