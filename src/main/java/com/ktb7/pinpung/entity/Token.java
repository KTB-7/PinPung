package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
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
