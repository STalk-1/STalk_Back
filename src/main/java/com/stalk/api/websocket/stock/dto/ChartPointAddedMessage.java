package com.stalk.api.websocket.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "실시간 차트 지점 추가 이벤트 메시지")
public record ChartPointAddedMessage(
        @Schema(description = "메시지 타입", example = "CHART_POINT_ADDED")
        String type,

        @Schema(description = "종목 코드", example = "005930")
        String symbol,

        @Schema(description = "차트 업데이트 데이터")
        Data data
) {
    public static final String TYPE = "CHART_POINT_ADDED";

    /**
     * 정적 팩토리 메서드: 복잡한 계층 구조 생성을 단순화합니다.
     */
    public static ChartPointAddedMessage of(
            String symbol,
            String interval,
            OffsetDateTime time,
            BigDecimal close
    ) {
        return new ChartPointAddedMessage(
                TYPE,
                symbol,
                new Data(
                        interval,
                        new Point(time, close)
                )
        );
    }

    @Schema(description = "차트 업데이트 상세 정보")
    public record Data(
            @Schema(description = "차트 주기 (예: 1m, 5m, 1d)", example = "30m")
            String interval,

            @Schema(description = "새로 추가된 차트 지점")
            Point point
    ) {
    }

    @Schema(description = "차트 지점 데이터")
    public record Point(
            @Schema(description = "지점 생성 시각", example = "2026-03-06T09:12:00+09:00")
            OffsetDateTime time,

            @Schema(description = "종가 (해당 시점 가격)", example = "72500")
            BigDecimal close
    ) {
    }
}