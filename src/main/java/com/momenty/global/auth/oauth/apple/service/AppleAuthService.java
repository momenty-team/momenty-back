package com.momenty.global.auth.oauth.apple.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.momenty.global.auth.jwt.JwtTokenProvider;
import com.momenty.global.auth.jwt.JwtUtil;
import com.momenty.global.auth.jwt.domain.AppleJwtStatus;
import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.auth.jwt.repository.AppleJwtStatusRedisRepository;
import com.momenty.global.auth.jwt.repository.JwtStatusRedisRepository;
import com.momenty.global.auth.jwt.service.JwtService;
import com.momenty.global.auth.oauth.apple.controller.AppleClient;
import com.momenty.global.auth.oauth.apple.domain.AppleAuthProperty;
import com.momenty.global.auth.oauth.apple.domain.AppleUser;
import com.momenty.global.auth.oauth.apple.dto.AppleAuthResponse;
import com.momenty.global.auth.oauth.apple.dto.AppleTokenResponse;
import com.momenty.global.auth.oauth.apple.repository.AppleUserRepository;
import com.momenty.global.auth.oauth.util.ClientSecret;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import com.momenty.user.service.UserService;
import io.jsonwebtoken.Claims;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppleAuthService {

    private final AppleClient appleClient;
    private final AppleAuthProperty appleAuthProperty;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    private final AppleJwtStatusRedisRepository appleJwtStatusRedisRepository;
    private final AppleUserRepository appleUserRepository;
    private final JwtStatusRedisRepository jwtStatusRedisRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppleAuthResponse processAppleAuth(String code, String idToken)
            throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {

        if (!validateIdToken(idToken)) {
            throw new IllegalArgumentException("Invalid ID Token");
        }

        AppleTokenResponse appleTokenResponse = requestAppleToken(code);
        String checkedIdToken = appleTokenResponse.idToken();

        String sub = getAppleAccountId(checkedIdToken);
        String email = getEmailFromIdToken(checkedIdToken);

        saveAppleToken(sub, appleTokenResponse);

        AppleUser appleUser = appleUserRepository.findBySub(sub).orElse(null);

        if (appleUser != null) {
            Integer userId = userRepository.getByEmail(appleUser.getEmail()).getId();
            JwtStatus jwtStatus = generateJwt(userId);
            return new AppleAuthResponse(appleUser, jwtStatus.getAccessToken(), jwtStatus.getRefreshToken());
        }

        if (canRegisterUser(email)) {
            AppleUser appleUserInfo = AppleUser.builder()
                    .sub(sub)
                    .email(email)
                    .refreshToken(appleTokenResponse.refreshToken())
                    .build();
            appleUserRepository.save(appleUserInfo);

            User userInfo = User.builder()
                    .email(email)
                    .build();
            userService.register(userInfo);
            JwtStatus jwtStatus = generateJwt(userInfo.getId());
            return new AppleAuthResponse(appleUserInfo, jwtStatus.getAccessToken(), jwtStatus.getRefreshToken());
        }

        throw new IllegalArgumentException("Email verification required");
    }

    private void saveAppleToken(String sub, AppleTokenResponse appleTokenResponse) {
        AppleJwtStatus appleJwtStatus = jwtService.createAppleJwtStatus(
                sub,
                appleTokenResponse.accessToken(),
                appleTokenResponse.refreshToken()
        );
        appleJwtStatusRedisRepository.save(appleJwtStatus);
    }

    @Transactional
    protected JwtStatus generateJwt(Integer userId) {
        String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(userId));
        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(userId));

        JwtStatus jwtStatus = jwtService.createJwtStatus(userId, accessToken, refreshToken);
        jwtStatusRedisRepository.save(jwtStatus);

        return jwtStatusRedisRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("JWT 저장 후 조회 실패")
        );
    }

    private boolean canRegisterUser(String email) {
        return email != null && !email.isEmpty();
    }

    private AppleTokenResponse requestAppleToken(String code) {
        return appleClient.getAppleToken(
                appleAuthProperty.getAud(),
                appleAuthProperty.getRedirectUri(),
                code,
                generateClientSecret(),
                "authorization_code"
        );
    }

    private String generateClientSecret() {
        return ClientSecret.createClientSecret(
                appleAuthProperty.getTeamId(),
                appleAuthProperty.getAud(),
                appleAuthProperty.getKey().getId(),
                appleAuthProperty.getKey().getPath(),
                appleAuthProperty.getIss()
        );
    }

    private String getAppleAccountId(String identityToken)
            throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
        return decodeIdToken(identityToken).getSubject();
    }

    private Claims decodeIdToken(String idToken)
            throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(
                jwtUtil.parseHeaders(idToken),
                appleClient.getAppleAuthPublicKey()
        );

        return jwtUtil.getTokenClaims(idToken, publicKey);
    }

    public String getEmailFromIdToken(String idToken)
            throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
        Claims claims = decodeIdToken(idToken);
        return claims.get("email", String.class);
    }


    private boolean validateIdToken(String idToken) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(
                jwtUtil.parseHeaders(idToken),
                appleClient.getAppleAuthPublicKey()
        );

        return jwtUtil.validateIdToken(
                jwtUtil.getTokenClaims(idToken, publicKey),
                appleAuthProperty.getIss(),
                appleAuthProperty.getAud()
        );
    }
}
