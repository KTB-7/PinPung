//package com.ktb7.pinpung.service;
//
//import com.ktb7.pinpung.exception.common.CustomException;
//import com.ktb7.pinpung.exception.common.ErrorCode;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import org.springframework.core.ParameterizedTypeReference;
//
//import java.util.*;
//
//@Service
//@Slf4j
//public class SearchService {
//
//    @Value("${openai.api.key}")
//    private String openaiApiKey;
//
//    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
//
//    private final WebClient webClient = WebClient.builder().build();
//
//    public Boolean useGpt(String keyword) {
//        // 프롬프트 생성
//        String prompt = "다음 키워드에 위치 정보가 있으면 '1'만을, 없으면 '0'만을 응답하세요: \"" + keyword + "\"";
//
//        // 요청 바디 구성
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "gpt-3.5-turbo");
//
//        List<Map<String, String>> messages = new ArrayList<>();
//        messages.add(Map.of("role", "user", "content", prompt));
//        requestBody.put("messages", messages);
//
//        try {
//            // OpenAI API 호출
//            Map<String, Object> response = webClient.post()
//                    .uri(OPENAI_API_URL)
//                    .header("Authorization", "Bearer " + openaiApiKey)
//                    .header("Content-Type", "application/json")
//                    .bodyValue(requestBody)
//                    .retrieve()
//
//                    //ParameterizedTypeReference는 복잡한 제네릭 타입을 처리하기 위해 사용
//                    //HTTP 응답 본문이 Map<String, Object> 구조라고 Spring에게 알려줌
//                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
//                    })
//                    .block();
//
//            // 응답에서 "choices" 가져오기
//            if (response == null || !response.containsKey("choices")) {
//                log.error("GPT 응답에서 'choices'가 누락되었습니다.");
//                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "GPT 응답에 'choices'가 없습니다.");
//            }
//
//            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
//            if (choices == null || choices.isEmpty()) {
//                log.error("GPT 응답에서 선택지가 없습니다.");
//                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "GPT 응답에 선택지가 없습니다.");
//            }
//
//            // 첫 번째 선택지에서 "message" 가져오기
//            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
//            if (message == null || !message.containsKey("content")) {
//                log.error("GPT 응답에서 'message' 또는 'content'가 누락되었습니다.");
//                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "GPT 응답에 'message' 또는 'content'가 없습니다.");
//            }
//
//            String content = ((String) message.get("content")).trim();
//
//            // 결과 처리
//            if ("1".equals(content)) {
//                return true;
//            } else if ("0".equals(content)) {
//                return false;
//            } else {
//                log.warn("예상치 못한 GPT 응답: {}", content);
//                return false;
//            }
//
//        } catch (CustomException e) {
//            log.error("OpenAI API 호출 중 알 수 없는 오류 발생", e);
//            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNEXPECTED_ERROR, "OpenAI API 호출 중 오류가 발생했습니다.");
//        }
//    }
//
////    public
//
//
//
//}