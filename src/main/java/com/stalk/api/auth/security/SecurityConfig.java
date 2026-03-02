package com.stalk.api.auth.security;

import com.stalk.api.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 카카오 로그인/콜백 허용
                        .requestMatchers("/api/auth/kakao/**").permitAll()

                        // Swagger 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // h2 콘솔 혀용
                        .requestMatchers("/h2-console/**").permitAll()

                        // 테스트 API
                        .requestMatchers(HttpMethod.GET, "/api/test/db/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/test/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/test/user").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/test/admin").hasRole("ADMIN")

                        .requestMatchers("/api/v1/stock/**").permitAll()

                        // ping/echo open
                        .requestMatchers("/api/test/ping", "/api/test/echo").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
