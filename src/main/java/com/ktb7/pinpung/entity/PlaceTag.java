package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "PlaceTag")
@Getter
public class PlaceTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long placeTagId;

    @Column(name = "tagId", nullable = false)
    private Long tagId;

    @Column(name = "placeId", nullable = false)
    private Long placeId;

    @Column(name = "tagCount", nullable = false)
    private Long tagCount;

    @Column(name = "isRepresentative", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean isRepresentative;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
