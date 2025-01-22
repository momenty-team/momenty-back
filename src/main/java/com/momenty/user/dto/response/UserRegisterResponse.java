package com.momenty.user.dto.response;

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
}
