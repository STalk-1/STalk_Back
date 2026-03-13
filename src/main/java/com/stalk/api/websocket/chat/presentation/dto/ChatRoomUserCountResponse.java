package com.stalk.api.websocket.chat.presentation.dto;

public record ChatRoomUserCountResponse(
        String symbol,
        int count
) {
}
