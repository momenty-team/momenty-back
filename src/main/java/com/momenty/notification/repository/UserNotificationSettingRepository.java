package com.momenty.notification.repository;

import com.momenty.notification.domain.UserNotificationSetting;
import com.momenty.notification.domain.UserNotificationSettingId;
import com.momenty.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface UserNotificationSettingRepository
        extends Repository<UserNotificationSetting, UserNotificationSettingId> {

    List<UserNotificationSetting> findAllByUser(User user);

    Optional<UserNotificationSetting> findById(UserNotificationSettingId userNotificationSettingId);

    void deleteById(UserNotificationSettingId userNotificationSettingId);

    void save(UserNotificationSetting userNotificationSetting);
}
