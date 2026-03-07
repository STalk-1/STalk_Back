package com.stalk.api.websocket.chat.dto;

public record ChatSendRequest(
        String symbol,
        String content,
        String sender
) {
}
