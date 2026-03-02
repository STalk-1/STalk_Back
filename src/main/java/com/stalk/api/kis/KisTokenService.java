package com.stalk.api.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class KisTokenService {

    private final RestClient kisRestClient;
    private final KisProperties props;

    private volatile String accessToken;
    private volatile Instant expiresAt;

    public String getValidAccessToken() {
        Instant now = Instant.now();
        if (accessToken != null && expiresAt != null && now.isBefore(expiresAt.minusSeconds(60))) {
            return accessToken;
        }
        synchronized (this) {
            now = Instant.now();
            if (accessToken != null && expiresAt != null && now.isBefore(expiresAt.minusSeconds(60))) {
                return accessToken;
            }
            TokenResponse res = issueToken();
            this.accessToken = res.accessToken();
            this.expiresAt = Instant.now().plusSeconds(res.expiresIn());
            return this.accessToken;
        }
    }

    private TokenResponse issueToken() {
        TokenRequest req = new TokenRequest("client_credentials", props.appkey(), props.appsecret());

        return kisRestClient.post()
                .uri(props.token().path())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(TokenResponse.class);
    }

    public record TokenRequest(
            @JsonProperty("grant_type") String grantType,
            @JsonProperty("appkey") String appkey,
            @JsonProperty("appsecret") String appsecret
    ) { }

    public record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("expires_in") long expiresIn,
            @JsonProperty("access_token_token_expired") String accessTokenTokenExpired
    ) { }
}
