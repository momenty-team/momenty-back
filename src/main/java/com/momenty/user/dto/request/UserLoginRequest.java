package com.momenty.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(SnakeCaseStrategy.class)
public record UserLoginRequest(
        @NotBlank(message = "nickname이 필요합니다.")
        String nickname,

        @NotBlank(message = "access_token이 필요합니다.")
        String accessToken
) {

}
