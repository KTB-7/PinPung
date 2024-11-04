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
    @Column(name = "id")
    private Long reviewId;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "placeId")
    private Long placeId;

    @Column(name = "imageId")
    private Long imageId;

    @Column(name = "text")
    private String text;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
