package com.momenty.global.auth.oauth.apple.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.momenty.global.auth.jwt.JwtTokenProvider;
import com.momenty.global.auth.jwt.JwtUtil;
import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.auth.jwt.repository.JwtStatusRedisRepository;
import com.momenty.global.auth.jwt.service.JwtService;
import com.momenty.global.auth.oauth.apple.controller.AppleClient;
import com.momenty.global.auth.oauth.apple.domain.AppleAuthProperty;
import com.momenty.global.auth.oauth.apple.domain.AppleUser;
import com.momenty.global.auth.oauth.apple.dto.AppleAuthRequest;
import com.momenty.global.auth.oauth.apple.dto.AppleAuthResponse;
import com.momenty.global.auth.oauth.apple.dto.AppleTokenResponse;
import com.momenty.global.auth.oauth.apple.repository.AppleUserRepository;
import com.momenty.global.auth.oauth.util.ClientSecret;
import com.momenty.user.domain.User;
import com.momenty.user.service.UserService;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    private final JwtStatusRedisRepository jwtStatusRedisRepository;
    private final AppleUserRepository appleUserRepository;

    @Transactional
    public AppleAuthResponse processAppleAuth(AppleAuthRequest appleAuthRequest)
            throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {

        String idToken = appleAuthRequest.getIdentityToken();
        String code = appleAuthRequest.getAuthorizationCode();

        if (!validateIdToken(idToken)) {
            throw new IllegalArgumentException("Invalid ID Token");
        }

        AppleTokenResponse appleTokenResponse = requestAppleToken(code);
        String checkedIdToken = appleTokenResponse.idToken();

        String sub = getAppleAccountId(checkedIdToken);
        String email = getEmail(appleAuthRequest);

        AppleUser user = appleUserRepository.findBySub(sub).orElse(null);
        AppleUser appleUserInfo = AppleUser.builder()
                .sub(sub)
                .email(email)
                .refreshToken(appleTokenResponse.refreshToken())
                .build();

        if (user != null) {
            return generateJwtAndReturnUser(appleUserInfo);
        }

        if (canRegisterUser(email)) {
            appleUserRepository.save(appleUserInfo);
            User userInfo = User.builder()
                    .email(email)
                    .name(getFullName(appleAuthRequest))
                    .build();
            userService.register(userInfo);
            return generateJwtAndReturnUser(appleUserInfo);
        }

        throw new IllegalArgumentException("Email verification required");
    }

    private AppleAuthResponse generateJwtAndReturnUser(AppleUser user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getSub());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getSub());

        JwtStatus jwtStatus = jwtService.createJwtStatus(user.getSub(), accessToken, refreshToken);
        jwtStatusRedisRepository.save(jwtStatus);

        return new AppleAuthResponse(user, accessToken, refreshToken);
    }

    private boolean canRegisterUser(String email) {
        return email != null && !email.isEmpty();
    }

    private AppleTokenResponse requestAppleToken(String code) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("client_id", appleAuthProperty.getAud());  // Apple Client ID
        request.add("client_secret", generateClientSecret());
        request.add("code", code);
        request.add("grant_type", "authorization_code");
        request.add("redirect_uri", appleAuthProperty.getRedirectUri());

        return appleClient.getAppleToken(request);
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
            throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(
                jwtUtil.parseHeaders(identityToken),
                appleClient.getAppleAuthPublicKey()
        );

        return jwtUtil.getTokenClaims(identityToken, publicKey).getSubject();
    }

    private String getFullName(AppleAuthRequest appleAuthRequest) {
        AppleAuthRequest.AppleUserRequest user = appleAuthRequest.getUser();
        if (user != null && user.getName() != null) {
            AppleAuthRequest.AppleName name = user.getName();
            String firstName = name.getFirstName();
            String lastName = name.getLastName();

            firstName = (firstName != null) ? firstName : "";
            lastName = (lastName != null) ? lastName : "";

            return firstName + lastName;
        }
        return "";
    }

    public String getEmail(AppleAuthRequest appleAuthRequest) {
        if (appleAuthRequest != null && appleAuthRequest.getUser() != null) {
            return appleAuthRequest.getUser().getEmail();
        }
        return "";
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
