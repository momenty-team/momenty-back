package com.momenty.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.user.domain.Gender;
import com.momenty.user.domain.User;
import java.time.LocalDate;

@JsonNaming(SnakeCaseStrategy.class)
public record UserUpdateRequest(
        String nickname,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate birthDate,

        String gender,

        Double height,

        Double weight,

        Boolean isPublic,

        String profileImageUrl
) {

    public void applyTo(User user) {
        user.updateProfile(
                (this.nickname != null && !this.nickname.isEmpty()) ? this.nickname : user.getNickname(),
                (this.birthDate != null) ? this.birthDate : user.getBirthDate(),
                (this.gender != null && !this.gender.isEmpty()) ? Gender.valueOf(this.gender.toUpperCase()) : user.getGender(),
                (this.height != null) ? this.height : user.getHeight(),
                (this.weight != null) ? this.weight : user.getWeight(),
                (this.isPublic != null) ? this.isPublic : user.isPublic(),
                (this.profileImageUrl != null) ? this.profileImageUrl : user.getProfileImageUrl()
        );
    }
}
