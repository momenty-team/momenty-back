package com.momenty.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationTokenRequest(
        @NotBlank(message = "토큰은 필수입니다.")
        String token
) {

}
