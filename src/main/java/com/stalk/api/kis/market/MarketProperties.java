package com.stalk.api.kis.market;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "market")
public record MarketProperties(
        Domestic domestic,
        Overseas overseas
) {
    public record Domestic(
            String kospiCode
    ) { }

    public record Overseas(
            String nasdaqCode,
            String sp500Code,
            String usdKrwCode
    ) { }
}
