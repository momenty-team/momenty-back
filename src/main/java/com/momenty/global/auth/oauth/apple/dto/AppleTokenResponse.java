package com.momenty.global.auth.oauth.apple.dto;


import static com.fasterxml.jackson.databind.PropertyNamingStrategies.*;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record AppleTokenResponse(
        String accessToken,
        String refreshToken,
        String idToken
) {

}