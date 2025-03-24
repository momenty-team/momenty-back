package com.momenty.global.auth.oauth.apple.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.momenty.global.auth.oauth.apple.dto.AppleAuthResponse;
import com.momenty.global.auth.oauth.apple.dto.AppleLoginResponse;
import com.momenty.global.auth.oauth.apple.service.AppleAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth/apple")
@RequiredArgsConstructor
public class AppleAuthController {

    private final AppleAuthService appleAuthService;

    @PostMapping(
            value = "/callback",
            consumes = {"application/x-www-form-urlencoded", "application/x-www-form-urlencoded;charset=UTF-8"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AppleLoginResponse> handleAppleCallback(
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "id_token", required = false) String idToken,
            HttpServletResponse response
    ) throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {

        log.info("Apple Callback 호출됨: state={}, code={}, id_token={}", state, code, idToken);

        AppleAuthResponse authResponse = appleAuthService.processAppleAuth(code, idToken);

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", authResponse.accessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .build();

        log.info("반환한 aceess token: access_token={}", authResponse.accessToken());

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResponse.accessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok(AppleLoginResponse.of(authResponse.user()));
    }
}
