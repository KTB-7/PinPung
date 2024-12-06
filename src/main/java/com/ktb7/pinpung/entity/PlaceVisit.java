package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "PlaceVisit")
@Getter
public class PlaceVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "placeId", nullable = false)
    private Long placeId;

    @Column(name = "visit", nullable = false)
    private Integer visit;

    @Column(name = "age", nullable = false, columnDefinition = "FLOAT DEFAULT 0")
    private Float age;

    @ManyToOne
    @JoinColumn(name = "placeId", insertable = false, updatable = false)
    private Place place;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
