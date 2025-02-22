package com.momenty.global.auth.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "jwtStatus", timeToLive = 1209600) // 14일 (1209600초)
public class JwtStatus {

    @Id
    private String sub;

    private String accessToken;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private String refreshToken;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
}
