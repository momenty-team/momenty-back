package com.momenty.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.Following;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record FollowingsResponse(
        @Schema(description = "팔로잉 목록", required = false)
        List<FollowingsDto> followings
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record FollowingsDto(
            @Schema(description = "팔로잉 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "팔로잉한 사용자의 ID", example = "1", required = true)
            Integer followingUserId,

            @Schema(description = "팔로잉한 사용자의 nickname", example = "길동이", required = true)
            String followingUserNickname,

            @Schema(description = "팔로잉한 사용자의 프로필 url", example = "urlurl", required = false)
            String followingUserProfileImageUrl
    ) {}

    public static FollowingsResponse of(List<Following> followings) {
        List<FollowingsDto> dtoList = followings.stream()
                .map(following -> new FollowingsDto(
                        following.getId(),
                        following.getFollowingUser().getId(),
                        following.getFollowingUser().getNickname(),
                        following.getFollowingUser().getProfileImageUrl()
                ))
                .toList();

        return new FollowingsResponse(dtoList);
    }
}
