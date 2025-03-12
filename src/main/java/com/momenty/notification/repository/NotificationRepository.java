package com.momenty.notification.repository;

import static com.momenty.notification.exception.NotificationExceptionMessage.NOT_FOUND_NOTIFICATION;

import com.momenty.global.exception.GlobalException;
import com.momenty.notification.domain.Notification;
import com.momenty.notification.domain.NotificationType;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface NotificationRepository extends Repository<Notification, Integer> {

    Optional<Notification> findByNotificationType(NotificationType notificationType);

    Optional<Notification> findById(Integer id);

    default Notification getByType(NotificationType notificationType) {
        return findByNotificationType(notificationType).orElseThrow(
                () -> new GlobalException(
                        NOT_FOUND_NOTIFICATION.getMessage(),
                        NOT_FOUND_NOTIFICATION.getStatus()
                )
        );
    }

    default Notification getById(Integer id) {
        return findById(id).orElseThrow(
                () -> new GlobalException(
                        NOT_FOUND_NOTIFICATION.getMessage(),
                        NOT_FOUND_NOTIFICATION.getStatus()
                )
        );
    }
}
