package com.momenty.notification.dto;

import com.momenty.notification.domain.NotificationType;

public record NotificationEvent(
    NotificationType notificationType,
    Integer userId
) {

}
