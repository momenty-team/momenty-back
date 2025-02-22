package com.momenty.global.auth.oauth.apple.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.global.auth.oauth.apple.domain.AppleUser;

@JsonNaming(SnakeCaseStrategy.class)
public record AppleAuthResponse(
        AppleUser user,
        String accessToken,
        String refreshToken
) {

}