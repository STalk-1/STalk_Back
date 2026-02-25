package com.stalk.api.market.dto;

import com.stalk.api.market.Domain.ChangeDirection;
import com.stalk.api.market.Domain.MarketType;
import com.stalk.api.market.Domain.Stock;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record StockCardDto(
        String name,                 // 삼성전자
        String code,                 // 005930
        MarketType market,           // KOSPI

        BigDecimal price,            // 현재가
        BigDecimal changeValue,      // 전일 대비 (+3800 / -3800)
        BigDecimal changeRate,       // 등락률 (2.13 / -2.13)

        ChangeDirection direction,   // UP/DOWN/FLAT

        List<BigDecimal> sparkline   // 최근 30거래일 종가
) {
    public static StockCardDto from(
            Stock entity,
            StockQuote quote,
            List<BigDecimal> sparkline
    ) {
        return StockCardDto.builder()
                .name(entity.getName())
                .code(entity.getCode())
                .market(entity.getMarket())
                .price(quote.price())
                .changeValue(quote.changeValue())
                .changeRate(quote.changeRate())
                .direction(ChangeDirectionResolver.resolve(quote.changeValue()))
                .sparkline(sparkline)
                .build();
    }
}
