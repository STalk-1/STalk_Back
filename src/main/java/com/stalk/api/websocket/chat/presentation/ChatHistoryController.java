package com.stalk.api.websocket.chat.presentation;

import com.stalk.api.global.ApiResponse;
import com.stalk.api.websocket.chat.application.ChatHistoryService;
import com.stalk.api.websocket.chat.presentation.dto.ChatMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "채팅 내역 조회", description = "채팅방 과거 메시지 내역 조회")
@RestController
@RequestMapping("/chat/rooms")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    @Operation(summary = "채팅방 과거 메시지 조회", description = "채팅방의 최근 50개 메시지를 시간 오름차순으로 조회합니다.")
    @GetMapping("/{symbol}/history")
    public ApiResponse<List<ChatMessageResponse>> getChatHistory(
            @Parameter(description = "종목 코드 (예: 005930)", example = "005930") @PathVariable String symbol) {
        
        List<ChatMessageResponse> history = chatHistoryService.getRecentMessages(symbol);
        return ApiResponse.ok(history);
    }
}
