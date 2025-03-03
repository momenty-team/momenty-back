package com.momenty.user.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.user.domain.User;
import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.dto.response.UserRegisterResponse;
import com.momenty.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 일반 회원가입
/*    @PostMapping("/register/general")
    public ResponseEntity<Void> GeneralRegister (
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    ) {
        userService.generalRegister(userRegisterRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate (
            @Valid @RequestBody UserAuthenticationRequest authenticationRequest
    ) {
        userService.authenticate(authenticationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*/


    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register (
            @Valid @RequestBody UserRegisterRequest userRegisterRequest,
            @UserId Integer userId
    ) {
        User user = userService.register(userRegisterRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserRegisterResponse.of(user));
    }
}
