package com.momenty.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record UserSearchResponse(
        @Schema(description = "사용자 검색 목록", required = false)
        List<UserSearchDto> users
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record UserSearchDto(
            @Schema(description = "사용자 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "사용자 닉네임", example = "huihui", required = true)
            String nickname,

            @Schema(description = "사용자 프로필 url", example = "urlurl", required = false)
            String profileImageUrl
    ) {}

    public static UserSearchResponse of(List<User> users) {
        List<UserSearchDto> dtoList = users.stream()
                .map(user -> new UserSearchDto(
                        user.getId(),
                        user.getNickname(),
                        user.getProfileImageUrl()
                ))
                .toList();

        return new UserSearchResponse(dtoList);
    }
}
