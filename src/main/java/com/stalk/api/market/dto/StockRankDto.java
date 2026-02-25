package com.stalk.api.market.dto;

import com.stalk.api.market.Domain.ChangeDirection;
import com.stalk.api.market.Domain.MarketType;
import com.stalk.api.market.Domain.Stock;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record StockRankDto(
        Integer rank,           // 1
        String name,            // 삼성전자
        String code,            // 005930
        MarketType market,     // KOSPI

        BigDecimal price,          // 현재가
        BigDecimal changeValue,    // 전일 대비
        BigDecimal changeRate,       // 등락률 (%)

        ChangeDirection direction   // UP / DOWN / FLAT
) {
    public static StockRankDto from(
            Integer rank,
            Stock entity,
            StockQuote quote
    ){
        return StockRankDto.builder()
                .rank(rank)
                .name(entity.getName())
                .code(entity.getCode())
                .market(entity.getMarket())
                .price(quote.price())
                .changeValue(quote.changeValue())
                .changeRate(quote.changeRate())
                .direction(ChangeDirectionResolver.resolve(quote.changeValue()))
                .build();
    }
}