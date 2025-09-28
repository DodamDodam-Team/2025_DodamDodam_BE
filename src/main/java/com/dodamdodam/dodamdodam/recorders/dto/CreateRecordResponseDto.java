package com.dodamdodam.dodamdodam.recorders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecordResponseDto {
    private Long recordId;
    private String message;
}
