package com.dodamdodam.dodamdodam.recorders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRankingDto {
    private Integer rank;
    private Long userId;
    private String userName;
    private Integer score;
}