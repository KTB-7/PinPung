package com.ktb7.pinpung.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Image")
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long imageId;

    private String imageTextKey;  // S3에 저장된 텍스트 이미지 파일의 경로 (key)
    private String pureImageKey;  // S3에 저장된 순수 이미지 파일의 경로 (key)
    private Long id;

}
