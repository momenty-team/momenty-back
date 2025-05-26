package com.momenty.user.domain;

import com.momenty.user.dto.request.UserRegisterRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash(value = "sport", timeToLive = 300) // 300초
public class UserTemporaryStatus {

    @Id
    String email;

    String name;

    String nickname;

    String password;

    String phoneNumber;

    LocalDate birthDate;

    String profileImageUrl;

    String authenticationNumber;

    public static UserTemporaryStatus of(
            UserRegisterRequest userRegisterRequest,
            String authenticationNumber
    ) {
        return new UserTemporaryStatus(userRegisterRequest.email(), userRegisterRequest.name(),
                userRegisterRequest.nickname(), userRegisterRequest.password(), userRegisterRequest.phoneNumber(),
                userRegisterRequest.birthDate(), userRegisterRequest.profileImageUrl(), authenticationNumber);
    }
}
