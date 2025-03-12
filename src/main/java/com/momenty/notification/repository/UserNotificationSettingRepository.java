package com.momenty.notification.repository;

import com.momenty.notification.domain.UserNotificationHistory;
import com.momenty.notification.domain.UserNotificationSetting;
import com.momenty.user.domain.User;
import java.util.List;
import org.springframework.data.repository.Repository;

public interface UserNotificationSettingRepository extends Repository<UserNotificationSetting, Integer> {

    List<UserNotificationSetting> findAllByUser(User user);
}
