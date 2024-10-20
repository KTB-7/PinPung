package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

}
