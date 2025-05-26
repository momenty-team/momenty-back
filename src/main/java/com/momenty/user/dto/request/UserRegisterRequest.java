package com.momenty.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.Gender;
import com.momenty.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonNaming(SnakeCaseStrategy.class)
public record UserRegisterRequest(
        String firstName,
        String lastName,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate birthDate,

        String gender,
        Double height,
        Double weight
) {
    public void applyTo(User user) {
        user.updateProfileToRegister(
                extractNameFromFullName(firstName, lastName),
                this.nickname,
                this.birthDate,
                Gender.valueOf(this.gender.toUpperCase()),
                this.height,
                this.weight
        );
    }

    public User toUser() {
        return User.builder()
                .nickname(this.nickname)
                .birthDate(this.birthDate)
                .gender(Gender.valueOf(this.gender.toUpperCase()))
                .height(this.height)
                .weight(this.weight)
                .build();
    }

    private String extractNameFromFullName(String firstName, String lastName) {
        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";

        return (firstName + lastName).replaceAll("\\s+", "");
    }
}
