package com.momenty.user.controller;

import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.dto.response.UserRegisterResponse;
import com.momenty.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register (
            @Valid UserRegisterRequest userRegisterRequest
    ) {
        UserRegisterResponse userRegisterResponse = userService.register(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRegisterResponse);
    }
}
