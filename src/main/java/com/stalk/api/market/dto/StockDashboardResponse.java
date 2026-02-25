package com.stalk.api.market.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record StockDashboardResponse(
        List<MarketIndexDto> marketIndices,
        List<StockRankDto> topVolumeStocks
) {
}
