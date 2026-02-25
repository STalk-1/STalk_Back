package com.stalk.api.market.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record StockSparklineResponse(
        String code,
        int days,
        String basis,                 // "CLOSE"
        List<BigDecimal> values       // 최근 N거래일 종가
) {
    public static StockSparklineResponse of(String code, int days, List<BigDecimal> values) {
        return StockSparklineResponse.builder()
                .code(code)
                .days(days)
                .basis("CLOSE")
                .values(values)
                .build();
    }
}