package com.momenty.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotificationExceptionMessage {
    NOT_FOUND_NOTIFICATION_HISTORY("알림 기록을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;

    NotificationExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
