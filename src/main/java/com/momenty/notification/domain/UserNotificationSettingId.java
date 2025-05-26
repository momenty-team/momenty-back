package com.momenty.notification.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class UserNotificationSettingId implements Serializable {

    private Integer user;
    private Integer notificationType;

    private UserNotificationSettingId(Integer user, Integer notificationType) {
        this.user = user;
        this.notificationType = notificationType;
    }

    protected UserNotificationSettingId(){};

    public static UserNotificationSettingId of(Integer user, Integer notificationType) {
        return new UserNotificationSettingId(user, notificationType);
    }
}
