package com.momenty.global.auth.oauth.apple.dto;

import com.momenty.global.auth.oauth.apple.domain.AppleUser;
import java.time.LocalDateTime;

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
