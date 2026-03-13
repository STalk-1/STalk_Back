package com.stalk.api.websocket.chat.presentation.dto;

import java.time.Instant;

public record ChatMessageResponse(
        String messageId,
        String symbol,
        String content,
        String sender,
        String sentAt
) {
}
