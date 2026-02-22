package com.stalk.api.auth.controller;

import com.stalk.api.auth.config.KakaoOauthProperties;
import com.stalk.api.auth.dto.KakaoTokenResponse;
import com.stalk.api.auth.dto.KakaoUserResponse;
import com.stalk.api.auth.service.KakaoOauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/kakao")
public class KakaoAuthController {

    private final KakaoOauthService kakaoOauthService;
    private final KakaoOauthProperties props;

    // 프론트가 이 URL 로 이동시키면 카카오 인가 페이지 url 반환
    @GetMapping("/login-url")
    public ResponseEntity<String> loginUrl() {
        log.info("[KAKAO] Login URL requested");
        String url = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", props.clientId())
                .queryParam("redirect_uri", props.redirectUri())
                .build()
                .toUriString();

        return ResponseEntity.ok(url);
    }

    // 카카오가 code를 붙여서 redirect_uri로 콜백
    @GetMapping("/callback")
    public ResponseEntity<KakaoUserResponse> callback(
            @RequestParam String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, name = "error_description") String errorDescription
    ) {
        log.info("[KAKAO] Callback received");
        if (error != null) {
            log.warn("[KAKAO] Callback error. error={}, description={}", error, errorDescription);
            throw new IllegalStateException("Kakao OAuth error=" + error + " desc=" + errorDescription);
        }
        // 토큰 요청
        KakaoTokenResponse token = kakaoOauthService.exchangeCodeForToken(code);

        // 사용자 정보 조회
        KakaoUserResponse user = kakaoOauthService.fetchUser(token.accessToken());

        log.info("[KAKAO] Login success. kakaoId={}", user.id());


        return ResponseEntity.ok(user);
    }
}
