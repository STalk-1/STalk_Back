package com.stalk.api.kis.stock.dto;

import com.stalk.api.kis.model.Direction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "메인 주식 랭킹 응답")
public record StockRankingResponse(
        @Schema(description = "랭킹 제목", example = "거래량 급등 TOP 10")
        String title,

        @Schema(description = "데이터 조회 시각", example = "2026-03-06T09:12:30+09:00")
        OffsetDateTime lastPolledAt,

        @Schema(description = "랭킹 아이템 목록")
        List<StockRankingItem> items,

        @Schema(description = "전체 아이템 개수", example = "10")
        int count

        ) {
        @Schema(description = "주식 랭킹 개별 아이템")
        public record StockRankingItem(

                @Schema(description = "순위", example = "1")
                int rank,

                @Schema(description = "종목 코드", example = "005930")
                String symbol,

                @Schema(description = "종목명", example = "삼성전자")
                String name,

                @Schema(description = "상장 시장", example = "KOSPI")
                String market,

                @Schema(description = "현재가", example = "72500")
                BigDecimal price,

                @Schema(description = "전일 대비 등락폭", example = "1200")
                BigDecimal change,

                @Schema(description = "전일 대비 등락률 (%)", example = "1.68")
                BigDecimal changeRate,

                @Schema(description = "주가 변동 방향", example = "UP")
                Direction direction
        ) { }
}
