package com.stalk.api.auth.service;

import com.stalk.api.auth.config.KakaoOauthProperties;
import com.stalk.api.auth.dto.KakaoTokenResponse;
import com.stalk.api.auth.dto.KakaoUserResponse;
import com.stalk.api.auth.exception.KakaoApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoOauthService {

    private final KakaoOauthProperties props;
    private final WebClient webClient;

    public KakaoOauthService(KakaoOauthProperties props, WebClient.Builder builder) {
        this.props = props;
        this.webClient = builder.build();
    }

    // 토큰 요청
    /**
     * 요청
     * curl -v -X POST "https://kauth.kakao.com/oauth/token" \
     *     -H "Content-Type: application/x-www-form-urlencoded;charset=utf-8" \
     *     -d "grant_type=authorization_code" \
     *     -d "client_id=${REST_API_KEY}" \
     *     --data-urlencode "redirect_uri=${REDIRECT_URI}" \
     *     -d "code=${AUTHORIZE_CODE}" \
     *     -d "client_secret=${CLIENT_SECRET}"
     */

    /**
     * 응답
     * HTTP/1.1 200
     * Content-Type: application/json;charset=UTF-8
     * {
     *     "token_type":"bearer",
     *     "access_token":"${ACCESS_TOKEN}",
     *     "expires_in":43199,
     *     "refresh_token":"${REFRESH_TOKEN}",
     *     "refresh_token_expires_in":5184000,
     *     "scope":"account_email profile"
     * }
     */
    public KakaoTokenResponse exchangeCodeForToken(String code) {
        // Token요청을 위한 Form Data 구성
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", props.clientId());
        form.add("redirect_uri", props.redirectUri());
        form.add("code", code);

        return webClient.post()
                .uri(props.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(body -> new KakaoApiException("Kakao token API failed: " + body)))
                .bodyToMono(KakaoTokenResponse.class)
                .block();
    }

    // 사용자 정보 조회

    /**
     * 요청
     * curl -v -G GET "https://kapi.kakao.com/v2/user/me" \
     *   -H "Authorization: Bearer ${ACCESS_TOKEN}"
     */

    /**
     * 응답
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
    public KakaoUserResponse fetchUser(String accessToken) {
        return webClient.post()
                .uri(props.userUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(body -> new KakaoApiException("Kakao user API failed: " + body)))
                .bodyToMono(KakaoUserResponse.class)
                .block();
    }

}
