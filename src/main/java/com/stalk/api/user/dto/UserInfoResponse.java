package com.stalk.api.user.dto;

import com.stalk.api.user.User;

public record UserInfoResponse(
        Long userId,
        String nickname
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getNickname()
        );
    }
}
