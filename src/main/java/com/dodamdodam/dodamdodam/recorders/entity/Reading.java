package com.dodamdodam.dodamdodam.recorders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate readDate;


    private LocalTime startTime;
    private LocalTime endTime;
    private Integer pagesRead;

    @Column(length = 1000)
    private String thought;

    private Integer readDurationMinutes;

    private Long readBookId;
    private String readBookTitle;

    private Boolean isGoalAchieved;

    private Boolean isBookCompletedForThisLog;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isBookCompletedForThisLog == null) {
            this.isBookCompletedForThisLog = false;
        }
        if (this.isGoalAchieved == null) {
            this.isGoalAchieved = false;
        }
        if (this.readDurationMinutes == null) {
            this.readDurationMinutes = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}