package com.stalk.api.auth.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
        String adminKakaoIds // "123,456"
) {
    public Set<Long> adminIdSet() {
        if (adminKakaoIds == null || adminKakaoIds.isBlank()) return Set.of();
        return Arrays.stream(adminKakaoIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }
}
