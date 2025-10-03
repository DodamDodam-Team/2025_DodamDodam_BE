package com.dodamdodam.dodamdodam.quiz.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private String optionText; // 보기 내용 (예: "B-612")

    @Column(nullable = false)
    private boolean isCorrect; // 이 보기가 정답인지 여부 (true/false)
}