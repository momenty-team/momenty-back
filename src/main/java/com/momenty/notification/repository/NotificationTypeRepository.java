package com.momenty.notification.repository;

import static com.momenty.notification.exception.NotificationExceptionMessage.*;

import com.momenty.global.exception.GlobalException;
import com.momenty.notification.domain.NotificationType;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface NotificationTypeRepository extends Repository<NotificationType, Integer> {

    Optional<NotificationType> findById(Integer id);

    default NotificationType getById(Integer id) {
        return findById(id).orElseThrow(
                () -> new GlobalException(
                        NOT_FOUND_NOTIFICATION_TYPE.getMessage(),
                        NOT_FOUND_NOTIFICATION_TYPE.getStatus()
                )
        );
    }

    Optional<NotificationType> findByType(String type);
}
