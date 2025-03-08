package com.momenty.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.Gender;
import com.momenty.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@JsonNaming(SnakeCaseStrategy.class)
public record UserRegisterRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate birthDate,

        String gender
) {
    public void applyTo(User user) {
        user.updateProfileToRegister(
                this.nickname,
                this.birthDate,
                Gender.valueOf(this.gender.toUpperCase())
        );
    }

    public User toUser() {
        return User.builder()
                .nickname(this.nickname)
                .birthDate(this.birthDate)
                .gender(Gender.valueOf(this.gender.toUpperCase()))
                .build();
    }
}
