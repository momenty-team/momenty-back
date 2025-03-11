package com.momenty.user.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.Gender;
import com.momenty.user.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record UserInfoResponse(
        Integer id,
        String name,
        String nickname,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate birthDate,

        String email,
        String profileImageUrl,
        Gender gender,
        boolean isPublic,
        Integer followerCount,
        Integer followingCount,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime updatedAt,

        boolean isDeleted
) {
    public static UserInfoResponse of(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getBirthDate(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getGender(),
                user.isPublic(),
                user.getFollowerCount(),
                user.getFollowingCount(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isDeleted()
        );
    }
}