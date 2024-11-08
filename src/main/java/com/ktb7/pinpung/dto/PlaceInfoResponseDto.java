package com.ktb7.pinpung.dto;

import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlaceInfoResponseDto {
    private Long placeId;
    private String placeName;
    private String address;
    private List<String> tags;
    private ReviewsDto reviews;
    private Pung representativePung;
    private byte[] imageUrl;
}
