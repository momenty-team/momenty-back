package com.momenty.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@JsonNaming(SnakeCaseStrategy.class)
public record UserRegisterResponse(

        @Schema(description = "사용자 이름", example = "다희", required = true)
        String name,

        @Schema(description = "사용자 닉네임", example = "huihui", required = true)
        String nickname,

        @Schema(description = "사용자 생일", example = "2003-11-03", required = false)
        LocalDate birthDate,

        @Schema(description = "사용자 성별", example = "male", required = true)
        String gender
) {
    public static UserRegisterResponse of(User user) {
        return new UserRegisterResponse(
                user.getName(),
                user.getNickname(),
                user.getBirthDate(),
                user.getGender().name());
    }
}
