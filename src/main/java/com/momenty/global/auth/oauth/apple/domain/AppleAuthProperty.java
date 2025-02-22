package com.momenty.global.auth.oauth.apple.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "apple.auth")
@Getter
@Setter
public class AppleAuthProperty {
    private String tokenUrl;
    private String publicKeyUrl;
    private String redirectUri;
    private String iss;
    private String aud;
    private String teamId;
    private AppleKey key;

    @Getter
    @Setter
    public static class AppleKey {
        private String id;
        private String path;
    }
}