package com.stalk.api.auth.jwt;

import com.stalk.api.auth.security.CustomPrincipal;
import com.stalk.api.user.UserRole;
import com.stalk.api.user.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtTokenProvider(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(CustomPrincipal p) {
        return createToken(p, props.accessTokenSeconds(), "access");
    }

    public String createRefreshToken(CustomPrincipal p) {
        return createToken(p, props.refreshTokenSeconds(), "refresh");
    }
    private String createToken(CustomPrincipal p, long ttlSeconds, String typ) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);

        Map<String, Object> claims = Map.of(
                "uid", p.userId(),
                "kid", p.kakaoId(),
                "role", p.role().name(),
                "status", p.status().name(),
                "typ", typ
        );

        return Jwts.builder()
                .issuer(props.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(claims)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public CustomPrincipal parse(String token) {
        try {
            Claims c = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(props.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long uid = ((Number) c.get("uid")).longValue();
            Long kid = ((Number) c.get("kid")).longValue();
            UserRole role = UserRole.valueOf(String.valueOf(c.get("role")));
            UserStatus status = UserStatus.valueOf(String.valueOf(c.get("status")));

            return new CustomPrincipal(uid, kid, role, status);

        } catch (JwtException | IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid JWT: " + exception.getMessage(), exception);
        }
    }


}
