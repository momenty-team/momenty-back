package com.momenty.global.auth.jwt;

import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.auth.jwt.repository.JwtStatusRedisRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    private final JwtStatusRedisRepository jwtStatusRedisRepository;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
            JwtStatusRedisRepository jwtStatusRedisRepository
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration * 1000; // 초 단위 -> 밀리초 변환
        this.refreshTokenExpiration = refreshTokenExpiration * 1000;
        this.jwtStatusRedisRepository = jwtStatusRedisRepository;
    }

    public String generateAccessToken(String userId) {
        return generateToken(userId, accessTokenExpiration);
    }

    public String generateRefreshToken(String userId) {
        return generateToken(userId, refreshTokenExpiration);
    }

    private String generateToken(String userId, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            String userId = getUserIdFromToken(token);
            JwtStatus jwtStatus = jwtStatusRedisRepository.getById(Integer.parseInt(userId));

            return token.equals(
                    URLDecoder.decode(jwtStatus.getAccessToken(), StandardCharsets.UTF_8)
            ) || token.equals(
                    URLDecoder.decode(jwtStatus.getRefreshToken(), StandardCharsets.UTF_8)
            );
        } catch (ExpiredJwtException e) {
            log.error("❌ 만료된 JWT 토큰: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("❌ 잘못된 JWT 형식: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("❌ JWT 서명 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ JWT 검증 실패: {}", e.getMessage());
        }
        return false;
    }
}