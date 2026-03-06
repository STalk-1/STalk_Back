package com.stalk.api.kis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kis")
public record KisProperties(
        String baseUrl,
        String appkey,
        String appsecret,
        String custtype,
        Token token,
        Domestic domestic,
        Overseas overseas
) {
    public record Token(String path) {}

    public record Domestic(
            String inquirePricePath,
            String trIdInquirePrice,
            String inquireIndexPricePath,
            String trIdInquireIndexPrice
    ) {}

    public record Overseas(
            String inquireDailyChartPricePath,
            String trIdInquireDailyChartPrice
    ) { }
}
