package com.stalk.api.kis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kis")
public record KisProperties(
        String baseUrl,
        String appkey,
        String appsecret,
        String custtype,
        Token token,
        Domestic domestic
) {
    public record Token(String path) {}
    public record Domestic(String inquirePricePath, String trIdInquirePrice) {}
}
