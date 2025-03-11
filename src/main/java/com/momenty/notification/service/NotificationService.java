package com.momenty.notification.service;

import com.momenty.notification.dto.NotificationTokenRequest;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserRepository userRepository;

    @Transactional
    public void saveToken(NotificationTokenRequest notificationTokenRequest, Integer userId) {
        User user = userRepository.getById(userId);
        user.updateNotificationToken(notificationTokenRequest.token());
    }
}
