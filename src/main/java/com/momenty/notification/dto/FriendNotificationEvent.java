package com.momenty.notification.dto;

import com.momenty.notification.domain.NotificationType;

public record FriendNotificationEvent(
        NotificationType notificationType,
        Integer requestUserId,
        Integer responseUserId
) {

}
