package com.momenty.notification.service;

import com.momenty.notification.domain.Notification;
import com.momenty.notification.domain.NotificationType;
import com.momenty.notification.domain.UserNotificationHistory;
import com.momenty.notification.domain.UserNotificationSetting;
import com.momenty.notification.domain.UserNotificationSettingId;
import com.momenty.notification.dto.NotificationTokenRequest;
import com.momenty.notification.dto.UpdateUserNotificationSettingRequest;
import com.momenty.notification.dto.UserNotificationHistoryUpdateRequest;
import com.momenty.notification.repository.NotificationRepository;
import com.momenty.notification.repository.NotificationTypeRepository;
import com.momenty.notification.repository.UserNotificationHistoryRepository;
import com.momenty.notification.repository.UserNotificationSettingRepository;
import com.momenty.record.domain.UserRecord;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private static final String EXPO_PUSH_API_URL = "https://exp.host/--/api/v2/push/send";

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final UserNotificationHistoryRepository userNotificationHistoryRepository;
    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void saveToken(NotificationTokenRequest notificationTokenRequest, Integer userId) {
        User user = userRepository.getById(userId);
        user.updateNotificationToken(notificationTokenRequest.token());
        log.info("user의 save token: {}", user.getNotificationToken());
    }

    public List<UserNotificationHistory> getUserNotificationHistory(Integer userId) {
        User user = userRepository.getById(userId);
        return userNotificationHistoryRepository.findAllByUser(user);
    }

    @Transactional
    public void readUserNotificationHistory(
            UserNotificationHistoryUpdateRequest userNotificationHistoryUpdateRequest
    ) {
        UserNotificationHistory notificationHistory =
                userNotificationHistoryRepository.getById(userNotificationHistoryUpdateRequest.userNotificationHistoryId());
        notificationHistory.readNotification();
    }

    public List<UserNotificationSetting> getUserNotificationSetting(Integer userId) {
        User user = userRepository.getById(userId);
        return userNotificationSettingRepository.findAllByUser(user);
    }

    @Transactional
    public void updateUserNotificationSetting(
            UpdateUserNotificationSettingRequest updateUserNotificationSettingRequest,
            Integer userId
    ) {
        UserNotificationSettingId userNotificationSettingId =
                UserNotificationSettingId.of(userId, updateUserNotificationSettingRequest.notificationTypeId());

        Optional<UserNotificationSetting> userNotificationSetting =
                userNotificationSettingRepository.findById(userNotificationSettingId);

        if (userNotificationSetting.isPresent() && !updateUserNotificationSettingRequest.isEnabled()) {
            userNotificationSettingRepository.deleteById(userNotificationSettingId);
        }

        if (userNotificationSetting.isEmpty() && updateUserNotificationSettingRequest.isEnabled()) {
            User user = userRepository.getById(userNotificationSettingId.getUser());
            NotificationType notificationType =
                    notificationTypeRepository.getById(userNotificationSettingId.getNotificationType());
            userNotificationSettingRepository.save(UserNotificationSetting.of(user, notificationType));
        }
    }

    @Transactional
    public void sendNotification(Integer userId, String userNickname, String token, Notification notification) {
        if (!StringUtils.hasText(token)) {
            log.info("푸시 토큰이 없는 사용자입니다. userId={}", userId);
            return;
        }

        String title = notification.getTitle();
        String content = writeNotification(notification, userNickname);
        String iconUrl = notification.getIconUrl();

        sendNotificationData(userId, token, title, content, iconUrl);
    }

    @Transactional
    protected void sendNotificationData(Integer userId, String token, String title, String content, String iconUrl) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("to", token);
        requestBody.put("title", title);
        requestBody.put("body", content);
        requestBody.put("sound", "default");
        requestBody.put("priority", "high");

        if (StringUtils.hasText(iconUrl)) {
            Map<String, Object> data = Map.of("icon", iconUrl);
            requestBody.put("data", data);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        log.info("푸시 요청 데이터: {}", requestBody);
        log.info("푸시 요청 헤더: {}", headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_API_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.warn("푸시 알림 전송 성공: status={}, response={}", response.getStatusCode(), response.getBody());
                addUserNotificationHistory(userId, title, content, iconUrl);
            } else {
                log.warn("푸시 알림 전송 실패: status={}, response={}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("푸시 알림 전송 중 오류 발생", e);
        }
    }

    @Transactional
    protected String writeNotification(Notification notification, String variable) {
        String type = notification.getNotificationType().getType();

        return switch (type) {
            case "친구신청", "기록알림", "팔로우", "기록작성알림" -> variable + notification.getContent();
            default -> notification.getContent();
        };
    }

    @Transactional
    public void addUserNotificationHistory(Integer userId, String title, String content, String iconUrl) {
        User user = userRepository.getById(userId);
        userNotificationHistoryRepository.save(UserNotificationHistory.create(user, title, content, iconUrl));
    }

    @Transactional
    public void sendRecordNotification(Integer userId, UserRecord record) {
        Optional<NotificationType> notificationTypeOptional = notificationTypeRepository.findByType("기록작성알림");
        if (notificationTypeOptional.isEmpty()) {
            return;
        }
        NotificationType notificationType = notificationTypeOptional.get();
        if (checkNotificationSetting(userId, notificationType)) {
            return;
        }
        Notification notification = notificationRepository.getByTypeWithNotificationType(notificationType);
        if (notification == null) {
            return;
        }

        User user = userRepository.getById(userId);
        String token = user.getNotificationToken();
        String recordTitle = record.getTitle();
        sendRecordNotificationData(userId, user.getNickname(), token, notification, recordTitle);
    }

    private void sendRecordNotificationData(
            Integer userId, String userNickname, String token, Notification notification, String recordTitle
    ) {
        if (!StringUtils.hasText(token)) {
            log.info("푸시 토큰이 없는 사용자입니다. userId={}", userId);
            return;
        }

        String title = notification.getTitle();
        String content = writeNotification(notification, userNickname + "님, " + recordTitle);
        String iconUrl = notification.getIconUrl();

        sendNotificationData(userId, token, title, content, iconUrl);
    }

    private boolean checkNotificationSetting(Integer userId, NotificationType notificationType) {
        Optional<UserNotificationSetting> userNotificationSetting =
                userNotificationSettingRepository.findById(
                        UserNotificationSettingId.of(userId, notificationType.getId())
                );

        return userNotificationSetting.isEmpty();
    }

    public List<Notification> getNotifications() {
        return notificationRepository.findAll();
    }
}
