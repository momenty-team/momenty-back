package com.momenty.user.service;

import static com.momenty.user.exception.UserExceptionMessage.*;

import com.momenty.global.auth.jwt.JwtTokenProvider;
import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.auth.jwt.repository.JwtStatusRedisRepository;
import com.momenty.global.auth.jwt.service.JwtService;
import com.momenty.global.exception.GlobalException;
import com.momenty.user.domain.User;
import com.momenty.user.dto.request.NicknameCheckRequest;
import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.dto.request.UserUpdateRequest;
import com.momenty.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    public void checkNickname(NicknameCheckRequest nicknameCheckRequest) {
        validNicknameDuplication(nicknameCheckRequest.nickname());
    }
}
