package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Pung")
@Getter
@Setter
public class Pung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long pungId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "placeId", nullable = false)
    private Long placeId;

    @Column(name = "imageId")
    private Long imageId;

    @Column(name = "text")
    private String text;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

}

