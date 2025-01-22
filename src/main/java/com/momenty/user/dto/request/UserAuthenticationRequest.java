package com.momenty.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(SnakeCaseStrategy.class)
public record UserAuthenticationRequest(

        @NotBlank(message = "인증번호가 필요합니다.")
        String authenticationNumber,

        @NotBlank(message = "이메일이 필요합니다.")
        String email
) {

}
