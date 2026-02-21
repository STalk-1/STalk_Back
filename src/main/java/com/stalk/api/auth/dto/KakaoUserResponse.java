package com.stalk.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
        Long id,
        @JsonProperty("connected_at") String connectedAt,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
        Boolean email_needs_agreement,
        String email,
        Profile profile
    ) { }

    public record Profile(
            String nickname
    ) { }
}

/**
 * HTTP/1.1 200 OK
 * {
 *     "id":123456789,
 *     "connected_at": "2022-04-11T01:45:28Z",
 *     "kakao_account": {
 *         "profile_nickname_needs_agreement": false,
 *         "profile": {
 *             "nickname": "홍길동"
 *         }
 *     },
 *     "properties":{
 *         "${CUSTOM_PROPERTY_KEY}": "${CUSTOM_PROPERTY_VALUE}",
 *         ...
 *     }
 * }
 */