package com.stalk.api.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.time.Instant;

@Slf4j
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
//            log.info("KIS accessToken cache hit. expiresAt={}", expiresAt);
            return accessToken;
        }
        synchronized (this) {
            now = Instant.now();
            if (accessToken != null && expiresAt != null && now.isBefore(expiresAt.minusSeconds(60))) {
//                log.info("KIS accessToken cache hit (after lock). expiresAt={}", expiresAt);
                return accessToken;
            }
            TokenResponse res = issueToken();
            this.accessToken = res.accessToken();
            this.expiresAt = Instant.now().plusSeconds(res.expiresIn());

//            log.info("KIS accessToken issued successfully. expiresAt={}", expiresAt);

            return this.accessToken;
        }
    }

    private TokenResponse issueToken() {

        log.info("KIS token request start");

        TokenRequest req = new TokenRequest("client_credentials", props.appkey(), props.appsecret());

        try {
            TokenResponse response = kisRestClient.post()
                    .uri(props.token().path())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(req)
                    .retrieve()
                    .body(TokenResponse.class);

            log.info("KIS token API response received");

            return response;
        } catch (Exception e) {
            log.error("KIS token issuance failed", e);
            throw e;
        }
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
