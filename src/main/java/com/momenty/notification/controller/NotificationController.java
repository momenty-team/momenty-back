package com.momenty.notification.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.notification.domain.UserNotificationHistory;
import com.momenty.notification.dto.NotificationTokenRequest;
import com.momenty.notification.dto.UserNotificationHistoryResponse;
import com.momenty.notification.dto.UserNotificationHistoryUpdateRequest;
import com.momenty.notification.service.NotificationService;
import java.util.List;
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
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/token")
    public ResponseEntity<Void> saveNotificationToken(
            @RequestBody NotificationTokenRequest notificationTokenRequest,
            @UserId Integer userId
    ) {
        notificationService.saveToken(notificationTokenRequest, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user/history")
    public ResponseEntity<UserNotificationHistoryResponse> getUserNotificationHistory(
            @UserId Integer userId
    ) {
        List<UserNotificationHistory> userNotificationHistories =
                notificationService.getUserNotificationHistory(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(UserNotificationHistoryResponse.from(userNotificationHistories));
    }

    @PutMapping("/user/history")
    public ResponseEntity<Void> updateUserNotificationHistory(
            @RequestBody UserNotificationHistoryUpdateRequest userNotificationHistoryUpdateRequest,
            @UserId Integer userId
    ) {
        notificationService.readUserNotificationHistory(userNotificationHistoryUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
