package com.momenty.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.User;
import java.time.LocalDate;

@JsonNaming(SnakeCaseStrategy.class)
public record UserRegisterResponse(

        String nickname,

        LocalDate birthDate,

        String gender
) {
    public static UserRegisterResponse of(User user) {
        return new UserRegisterResponse(
                user.getNickname(), user.getBirthDate(),
                user.getGender().name());
    }
}
