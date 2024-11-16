package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "Token")
@Setter
@Getter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long tokenId;

    @Column(name="userId", nullable = false)
    private Long userId;

    @Column(name="refreshToken", nullable = false)
    private String refreshToken;

    @Column(name="expiresIn")
    private Integer expiresIn;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
