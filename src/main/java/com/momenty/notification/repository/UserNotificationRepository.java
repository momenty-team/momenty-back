package com.momenty.notification.repository;

import com.momenty.notification.domain.UserNotificationHistory;
import com.momenty.user.domain.User;
import java.util.List;
import org.springframework.data.repository.Repository;

public interface UserNotificationRepository extends Repository<UserNotificationHistory, Integer> {

    List<UserNotificationHistory> findAllByUser(User user);
}
