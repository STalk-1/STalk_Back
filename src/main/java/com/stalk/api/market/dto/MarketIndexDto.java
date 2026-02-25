package com.stalk.api.market.dto;

import com.stalk.api.market.Domain.ChangeDirection;
import com.stalk.api.market.Domain.MarketIndexType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MarketIndexDto(
        MarketIndexType type,                // KOSPI, NASDAQ

        BigDecimal value,           // 5,507.01
        BigDecimal changeValue,     // 15.26
        BigDecimal changeRate,      // 0.28

        ChangeDirection direction   // UP / DOWN / FLAT
) {
    public static MarketIndexDto from(
            MarketIndexType type,
            BigDecimal value,
            BigDecimal changeValue,
            BigDecimal changeRate

    ){
        return MarketIndexDto.builder()
                .type(type)
                .value(value)
                .changeValue(changeValue)
                .changeRate(changeRate)
                .direction(ChangeDirectionResolver.resolve(changeValue))
                .build();
    }
}
