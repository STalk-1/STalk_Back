package com.stalk.api.user.controller;

import com.stalk.api.auth.security.CustomPrincipal;
import com.stalk.api.user.User;
import com.stalk.api.user.UserRepository;
import com.stalk.api.user.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal CustomPrincipal principal) {
        log.info("[USER] getMyInfo requested. userId={}", principal.userId());
        
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + principal.userId()));
                
        return ResponseEntity.ok(UserInfoResponse.from(user));
    }
}
