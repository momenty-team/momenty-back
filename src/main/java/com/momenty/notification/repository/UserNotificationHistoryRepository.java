package com.momenty.notification.repository;

import static com.momenty.notification.exception.NotificationExceptionMessage.*;

import com.momenty.global.exception.GlobalException;
import com.momenty.notification.domain.UserNotificationHistory;
import com.momenty.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface UserNotificationHistoryRepository extends Repository<UserNotificationHistory, Integer> {

    List<UserNotificationHistory> findAllByUser(User user);

    Optional<UserNotificationHistory> findById(Integer id);

    default UserNotificationHistory getById(Integer id) {
        return findById(id).orElseThrow(
                () -> new GlobalException(
                        NOT_FOUND_NOTIFICATION_HISTORY.getMessage(),
                        NOT_FOUND_NOTIFICATION_HISTORY.getStatus()
                )
        );
    }

    void save(UserNotificationHistory userNotificationHistory);
}
