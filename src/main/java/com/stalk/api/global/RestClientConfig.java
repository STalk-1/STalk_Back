package com.stalk.api.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    log.debug("[REST] >>> {} {}", request.getMethod(), request.getURI());

                    var response = execution.execute(request, body);

                    log.debug("[REST] <<< status={} {}", response.getStatusCode(), request.getURI());
                    return response;
                });
    }
}