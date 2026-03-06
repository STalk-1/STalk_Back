package com.stalk.api.kis.stock;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "stocks")
public record StockProperties(
        Poll poll,
        List<String> watchlist
) {
    public record Poll(
            long fixedDelayMs
    ) {}
}