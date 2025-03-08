package com.momenty.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.User;
import java.time.LocalDate;

@JsonNaming(SnakeCaseStrategy.class)
public record UserUpdateResponse(

        String nickname,

        LocalDate birthDate,

        String gender,

        boolean isPublic,

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
