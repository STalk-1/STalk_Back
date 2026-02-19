package com.stalk.api.websocket.dto;

public record ChatSendRequest(
        String symbol,
        String content,
        String sender
) {
}
