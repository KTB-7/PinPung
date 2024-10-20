package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PlaceTag")
@Getter
public class PlaceTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long placeTagId;

    @Column(name = "tagId")
    private Long tagId;

    @Column(name = "placeId")
    private Long placeId;
}
