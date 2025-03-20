package com.momenty.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@JsonNaming(SnakeCaseStrategy.class)
public record UserUpdateResponse(

        @Schema(description = "사용자 닉네임", example = "huihui", required = true)
        String nickname,

        @Schema(description = "사용자 생일", example = "2003-11-03", required = false)
        LocalDate birthDate,

        @Schema(description = "사용자 성별", example = "male", required = true)
        String gender,

        @Schema(description = "사용자 기록 공개 여부", example = "true", required = true)
        boolean isPublic,

        @Schema(description = "사용자 프로필 url", example = "urlurl", required = false)
        String profileImageUrl
) {

    public static UserUpdateResponse of(User user) {
        return new UserUpdateResponse(
                user.getNickname(),
                user.getBirthDate(),
                user.getGender().name(),
                user.isPublic(),
                user.getProfileImageUrl()
        );
    }
}
