package com.dodamdodam.dodamdodam.recorders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingDto {

    private LocalDate readDate;
    private Integer readDurationMinutes;
    private Boolean isGoalAchieved;
    private Integer booksCompletedOnDate;

}
