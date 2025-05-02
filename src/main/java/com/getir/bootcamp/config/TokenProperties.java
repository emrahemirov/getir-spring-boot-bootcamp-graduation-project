package com.getir.bootcamp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "token")
@Getter
@Setter
public class TokenProperties {

    private AccessTokenConfig accessToken;
    private RefreshTokenConfig refreshToken;
    private String secretKey;

    @Getter
    @Setter
    public static class AccessTokenConfig {
        private Long maxAge;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class RefreshTokenConfig {
        private Long maxAge;
        private String cookieName;
    }
}