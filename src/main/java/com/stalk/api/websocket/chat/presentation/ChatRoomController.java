package com.stalk.api.websocket.chat.presentation;

import com.stalk.api.global.ApiResponse;
import com.stalk.api.kis.stock.StockMasterProvider;
import com.stalk.api.websocket.chat.application.ChatRoomConnectionManager;
import com.stalk.api.websocket.chat.presentation.dto.ChatRoomInfoResponse;
import com.stalk.api.websocket.chat.presentation.dto.ChatRoomUserCountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "채팅방 내부 정보 조회", description = "채팅방 내부 정보 조회")
@RestController
@RequestMapping("/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomConnectionManager connectionManager;
    private final StockMasterProvider stockMasterProvider;

    @Operation(summary = "채팅방 참여 인원수 조히", description = "채팅방에 참여한 인원 수를 조회합니다.")
    @GetMapping("/{symbol}/count")
    public ApiResponse<ChatRoomUserCountResponse> getConnectedUserCount(
            @Parameter(description = "종목 코드 (예: 005930)", example = "005930") @PathVariable String symbol) {
        int count = connectionManager.getConnectedUserCount(symbol);
        return ApiResponse.ok(new ChatRoomUserCountResponse(symbol, count));
    }

    @Operation(summary = "채팅방 종목 정보 조회", description = "채팅방의 종목 정보를 조회합니다.")
    @GetMapping("/{symbol}/info")
    public ApiResponse<ChatRoomInfoResponse> getChatRoomInfo(
            @Parameter(description = "종목 코드 (예: 005930)", example = "005930") @PathVariable String symbol) {
        var master = stockMasterProvider.getStockMasterFromCode(symbol);
        return ApiResponse.ok(new ChatRoomInfoResponse(symbol, master.name(), master.market()));
    }
}
