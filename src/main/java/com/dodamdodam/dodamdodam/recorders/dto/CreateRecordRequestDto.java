package com.dodamdodam.dodamdodam.recorders.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateRecordRequestDto {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer pagesRead;
    private String thought;
}