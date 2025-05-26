package com.momenty.user.domain;

import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = PROTECTED)
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

    @Size(max = 255)
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 15)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

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

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Column(name = "follower_count", nullable = false)
    private Integer followerCount;

    @Column(name = "following_count", nullable = false)
    private Integer followingCount;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = true)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Builder
    private User(
            String name,
            String password,
            String nickname,
            String phoneNumber,
            String email,
            LocalDate birthDate,
            String profileImageUrl,
            Gender gender
    ) {
        this.name = name;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public void updateProfile(
            String name,
            String nickname,
            String encodedPassword,
            String phoneNumber,
            LocalDate birthDate,
            String email,
            String profileImageUrl,
            Gender gender
    ) {
        this.name = name;
        this.nickname = nickname;
        this.password = encodedPassword;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
    }
}
