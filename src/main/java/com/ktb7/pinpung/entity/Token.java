package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long tokenId;

    @Column(name="userId")
    private Long userId;

    @Column(name="refreshToken")
    private String refreshToken;

    @Column(name="accessToken")
    private String accessToken;

    @Column(name="expiresIn")
    private Integer expiresIn;
}
