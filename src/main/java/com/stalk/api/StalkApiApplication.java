package com.stalk.api;

import com.stalk.api.auth.config.KakaoOauthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationPropertiesScan
@SpringBootApplication
public class StalkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StalkApiApplication.class, args);
	}

}
