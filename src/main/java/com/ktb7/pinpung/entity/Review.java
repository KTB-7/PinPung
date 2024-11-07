package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.text.DateFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "Review")
@Getter
@Setter // for test
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long reviewId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "placeId", nullable = false)
    private Long placeId;

    @Column(name = "imageId")
    private Long imageId;

    @Column(name = "text")
    private String text;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
