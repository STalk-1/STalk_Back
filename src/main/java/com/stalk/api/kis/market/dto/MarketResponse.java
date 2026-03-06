package com.stalk.api.kis.market.dto;

import com.stalk.api.kis.model.Direction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "메인 시장 지수 응답")
public record MarketResponse(

        @Schema(description = "데이터 갱신 시각", example = "2026-03-06T09:12:30+09:00")
        OffsetDateTime updatedAt,

        @Schema(description = "시장 목록")
        List<MarketItem> markets
) {

    @Schema(description = "개별 시장 정보")
    public record MarketItem(

            @Schema(description = "시장 이름", example = "KOSPI")
            String name,

            @Schema(description = "현재 값", example = "2550.72")
            BigDecimal value,

            @Schema(description = "전일 대비", example = "15.26")
            BigDecimal change,

            @Schema(description = "등락률", example = "0.59")
            BigDecimal changeRate,

            @Schema(description = "등락 방향", example = "DOWN")
            Direction direction
    ) { }
}
