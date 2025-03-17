package com.momenty.global.auth.jwt;

import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.auth.jwt.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    @PostMapping("/access-token")
    public ResponseEntity<Void> issueAccessToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (request.getCookies() != null) {
            String cookies = Arrays.stream(request.getCookies())
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                    .collect(Collectors.joining("; "));
            log.info("Request cookies: {}", cookies);
        } else {
            log.info("Request has no cookies");
        }

        String refreshToken = extractTokenFromCookie(request, "refresh_token");
        JwtStatus savedJwtStatus = jwtService.issueAccessToken(refreshToken);

        Cookie accessTokenCookie = new Cookie("access_token", savedJwtStatus.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");

        Cookie refreshTokenCookie = new Cookie("refresh_token", savedJwtStatus.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().build();
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
