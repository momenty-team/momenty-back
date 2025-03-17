package com.momenty.notification.event;

import com.momenty.notification.domain.Notification;
import com.momenty.notification.domain.NotificationType;
import com.momenty.notification.domain.UserNotificationSetting;
import com.momenty.notification.domain.UserNotificationSettingId;
import com.momenty.notification.dto.FriendNotificationEvent;
import com.momenty.notification.dto.NotificationEvent;
import com.momenty.notification.repository.NotificationRepository;
import com.momenty.notification.repository.UserNotificationSettingRepository;
import com.momenty.notification.service.NotificationService;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Async
    @EventListener
    public void sendNotification(NotificationEvent notificationEvent) {
        Integer userId = notificationEvent.userId();
        NotificationType notificationType = notificationEvent.notificationType();
        if (checkNotificationSetting(userId, notificationType)) {
            return;
        }

        User user = userRepository.getById(userId);
        String token = user.getNotificationToken();

        Notification notification = notificationRepository.getByTypeWithNotificationType(notificationType);
        notificationService.sendNotification(userId, user.getNickname(), token, notification);
    }

    @Async
    @EventListener
    public void sendNotification(FriendNotificationEvent notificationEvent) {
        Integer userId = notificationEvent.responseUserId();
        NotificationType notificationType = notificationEvent.notificationType();
        if (checkNotificationSetting(userId, notificationEvent.notificationType())) {
            return;
        }

        User user = userRepository.getById(userId);
        User requestUser = userRepository.getById(notificationEvent.requestUserId());
        String token = user.getNotificationToken();

        Notification notification = notificationRepository.getByTypeWithNotificationType(notificationType);
        if (notification == null) {
            return;
        }
        notificationService.sendNotification(userId, requestUser.getNickname(), token, notification);
    }

    private boolean checkNotificationSetting(Integer userId, NotificationType notificationType) {
        Optional<UserNotificationSetting> userNotificationSetting =
                userNotificationSettingRepository.findById(
                        UserNotificationSettingId.of(userId, notificationType.getId())
                );

        return userNotificationSetting.isEmpty();
    }
}
