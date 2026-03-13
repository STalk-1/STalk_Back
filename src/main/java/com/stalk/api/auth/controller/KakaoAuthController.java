package com.stalk.api.auth.controller;

import com.stalk.api.auth.config.KakaoOauthProperties;
import com.stalk.api.auth.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Tag(name = "Kakao 인증/인가", description = "카카오 소셜 로그인 api")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/kakao")
public class KakaoAuthController {

    private final KakaoLoginService kakaoLoginService;
    private final KakaoOauthProperties props;

    // 프론트가 이 URL 로 이동시키면 카카오 인가 페이지 url 반환
    @Operation(summary = "카카오 인가 페이지 url 반환", description = "프론트가 이 URL 로 이동시키면 카카오 인가 페이지 url 반환")
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
    @Operation(summary = "callback", description = "카카오가 code를 붙여서 redirect_uri로 콜백")
    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, name = "error_description") String errorDescription
    ) {
        log.info("[KAKAO] Callback received");
        if (error != null) {
            log.warn("[KAKAO] Callback error. error={}, description={}", error, errorDescription);
            
            // 오류 시에도 프론트로 에러 정보와 함께 리다이렉트
            String errorRedirectUrl = UriComponentsBuilder
                    .fromUriString(props.frontendRedirectUri())
                    .queryParam("error", error)
                    .queryParam("error_description", errorDescription)
                    .build()
                    .toUriString();
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(errorRedirectUrl)).build();
        }

        var result = kakaoLoginService.loginByAuthorizationCode(code);
        
        // 쿠키 생성 (HttpOnly 적용)
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", result.accessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(900) // 15분
                .build();
                
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(1209600) // 14일
                .build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .location(URI.create(props.frontendRedirectUri()))
                .build();
    }
}
