package com.stalk.api.auth.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class KakaoOAuthExceptionHandler {

    @ExceptionHandler(KakaoApiException.class)
    public ResponseEntity<?> handleKakaoApi(KakaoApiException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("KAKAO_API_ERROR", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("OAUTH_ERROR", e.getMessage()));
    }

    public record ErrorResponse(String code, String message) {}

}
