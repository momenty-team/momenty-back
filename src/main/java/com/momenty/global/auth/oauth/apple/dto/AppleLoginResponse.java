package com.momenty.global.auth.oauth.apple.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.global.auth.oauth.apple.domain.AppleUser;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record AppleLoginResponse(
        Integer id,
        String email,
        String sub,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static AppleLoginResponse of(AppleUser user) {
        return new AppleLoginResponse(
                user.getId(),
                user.getEmail(),
                user.getSub(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
