package com.momenty.global.auth.jwt.service;

import com.momenty.global.auth.jwt.JwtTokenProvider;
import com.momenty.global.auth.jwt.domain.AppleJwtStatus;
import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.auth.jwt.repository.JwtStatusRedisRepository;
import com.momenty.global.exception.InvalidJwtTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtStatusRedisRepository jwtStatusRedisRepository;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public AppleJwtStatus createAppleJwtStatus(String sub, String accessToken, String refreshToken) {
        return AppleJwtStatus.builder()
                .sub(sub)
                .accessToken(accessToken)
                .accessTokenExpiration(accessTokenExpiration)
                .refreshToken(refreshToken)
                .refreshTokenExpiration(refreshTokenExpiration)
                .build();
    }

    public JwtStatus createJwtStatus(Integer id, String accessToken, String refreshToken) {
        return JwtStatus.builder()
                .id(id)
                .accessToken(accessToken)
                .accessTokenExpiration(accessTokenExpiration)
                .refreshToken(refreshToken)
                .refreshTokenExpiration(refreshTokenExpiration)
                .build();
    }

    public JwtStatus issueAccessToken(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidJwtTokenException();
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        JwtStatus jwtStatus = jwtStatusRedisRepository.getById(Integer.parseInt(userId));
        String accessToken = jwtTokenProvider.generateAccessToken(userId);

        JwtStatus updatedStatus = JwtStatus.builder()
                .id(jwtStatus.getId())
                .accessToken(accessToken)
                .accessTokenExpiration(jwtStatus.getAccessTokenExpiration())
                .refreshToken(jwtStatus.getRefreshToken())
                .refreshTokenExpiration(jwtStatus.getRefreshTokenExpiration())
                .build();

        return jwtStatusRedisRepository.save(updatedStatus);
    }
}