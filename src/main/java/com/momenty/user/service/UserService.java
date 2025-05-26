package com.momenty.user.service;

import static com.momenty.global.auth.jwt.exception.TokenExceptionMessage.INVALID_TOKEN;
import static com.momenty.user.exception.UserExceptionMessage.*;

import com.momenty.global.auth.jwt.JwtTokenProvider;
import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.auth.jwt.repository.JwtStatusRedisRepository;
import com.momenty.global.auth.jwt.service.JwtService;
import com.momenty.global.auth.oauth.apple.domain.AppleUser;
import com.momenty.global.auth.oauth.apple.repository.AppleUserRepository;
import com.momenty.global.exception.GlobalException;
import com.momenty.notification.domain.NotificationType;
import com.momenty.notification.dto.FriendNotificationEvent;
import com.momenty.notification.repository.NotificationTypeRepository;
import com.momenty.user.domain.Follower;
import com.momenty.user.domain.Following;
import com.momenty.user.domain.User;
import com.momenty.user.dto.request.FollowingCancelRequest;
import com.momenty.user.dto.request.FollowingRequest;
import com.momenty.user.dto.request.UserLoginRequest;
import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.dto.request.UserUpdateRequest;
import com.momenty.user.repository.FollowerRepository;
import com.momenty.user.repository.FollowingRepository;
import com.momenty.user.repository.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    private final JwtStatusRedisRepository jwtStatusRedisRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final FollowingRepository followingRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AppleUserRepository appleUserRepository;

    @Transactional
    public JwtStatus generalRegister(
            UserRegisterRequest userRegisterRequest
    ) {
        User user = userRegisterRequest.toUser();
        User savedUser = userRepository.save(user);
        return generateJwt(savedUser.getId());
    }

    @Transactional
    public void register(
            User userInfo
    ) {
        userRepository.save(userInfo);
    }

    @Transactional
    public User register(
            UserRegisterRequest userRegisterRequest,
            Integer userId
    ) {
        User existingUser = userRepository.getById(userId);
        validNicknameDuplication(userRegisterRequest.nickname());
        userRegisterRequest.applyTo(existingUser);
        return existingUser;
    }

    private void validNicknameDuplication(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new GlobalException(DUPLICATION_NICKNAME.getMessage(), DUPLICATION_NICKNAME.getStatus());
        }
    }

    private JwtStatus generateJwt(Integer userId) {
        String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(userId));
        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(userId));

        JwtStatus jwtStatus = jwtService.createJwtStatus(userId, accessToken, refreshToken);
        return jwtStatusRedisRepository.save(jwtStatus);
    }

    @Transactional
    public User update(
            UserUpdateRequest userUpdateRequest,
            Integer userId
    ) {
        User existingUser = userRepository.getById(userId);
        validNicknameDuplication(userUpdateRequest.nickname());
        userUpdateRequest.applyTo(existingUser);
        return existingUser;
    }

    public User getInfo(Integer userId) {
        return userRepository.getById(userId);
    }

    public void checkNickname(String nickname) {
        validNicknameDuplication(nickname);
    }

    @Transactional
    public void follow(@Valid FollowingRequest followingRequest, Integer userId) {
        User user = userRepository.getById(userId);
        User followingUser = userRepository.getById(followingRequest.followingUserId());

        if (followingRepository.findByUserAndFollowingUser(user, followingUser).isPresent()) {
            throw new GlobalException(DUPLICATION_FOLLOWING.getMessage(), DUPLICATION_FOLLOWING.getStatus());
        }

        Following followingData = Following.builder()
                .user(user)
                .followingUser(followingUser)
                .build();
        followingRepository.save(followingData);

        Optional<Follower> existedFollowerData = followerRepository.findByUserAndFollowerUser(followingUser, user);
        if (existedFollowerData.isEmpty()) {
            Follower followerData = Follower.builder()
                    .user(followingUser)
                    .followerUser(user)
                    .build();
            followerRepository.save(followerData);
        }

        sendFollowNotification(userId, followingUser.getId());
    }

    private void sendFollowNotification(Integer userId, Integer followingUserId) {
        Optional<NotificationType> notificationType = notificationTypeRepository.findByType("팔로우");
        if (notificationType.isEmpty()) {
            return;
        }
        eventPublisher.publishEvent(new FriendNotificationEvent(notificationType.get(), userId, followingUserId));
    }

    @Transactional
    public void cancelFollowing(FollowingCancelRequest followingCancelRequest, Integer userId) {
        User user = userRepository.getById(userId);
        User followingCancelUser = userRepository.getById(followingCancelRequest.followingCancelUserId());

        Following followingData = followingRepository.getByUserAndFollowingUser(user, followingCancelUser);
        followingRepository.deleteById(followingData.getId());
    }

    public List<Following> getFollowings(Integer userId) {
        User user = userRepository.getById(userId);
        return followingRepository.findAllByUser(user);
    }

    public List<Follower> getFollowers(Integer userId) {
        User user = userRepository.getById(userId);
        return followerRepository.findAllByUser(user);
    }

    public List<User> searchUser(String nickname, String email) {
        if (nickname == null && email == null) {
            throw new GlobalException(NEED_NICKNAME_OR_EMAIL.getMessage(), NEED_NICKNAME_OR_EMAIL.getStatus());
        }

        if (nickname != null) {
            return userRepository.findAllByNicknameContaining(nickname);
        }
        return userRepository.findAllByEmailContaining(email);
    }

    public LocalDateTime getUserStartDay(Integer userId) {
        User user = userRepository.getById(userId);
        return user.getCreatedAt();
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.getById(userId);
        String email = user.getEmail();

        userRepository.deleteById(user.getId());

        Optional<AppleUser> appleData = appleUserRepository.findByEmail(email);
        appleData.ifPresent(appleUser -> appleUserRepository.deleteByEmail(appleUser.getEmail()));
    }

    @Transactional
    public JwtStatus generalLogin(UserLoginRequest userLoginRequest) {
        String accessToken = userLoginRequest.accessToken();
        User user = userRepository.getByNickname(userLoginRequest.nickname());

        if (accessToken == null) {
            throw new GlobalException(INVALID_TOKEN.getMessage(), INVALID_TOKEN.getStatus());
        }

        String tokenUserId = jwtTokenProvider.getUserIdEvenIfExpired(accessToken);
        if (!user.getId().equals(Integer.parseInt(tokenUserId))) {
            throw new GlobalException(INVALID_TOKEN.getMessage(), INVALID_TOKEN.getStatus());
        }

        if (jwtTokenProvider.isTokenExpired(accessToken)) {
            return jwtService.generateAndStoreJwt(user.getId());
        }

        return jwtStatusRedisRepository.getById(user.getId());
    }
}
