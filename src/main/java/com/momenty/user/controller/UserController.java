package com.momenty.user.controller;

import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/register")
    public ResponseEntity<Void> register (
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    ) {
        userService.register(userRegisterRequest);
        return ResponseEntity.ok().build();
    }
}
