package com.momenty.user.dto.response;

import com.momenty.user.domain.User;
import com.momenty.user.domain.UserTemporaryStatus;
import java.time.LocalDate;

public record UserRegisterResponse(

        String name,

        String nickname,

        String password,

        String phoneNumber,

        LocalDate birthDate,

        String email,

        String profileImageUrl
) {

    public static UserRegisterResponse of(UserTemporaryStatus userTemporaryStatus) {
        return new UserRegisterResponse(userTemporaryStatus.getName(), userTemporaryStatus.getPassword(),
                userTemporaryStatus.getNickname(), userTemporaryStatus.getEmail(), userTemporaryStatus.getBirthDate(),
                userTemporaryStatus.getPhoneNumber(), userTemporaryStatus.getProfileImageUrl());
    }

    public static UserRegisterResponse of(User user) {
        return new UserRegisterResponse(user.getName(), user.getPassword(),
                user.getNickname(), user.getEmail(), user.getBirthDate(),
                user.getPhoneNumber(), user.getProfileImageUrl());
    }
}
