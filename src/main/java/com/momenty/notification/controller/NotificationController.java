package com.momenty.notification.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.notification.domain.UserNotificationHistory;
import com.momenty.notification.domain.UserNotificationSetting;
import com.momenty.notification.dto.NotificationTokenRequest;
import com.momenty.notification.dto.UpdateUserNotificationSettingRequest;
import com.momenty.notification.dto.UserNotificationHistoryResponse;
import com.momenty.notification.dto.UserNotificationHistoryUpdateRequest;
import com.momenty.notification.dto.UserNotificationSettingResponse;
import com.momenty.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/token")
    public ResponseEntity<Void> saveNotificationToken(
            @RequestBody NotificationTokenRequest notificationTokenRequest,
            @Parameter(hidden = true) @UserId Integer userId,
            HttpServletRequest request
    ) {
        if (request.getCookies() != null) {
            String cookies = Arrays.stream(request.getCookies())
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                    .collect(Collectors.joining("; "));
            log.info("Request cookies: {}", cookies);
        } else {
            log.info("Request has no cookies");
        }

        log.info("Request token: {}", notificationTokenRequest.token());
        notificationService.saveToken(notificationTokenRequest, userId);
        log.info("저장");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user/history")
    public ResponseEntity<UserNotificationHistoryResponse> getUserNotificationHistory(
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        List<UserNotificationHistory> userNotificationHistories =
                notificationService.getUserNotificationHistory(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(UserNotificationHistoryResponse.of(userNotificationHistories));
    }

    @PutMapping("/user/history")
    public ResponseEntity<Void> updateUserNotificationHistory(
            @RequestBody UserNotificationHistoryUpdateRequest userNotificationHistoryUpdateRequest,
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        notificationService.readUserNotificationHistory(userNotificationHistoryUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user/setting")
    public ResponseEntity<UserNotificationSettingResponse> getUserNotificationSetting(
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        List<UserNotificationSetting> userNotificationSettings = notificationService.getUserNotificationSetting(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserNotificationSettingResponse.of(userNotificationSettings));
    }

    @PutMapping("/user/setting")
    public ResponseEntity<Void> updateUserNotificationSetting(
            @RequestBody UpdateUserNotificationSettingRequest updateUserNotificationSettingRequest,
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        notificationService.updateUserNotificationSetting(updateUserNotificationSettingRequest, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
