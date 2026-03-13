package com.stalk.api.websocket.chat.presentation;

import com.stalk.api.global.ApiResponse;
import com.stalk.api.websocket.chat.application.ChatRoomConnectionManager;
import com.stalk.api.websocket.chat.presentation.dto.ChatRoomUserCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomConnectionManager connectionManager;

    @GetMapping("/{symbol}/count")
    public ApiResponse<ChatRoomUserCountResponse> getConnectedUserCount(@PathVariable String symbol) {
        int count = connectionManager.getConnectedUserCount(symbol);
        return ApiResponse.ok(new ChatRoomUserCountResponse(symbol, count));
    }
}
