package com.momenty.notification.event;

import com.momenty.notification.domain.Notification;
import com.momenty.notification.domain.NotificationType;
import com.momenty.notification.domain.UserNotificationSetting;
import com.momenty.notification.domain.UserNotificationSettingId;
import com.momenty.notification.dto.NotificationEvent;
import com.momenty.notification.repository.NotificationRepository;
import com.momenty.notification.repository.UserNotificationSettingRepository;
import com.momenty.notification.service.NotificationService;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
        Optional<UserNotificationSetting> userNotificationSetting =
                userNotificationSettingRepository.findById(
                        UserNotificationSettingId.of(userId, notificationType.getId())
                );

        if (userNotificationSetting.isEmpty()) {
            return;
        }

        User user = userRepository.getById(userId);
        String token = user.getNotificationToken();

        Notification notification = notificationRepository.getByType(notificationType);
        Integer notificationId = notification.getId();
        String title = notification.getTitle();
        String content = notification.getContent();
        String iconUrl = notification.getIconUrl();

        notificationService.sendNotification(userId, notificationId, token, title, content, iconUrl);
    }
}
