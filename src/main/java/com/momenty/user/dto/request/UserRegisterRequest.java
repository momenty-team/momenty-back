package com.momenty.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

        @NotBlank(message = "전화번호는 필수입니다.")
        String phoneNumber,

        @NotNull(message = "생일은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate birthDate,

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        String profileImageUrl
) {
    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
            .name(name)
            .password(passwordEncoder.encode(password))
            .nickname(nickname)
            .email(email)
            .phoneNumber(phoneNumber)
            .birthDate(birthDate)
            .profileImageUrl(profileImageUrl)
            .build();
    }
}
