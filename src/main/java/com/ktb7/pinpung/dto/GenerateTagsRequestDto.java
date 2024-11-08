package com.ktb7.pinpung.dto;

import lombok.Getter;

@Getter
public class GenerateTagsRequestDto {
    private Long place_id;
    private String review_text;
    private String review_image_url;

}
