package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Place")
@Getter
@Setter // for test
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long placeId;

    @Column(name = "placeId")
    private String kakaoPlaceId;

    @Column(name = "placeName")
    private String placeName;

    @Column(name = "address")
    private String address;

    @Column(name = "x")
    private String x;

    @Column(name = "y")
    private String y;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
