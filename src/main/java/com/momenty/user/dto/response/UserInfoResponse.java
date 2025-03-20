package com.momenty.user.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.Gender;
import com.momenty.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record UserInfoResponse(
        @Schema(description = "사용자 ID", example = "1", required = true)
        Integer id,

        @Schema(description = "사용자 이름", example = "박다희", required = true)
        String name,

        @Schema(description = "사용자 닉네임", example = "huihui", required = true)
        String nickname,

        @Schema(description = "사용자 생일", example = "2003-11-03", required = false)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate birthDate,

        @Schema(description = "사용자 이메일", example = "qkrekgml7414@gmail.com", required = true)
        String email,

        @Schema(description = "사용자 프로필 url", example = "urlurl", required = false)
        String profileImageUrl,

        @Schema(description = "사용자 성별", example = "male", required = true)
        Gender gender,

        @Schema(description = "사용자 기록 공개 여부", example = "true", required = true)
        boolean isPublic,

        @Schema(description = "사용자 팔로워 수", example = "10", required = true)
        Integer followerCount,

        @Schema(description = "사용자 팔로잉 수", example = "11", required = true)
        Integer followingCount,

        @Schema(description = "사용자 생성 날짜", example = "2024-03-20 12:00:00", required = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,

        @Schema(description = "사용자 정보 수정 날짜", example = "2024-03-20 12:00:00", required = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime updatedAt,

        @Schema(description = "사용자 탈퇴 여부", example = "false", required = true)
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