package com.ktb7.pinpung.dto.AI;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GenerateTagsAIRequestDto {
    private Long place_id;
    private String review_text;
    private String review_image_url;
    private Long user_id;
}
