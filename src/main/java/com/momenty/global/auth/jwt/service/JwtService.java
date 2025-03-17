package com.momenty.global.auth.jwt.service;

import static com.momenty.global.auth.jwt.exception.TokenExceptionMessage.INVALID_TOKEN;

import com.momenty.global.auth.jwt.JwtTokenProvider;
import com.momenty.global.auth.jwt.domain.AppleJwtStatus;
import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.auth.jwt.repository.JwtStatusRedisRepository;
import com.momenty.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
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
            log.error("🚨 유효하지 않은 refresh_token: {}", refreshToken);
            throw new GlobalException(INVALID_TOKEN.getMessage(), INVALID_TOKEN.getStatus());
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        JwtStatus jwtStatus = jwtStatusRedisRepository.getById(Integer.parseInt(userId));

        if (jwtStatus == null) {
            log.error("🚨 Redis에서 JwtStatus를 찾을 수 없음: userId={}", userId);
            throw new GlobalException(INVALID_TOKEN.getMessage(), INVALID_TOKEN.getStatus());
        }

        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        log.info("✅ 새 access_token 발급: {}", accessToken);

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