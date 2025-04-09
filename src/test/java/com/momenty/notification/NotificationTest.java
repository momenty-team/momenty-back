package com.momenty.notification;

import com.momenty.notification.domain.NotificationType;
import com.momenty.notification.dto.FriendNotificationEvent;
import com.momenty.notification.dto.NotificationEvent;
import com.momenty.notification.repository.NotificationTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class NotificationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private NotificationTypeRepository notificationTypeRepository;

    @Test
    @Transactional(readOnly = false)
    public void testPublishNotificationEvent() {
        Integer userId = 8;
        NotificationType NotificationType = notificationTypeRepository.getById(2);

        NotificationEvent event = new NotificationEvent(NotificationType, userId);
        eventPublisher.publishEvent(event);
    }

    @Test
    @Transactional(readOnly = false)
    public void testPublishFriendNotificationEvent() {
        Integer userId = 8;
        Integer requestUserId = 6;
        NotificationType NotificationType = notificationTypeRepository.getById(1);

        FriendNotificationEvent event = new FriendNotificationEvent(NotificationType, requestUserId, userId);
        eventPublisher.publishEvent(event);
    }
}
