package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Pung.PungsResponseDto;
import com.ktb7.pinpung.dto.Pung.UploadPungRequestDto;
import com.ktb7.pinpung.dto.Review.MessageResponseDto;
import com.ktb7.pinpung.oauth2.service.TokenService;
import com.ktb7.pinpung.service.PungService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Slf4j
@RequestMapping("/api/pungs")
@AllArgsConstructor
@Tag(name = "Pung API", description = "펑(Pung) 관련 API")
public class PungController {

    private final PungService pungService;
    private final TokenService tokenService;

    @GetMapping("/byPlace/{placeId}")
    @Operation(
            summary = "특정 장소의 펑 목록 조회",
            description = "주어진 장소 ID에 대한 펑을 페이징 처리하여 반환합니다.",
            parameters = {
                    @Parameter(name = "placeId", description = "조회할 장소의 ID", required = true, example = "123"),
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
                    @Parameter(name = "size", description = "한 페이지에 포함될 데이터 수", example = "3")
            }
    )
    public ResponseEntity<PungsResponseDto> getPungsByPlace(
            @PathVariable Long placeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        // 유효성 검증
        ValidationUtils.validatePlaceId(placeId);
        ValidationUtils.validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        PungsResponseDto pungs = pungService.getPungsByPlaceId(placeId, pageable);
        return ResponseEntity.ok(pungs);
    }

    @PostMapping
    @Operation(
            summary = "펑 업로드",
            description = "사용자가 업로드한 펑 데이터를 처리합니다.",
            parameters = {
                    @Parameter(name = "uploadPungRequest", description = "펑 업로드 요청 데이터 (userId, placeId, text, 이미지 파일 두가지)", required = true)
            }
    )
    public ResponseEntity<MessageResponseDto> uploadPungs(@RequestHeader("Authorization") String authorizationHeader, @ModelAttribute UploadPungRequestDto uploadPungRequest) {
        log.info("uploadPungs: {} {}", uploadPungRequest.getPlaceId(), uploadPungRequest.getText());

        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        // 유효성 검증
        ValidationUtils.validateUserAndPlaceId(userId, uploadPungRequest.getPlaceId());
        ValidationUtils.validateFile(uploadPungRequest.getImageWithText(), "imageWithText");
        ValidationUtils.validateFile(uploadPungRequest.getPureImage(), "pureImage");

        MessageResponseDto response = pungService.uploadPung(userId, uploadPungRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/byUser")
    @Operation(
            summary = "특정 유저의 펑 목록 조회",
            description = "주어진 유저 ID에 대한 펑을 페이징 처리하여 반환합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
                    @Parameter(name = "size", description = "한 페이지에 포함될 데이터 수", example = "3")
            }
    )
    public ResponseEntity<PungsResponseDto> getPungsByUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        log.info("getPungsByUser");

        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        // 유효성 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        PungsResponseDto pungs = pungService.getPungsByUserId(userId, pageable);
        return ResponseEntity.ok(pungs);
    }
}
