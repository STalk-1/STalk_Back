package com.stalk.api.auth.security;

import com.stalk.api.user.UserRole;
import com.stalk.api.user.UserStatus;

public record CustomPrincipal(
        Long userId,
        Long kakaoId,
        UserRole role,
        UserStatus status
) {
}
