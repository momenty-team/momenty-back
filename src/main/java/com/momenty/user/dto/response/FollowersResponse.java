package com.momenty.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.Follower;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record FollowersResponse(
        @Schema(description = "팔로워 목록", required = false)
        List<FollowersDto> followers
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record FollowersDto(
            @Schema(description = "팔로워 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "팔로워한 사용자의 ID", example = "1", required = true)
            Integer followerUserId,

            @Schema(description = "팔로워한 사용자의 nickname", example = "길동이", required = true)
            String followerUserNickname,

            @Schema(description = "팔로워한 사용자의 프로필 url", example = "urlurl", required = false)
            String followerUserProfileImageUrl
    ) {}

    public static FollowersResponse of(List<Follower> followers) {
        List<FollowersDto> dtoList = followers.stream()
                .map(follower -> new FollowersDto(
                        follower.getId(),
                        follower.getFollowerUser().getId(),
                        follower.getFollowerUser().getNickname(),
                        follower.getFollowerUser().getProfileImageUrl()
                ))
                .toList();

        return new FollowersResponse(dtoList);
    }
}

