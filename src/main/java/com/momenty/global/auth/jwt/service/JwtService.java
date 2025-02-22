package com.momenty.global.auth.jwt.service;

import com.momenty.global.auth.jwt.domain.JwtStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public JwtStatus createJwtStatus(String sub, String accessToken, String refreshToken) {
        return JwtStatus.builder()
                .sub(sub)
                .accessToken(accessToken)
                .accessTokenExpiration(accessTokenExpiration)
                .refreshToken(refreshToken)
                .refreshTokenExpiration(refreshTokenExpiration)
                .build();
    }
}