package com.stalk.api.market.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FavoriteStocksResponse(
        List<StockCardDto> items
) {}