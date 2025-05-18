package com.momenty.notification.repository;

import static com.momenty.notification.exception.NotificationExceptionMessage.NOT_FOUND_NOTIFICATION;

import com.momenty.global.exception.GlobalException;
import com.momenty.notification.domain.Notification;
import com.momenty.notification.domain.NotificationType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT n FROM Notification n JOIN FETCH n.notificationType WHERE n.notificationType = :notificationType")
    Optional<Notification> findByTypeWithNotificationType(@Param("notificationType") NotificationType notificationType);


    default Notification getByTypeWithNotificationType(NotificationType notificationType) {
        return findByTypeWithNotificationType(notificationType)
                .orElseThrow(() -> new GlobalException(
                        NOT_FOUND_NOTIFICATION.getMessage(),
                        NOT_FOUND_NOTIFICATION.getStatus()
                        )
                );
    }

    List<Notification> findAll();
}
