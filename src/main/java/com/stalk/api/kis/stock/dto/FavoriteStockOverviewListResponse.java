package com.stalk.api.kis.stock.dto;

import com.stalk.api.kis.model.Direction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "관심 종목 요약 목록 응답")
public record FavoriteStockOverviewListResponse(
        @Schema(description = "관심 종목 상세 정보 목록")
        List<FavoriteStockOverviewItem> items,

        @Schema(description = "관심 종목 총 개수", example = "5")
        int count
) {
    @Schema(description = "관심 종목 개별 요약 정보")
    public record FavoriteStockOverviewItem(
            @Schema(description = "종목 코드", example = "005930")
            String symbol,

            @Schema(description = "현재 시세 정보")
            Quote quote,

            @Schema(description = "차트 데이터 정보")
            Chart chart
    ) {
    }

    @Schema(description = "종목 상세 시세(Quote)")
    public record Quote(
            @Schema(description = "종목명", example = "삼성전자")
            String name,

            @Schema(description = "상장 시장", example = "KOSPI")
            String market,

            @Schema(description = "현재가", example = "72500")
            long price,

            @Schema(description = "전일 대비 등락폭", example = "1200")
            long change,

            @Schema(description = "전일 대비 등락률 (%)", example = "1.68")
            double changeRate,

            @Schema(description = "주가 변동 방향", example = "UP")
            Direction direction,

            @Schema(description = "시세 기준 시각", example = "2026-03-06T15:30:00+09:00")
            OffsetDateTime asOf
    ) {
    }

    @Schema(description = "종목 요약 차트 정보")
    public record Chart(
            @Schema(description = "데이터 간격 (예: 1d, 1h, 10m)", example = "30m")
            String interval,

            @Schema(description = "차트 지점 데이터 목록")
            List<ChartPoint> points,

            @Schema(description = "차트 마지막 데이터 기준 시각", example = "2026-03-06T15:30:00+09:00")
            OffsetDateTime asOf
    ) {
    }

    @Schema(description = "차트 개별 지점 데이터")
    public record ChartPoint(
            @Schema(description = "해당 시점 시각", example = "2026-03-06T09:00:00+09:00")
            OffsetDateTime time,

            @Schema(description = "종가 (해당 시점 가격)", example = "71500")
            long close
    ) {
    }
}