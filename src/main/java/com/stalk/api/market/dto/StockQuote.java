package com.stalk.api.market.dto;

import java.math.BigDecimal;

public record StockQuote(
        BigDecimal price,
        BigDecimal changeValue,
        BigDecimal changeRate
) {
}