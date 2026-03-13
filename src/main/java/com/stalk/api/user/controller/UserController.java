package com.stalk.api.user.controller;

import com.stalk.api.auth.security.CustomPrincipal;
import com.stalk.api.user.User;
import com.stalk.api.user.UserRepository;
import com.stalk.api.user.dto.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 정보 조회", description = "유저 id, nickname 정보를 조회합니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "유저 정보 조회", description = "유저 id, nickname 정보를 조회")
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal CustomPrincipal principal) {
        log.info("[USER] getMyInfo requested. userId={}", principal.userId());
        
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + principal.userId()));
                
        return ResponseEntity.ok(UserInfoResponse.from(user));
    }
}
