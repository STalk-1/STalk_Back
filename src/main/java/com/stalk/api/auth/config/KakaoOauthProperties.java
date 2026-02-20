package com.stalk.api.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.oauth")
public record KakaoOauthProperties(
        String clientId,
        String redirectUri,
        String tokenUri,
        String uerUri
) {
}
