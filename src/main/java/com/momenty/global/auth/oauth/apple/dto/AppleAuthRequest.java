package com.momenty.global.auth.oauth.apple.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppleAuthRequest {
    private Authorization authorization;
    private AuthorizedData authorizedData;
    private boolean consentRequired;
    private boolean enableSignInWithAppleNewFirstTimeRunScreen;
    private int consentVersion;
    private boolean reAuthorization;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Authorization {
        @JsonProperty("id_token")
        private String idToken;

        @JsonProperty("grant_code")
        private String grantCode;

        private List<String> scope;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorizedData {
        private String userId;
    }
}

