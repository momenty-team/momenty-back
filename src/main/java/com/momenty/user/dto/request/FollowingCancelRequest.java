package com.momenty.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(SnakeCaseStrategy.class)
public record FollowingCancelRequest(
        @NotNull(message = "followingCancelUserId는 필수입니다.")
        Integer followingCancelUserId
) {

}
