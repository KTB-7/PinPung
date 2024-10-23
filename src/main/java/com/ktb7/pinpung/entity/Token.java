package com.ktb7.pinpung.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Setter;

@Setter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long tokenId;

    @Column(name="userId")
    private Long userId;

    @Column(name="refreshToken")
    private String refreshToken;

    @Column(name="expiresIn")
    private Integer expiresIn;
}
