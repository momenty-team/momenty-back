package com.momenty.user.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.user.domain.Follower;
import com.momenty.user.domain.Following;
import com.momenty.user.domain.User;
import com.momenty.user.dto.request.FollowingCancelRequest;
import com.momenty.user.dto.request.FollowingRequest;
import com.momenty.user.dto.request.UserLoginRequest;
import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.dto.request.UserUpdateRequest;
import com.momenty.user.dto.response.FollowersResponse;
import com.momenty.user.dto.response.FollowingsResponse;
import com.momenty.user.dto.response.UserInfoResponse;
import com.momenty.user.dto.response.UserRegisterResponse;
import com.momenty.user.dto.response.UserSearchResponse;
import com.momenty.user.dto.response.UserStartDayResponse;
import com.momenty.user.dto.response.UserUpdateResponse;
import com.momenty.user.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        return returnCookieResponse(response, jwtStatus);
    }

    private ResponseEntity<Void> returnCookieResponse(HttpServletResponse response, JwtStatus jwtStatus) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", jwtStatus.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", jwtStatus.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> GeneralLogin (
            @Valid @RequestBody UserLoginRequest userLoginRequest,
            HttpServletResponse response
    ) {
        JwtStatus jwtStatus = userService.generalLogin(userLoginRequest);
        return returnCookieResponse(response, jwtStatus);
    }


    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register (
            @Valid @RequestBody UserRegisterRequest userRegisterRequest,
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        User user = userService.register(userRegisterRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserRegisterResponse.of(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserUpdateResponse> update (
            @Valid @RequestBody UserUpdateRequest userUpdateRequest,
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        User user = userService.update(userUpdateRequest, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserUpdateResponse.of(user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getInfo (
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        User user = userService.getInfo(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserInfoResponse.of(user));
    }

    @GetMapping("/nickname/check")
    public ResponseEntity<Void> checkNickname (
            @RequestParam(required = true) String nickname,
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        userService.checkNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/followings")
    public ResponseEntity<Void> followUser (
            @Valid @RequestBody FollowingRequest followingRequest,
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        userService.follow(followingRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/followings")
    public ResponseEntity<Void> cancelFollowing (
            @Valid @RequestBody FollowingCancelRequest followingCancelRequest,
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        userService.cancelFollowing(followingCancelRequest, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/followings")
    public ResponseEntity<FollowingsResponse> getFollowings (
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        List<Following> followings = userService.getFollowings(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(FollowingsResponse.of(followings));
    }

    @GetMapping("/followers")
    public ResponseEntity<FollowersResponse> getFollowers (
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        List<Follower> followers = userService.getFollowers(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(FollowersResponse.of(followers));
    }

    @GetMapping("/search")
    public ResponseEntity<UserSearchResponse> searchUser (
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email,
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        List<User> users = userService.searchUser(nickname, email);
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserSearchResponse.of(users));
    }

    @GetMapping("/start-days")
    public ResponseEntity<UserStartDayResponse> getUserStartDay (
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        LocalDateTime startDay = userService.getUserStartDay(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserStartDayResponse.from(startDay));
    }

    @DeleteMapping("/me")
    public ResponseEntity<UserStartDayResponse> deleteUser (
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
