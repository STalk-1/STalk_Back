package com.stalk.api.kis.quote;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "quotes")
public record QuoteProperties(
        Poll poll,
        List<String> watchlist
) {
    public record Poll(boolean enabled, long fixedDelayMs) {}
}