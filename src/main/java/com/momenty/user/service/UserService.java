package com.momenty.user.service;

import com.momenty.global.auth.jwt.JwtTokenProvider;
import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.auth.jwt.repository.JwtStatusRedisRepository;
import com.momenty.global.auth.jwt.service.JwtService;
import com.momenty.global.auth.oauth.apple.dto.AppleAuthResponse;
import com.momenty.user.domain.User;
import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.repository.UserRedisRepository;
import com.momenty.user.repository.UserRepository;
import com.momenty.util.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final int CODE_LENGTH = 6;

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    private final JwtStatusRedisRepository jwtStatusRedisRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JwtStatus generalRegister(
            UserRegisterRequest userRegisterRequest
    ) {
        User user = userRegisterRequest.toUser(passwordEncoder);
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
        userRegisterRequest.applyTo(existingUser, passwordEncoder);
        return existingUser;
    }

    private JwtStatus generateJwt(Integer userId) {
        String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(userId));
        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(userId));

        JwtStatus jwtStatus = jwtService.createJwtStatus(userId, accessToken, refreshToken);
        return jwtStatusRedisRepository.save(jwtStatus);
    }
}
