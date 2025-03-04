package com.momenty.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.Gender;
import com.momenty.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.springframework.security.crypto.password.PasswordEncoder;

@JsonNaming(SnakeCaseStrategy.class)
public record UserRegisterRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        String phoneNumber,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate birthDate,

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        String profileImageUrl,

        String gender
) {
    public void applyTo(User user, PasswordEncoder encoder) {
        user.updateProfile(
                this.name,
                this.nickname,
                encoder.encode(this.password),
                this.phoneNumber,
                this.birthDate,
                this.email,
                this.profileImageUrl,
                Gender.valueOf(this.gender.toUpperCase())
        );
    }

    public User toUser(PasswordEncoder encoder) {
        return User.builder()
                .name(this.name)
                .nickname(this.nickname)
                .password(encoder.encode(this.password))
                .phoneNumber(this.phoneNumber)
                .birthDate(this.birthDate)
                .email(this.email)
                .profileImageUrl(this.profileImageUrl)
                .gender(Gender.valueOf(this.gender.toUpperCase()))
                .build();
    }
}
