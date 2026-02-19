package com.stalk.api.websocket.dto;

import java.time.Instant;

public record ChatMessageResponse(
        String messageId,
        String symbol,
        String content,
        String sender,
        Instant sentAt
        /**
         * localDateTime class 보다
         * 여러 사용자가 접속하는 채팅 서버는 Instant가 유리함
         * 기준 : UTC
         * // 사용자의 브라우저 설정에 맞춰 자동으로 변환됨
         * const localTime = new Date(serverTime).toLocaleString();
         */
) {
}
