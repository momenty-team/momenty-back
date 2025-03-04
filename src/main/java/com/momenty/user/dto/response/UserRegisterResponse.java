package com.momenty.user.dto.response;

import com.momenty.user.domain.User;
import java.time.LocalDate;

public record UserRegisterResponse(

        String name,

        String nickname,

        String password,

        String phoneNumber,

        LocalDate birthDate,

        String email,

        String profileImageUrl,

        String gender
) {
    public static UserRegisterResponse of(User user) {
        return new UserRegisterResponse(
                user.getName(), user.getNickname(),
                user.getPassword(), user.getPhoneNumber(),
                user.getBirthDate(), user.getEmail(),
                user.getProfileImageUrl(), user.getGender().name());
    }
}
