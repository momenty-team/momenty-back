package com.momenty.user.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.user.domain.User;
import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.dto.request.UserUpdateRequest;
import com.momenty.user.dto.response.UserRegisterResponse;
import com.momenty.user.dto.response.UserUpdateResponse;
import com.momenty.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register/general")
    public ResponseEntity<Void> GeneralRegister (
            @Valid @RequestBody UserRegisterRequest userRegisterRequest,
            HttpServletResponse response
    ) {
        JwtStatus jwtStatus = userService.generalRegister(userRegisterRequest);
        Cookie accessTokenCookie = new Cookie("access_token", jwtStatus.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간

        Cookie refreshTokenCookie = new Cookie("refresh_token", jwtStatus.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 14); // 14일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register (
            @Valid @RequestBody UserRegisterRequest userRegisterRequest,
            @UserId Integer userId
    ) {
        User user = userService.register(userRegisterRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserRegisterResponse.of(user));
    }

    @PutMapping("/details")
    public ResponseEntity<UserUpdateResponse> update (
            @Valid @RequestBody UserUpdateRequest userUpdateRequest,
            @UserId Integer userId
    ) {
        User user = userService.update(userUpdateRequest, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserUpdateResponse.of(user));
    }
}
