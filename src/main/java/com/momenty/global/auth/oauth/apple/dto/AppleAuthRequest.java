package com.momenty.global.auth.oauth.apple.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppleAuthRequest {
    private String state;
    private String authorizationCode;
    private String identityToken;
    private AppleUserRequest user;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppleUserRequest {
        private String email;
        private AppleName name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppleName {
        private String firstName;
        private String lastName;
    }
}

