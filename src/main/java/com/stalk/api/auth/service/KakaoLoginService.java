package com.stalk.api.auth.service;

import com.stalk.api.auth.AuthProvider;
import com.stalk.api.auth.config.AuthProperties;
import com.stalk.api.auth.dto.KakaoTokenResponse;
import com.stalk.api.auth.dto.KakaoUserResponse;
import com.stalk.api.auth.jwt.JwtTokenProvider;
import com.stalk.api.auth.security.CustomPrincipal;
import com.stalk.api.user.User;
import com.stalk.api.user.UserRepository;
import com.stalk.api.user.UserRole;
import com.stalk.api.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoOauthService kakaoOauthService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthProperties authProperties;

    @Transactional
    public LoginResult loginByAuthorizationCode(String code) {
        KakaoTokenResponse token = kakaoOauthService.exchangeCodeForToken(code);
        KakaoUserResponse kakaoUser = kakaoOauthService.fetchUser(token.accessToken());

        Long kakaoId = kakaoUser.id();
        String nickname = kakaoUser.kakaoAccount().profile().nickname();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    UserRole role = authProperties.adminIdSet().contains(kakaoId) ? UserRole.ADMIN : UserRole.USER;
                    return userRepository.save(new User(kakaoId, nickname, AuthProvider.KAKAO, role, UserStatus.ACTIVE));
                });

        // 기존 유저 닉네임 변경 반영 (카카오에서 변경됐을 경우)
        if (!nickname.equals(user.getNickname())) {
            user.updateNickname(nickname);
        }

        // 기존 유저라도 admin 목록에 있으면 role 승격
        if (authProperties.adminIdSet().contains(kakaoId) && user.getRole() != UserRole.ADMIN) {
            user.changeRole(UserRole.ADMIN);
        }

        CustomPrincipal principal = new CustomPrincipal(user.getId(), user.getKakaoId(), user.getRole(), user.getStatus());
        String access = jwtTokenProvider.createAccessToken(principal);
        String refresh = jwtTokenProvider.createRefreshToken(principal);

        log.info("[AUTH] Login issued tokens. userId={}, kakaoId={}, role={}, status={}",
                user.getId(), user.getKakaoId(), user.getRole(), user.getStatus());

        return new LoginResult(
                user.getId(),
                user.getKakaoId(),
                user.getRole().name(),
                user.getStatus().name(),
                access, refresh);
    }

    public record LoginResult(
            Long userId,
            Long kakaoId,
            String role,
            String status,
            String accessToken,
            String refreshToken
    ) {}
}