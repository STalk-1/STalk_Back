package com.stalk.api.websocket.chat.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 주식 정보 응답")
public record ChatRoomInfoResponse(
    @Schema(description = "종목 코드", example = "005930")
    String symbol,

    @Schema(description = "종목명", example = "삼성전자")
    String name,

    @Schema(description = "상장 시장", example = "KOSPI")
    String market) {
}
