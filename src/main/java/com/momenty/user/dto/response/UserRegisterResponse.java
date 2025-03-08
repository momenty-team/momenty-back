package com.momenty.user.dto.response;

import com.momenty.user.domain.User;
import java.time.LocalDate;

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
