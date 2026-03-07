package com.stalk.api.websocket.stock.dto;

import com.stalk.api.kis.model.Direction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "시세 업데이트 이벤트 메시지 (WebSocket/Push용)")
public record QuoteUpdatedMessage(

        @Schema(description = "메시지 타입", example = "QUOTE_UPDATED")
        String type,

        @Schema(description = "종목 코드", example = "005930")
        String symbol,

        @Schema(description = "실시간 시세 데이터")
        Data data
) {
    public static final String TYPE = "QUOTE_UPDATED";

    /**
     * 정적 팩토리 메서드: 메시지 생성을 캡슐화합니다.
     */
    public static QuoteUpdatedMessage of(
            String symbol,
            BigDecimal price,
            BigDecimal change,
            BigDecimal changeRate,
            Direction direction,
            OffsetDateTime asOf
    ) {
        return new QuoteUpdatedMessage(
                TYPE,
                symbol,
                new Data(price, change, changeRate, direction, asOf)
        );
    }

    @Schema(description = "업데이트된 시세 상세 정보")
    public record Data(
            @Schema(description = "현재가", example = "172500")
            BigDecimal price,

            @Schema(description = "전일 대비 등락폭", example = "1200")
            BigDecimal change,

            @Schema(description = "전일 대비 등락률 (%)", example = "1.68")
            BigDecimal changeRate,

            @Schema(description = "주가 변동 방향 (UP, DOWN, FLAT)", example = "UP")
            Direction direction,

            @Schema(description = "데이터 기준 시각", example = "2026-03-06T15:30:00+09:00")
            OffsetDateTime asOf
    ) {
    }
}