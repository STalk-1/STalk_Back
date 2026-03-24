package com.stalk.api.websocket.chat.presentation.dto;

public record ChatMessageResponse(
        String messageId,
        String symbol,
        String content,
        String sender,
        String sentAt
) {
}
