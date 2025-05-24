package com.momenty.user.domain;

import static lombok.AccessLevel.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.momenty.notification.domain.UserNotificationHistory;
import com.momenty.notification.domain.UserNotificationSetting;
import com.momenty.record.domain.UserRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 100)
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 50)
    @Column(name = "nickname", length = 50, unique = true)
    private String nickname;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Size(max = 255)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 255)
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double weight;

    @Column(name = "is_public")
    private boolean isPublic = false;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follower> followers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Following> followings = new ArrayList<>();

    @Column(name = "notification_token")
    private String notificationToken;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = true)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserNotificationHistory> notificationHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserNotificationSetting> notificationSettings = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRecord> records = new ArrayList<>();

    @Builder
    private User(
            String name,
            String nickname,
            String email,
            LocalDate birthDate,
            String profileImageUrl,
            Gender gender,
            Double height,
            Double weight,
            boolean isPublic
    ) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.birthDate = birthDate;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.isPublic = isPublic;
    }

    public void updateProfile(
            String nickname,
            LocalDate birthDate,
            Gender gender,
            Boolean isPublic,
            String profileImageUrl
    ) {
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.gender = gender;
        this.isPublic = isPublic;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfileToRegister(
            String name,
            String nickname,
            LocalDate birthDate,
            Gender gender,
            Double height,
            Double weight
    ) {
        this.name = name;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
    }

    public void updateNotificationToken(
            String notificationToken
    ) {
        this.notificationToken = notificationToken;
    }
}
