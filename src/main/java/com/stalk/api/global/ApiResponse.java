package com.stalk.api.global;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    private final String status;   // SUCCESS 또는 ERROR
    private final String message;  // 응답 메시지
    private final T data;          // 실제 담고 싶은 데이터 (Generic)
    private final LocalDateTime timestamp;

    private ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 성공 응답 정적 팩토리 메서드
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("SUCCESS", "요청이 성공적으로 처리되었습니다.", data);
    }

    // 실패 응답 정적 팩토리 메서드
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>("ERROR", message, null);
    }
}
