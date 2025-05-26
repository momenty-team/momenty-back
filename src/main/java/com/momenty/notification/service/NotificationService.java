package com.momenty.notification.service;

import com.momenty.notification.domain.UserNotificationHistory;
import com.momenty.notification.dto.NotificationTokenRequest;
import com.momenty.notification.repository.UserNotificationRepository;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;

    @Transactional
    public void saveToken(NotificationTokenRequest notificationTokenRequest, Integer userId) {
        User user = userRepository.getById(userId);
        user.updateNotificationToken(notificationTokenRequest.token());
    }

    public List<UserNotificationHistory> getUserNotificationHistory(Integer userId) {
        User user = userRepository.getById(userId);
        return userNotificationRepository.findAllByUser(user);
    }
}
